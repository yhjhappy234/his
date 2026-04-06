package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Attendance;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.Schedule;
import com.yhj.his.module.hr.enums.AttendanceStatus;
import com.yhj.his.module.hr.enums.ScheduleType;
import com.yhj.his.module.hr.repository.AttendanceRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.ScheduleRepository;
import com.yhj.his.module.hr.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考勤服务实现
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleRepository scheduleRepository;

    private static final int LATE_THRESHOLD_MINUTES = 15;
    private static final int EARLY_LEAVE_THRESHOLD_MINUTES = 15;

    @Override
    @Transactional
    public AttendanceVO clock(AttendanceClockDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        LocalDate attendanceDate = dto.getAttendanceDate() != null ? dto.getAttendanceDate() : LocalDate.now();
        LocalTime clockTime = dto.getClockTime() != null ? dto.getClockTime() : LocalTime.now();

        // 查找或创建考勤记录
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDateAndDeletedFalse(
                dto.getEmployeeId(), attendanceDate)
                .orElseGet(() -> createNewAttendance(employee, attendanceDate));

        // 设置签到时间
        attendance.setClockInTime(clockTime);

        // 判断考勤状态
        determineAttendanceStatus(attendance, clockTime, true);

        attendance = attendanceRepository.save(attendance);
        return convertToVO(attendance);
    }

    @Override
    @Transactional
    public AttendanceVO clockOut(AttendanceClockDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        LocalDate attendanceDate = dto.getAttendanceDate() != null ? dto.getAttendanceDate() : LocalDate.now();
        LocalTime clockTime = dto.getClockTime() != null ? dto.getClockTime() : LocalTime.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDateAndDeletedFalse(
                dto.getEmployeeId(), attendanceDate)
                .orElseThrow(() -> new BusinessException("未找到签到记录，请先签到"));

        // 设置签退时间
        attendance.setClockOutTime(clockTime);

        // 判断考勤状态
        determineAttendanceStatus(attendance, clockTime, false);

        // 计算加班时长
        if (attendance.getScheduleEnd() != null && clockTime.isAfter(attendance.getScheduleEnd())) {
            long overtimeMinutes = calculateMinutes(attendance.getScheduleEnd(), clockTime);
            attendance.setOvertimeHours(BigDecimal.valueOf(overtimeMinutes / 60.0));
        }

        attendance = attendanceRepository.save(attendance);
        return convertToVO(attendance);
    }

    @Override
    public AttendanceVO getAttendanceById(String attendanceId) {
        Attendance attendance = attendanceRepository.findByIdAndDeletedFalse(attendanceId)
                .orElseThrow(() -> new BusinessException("考勤记录不存在"));
        return convertToVO(attendance);
    }

    @Override
    public AttendanceVO getAttendanceByEmployeeAndDate(String employeeId, LocalDate attendanceDate) {
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDateAndDeletedFalse(
                employeeId, attendanceDate).orElse(null);
        return attendance != null ? convertToVO(attendance) : null;
    }

    @Override
    public List<AttendanceVO> listAttendancesByEmployeeAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> list = attendanceRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<AttendanceVO> listAttendances(String deptId, String employeeId, AttendanceStatus status, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize) {
        Page<Attendance> page = attendanceRepository.findByConditions(deptId, employeeId, status, startDate, endDate, PageUtils.of(pageNum, pageSize));
        List<AttendanceVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public AttendanceStatisticsVO getAttendanceStatistics(String employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        AttendanceStatisticsVO vo = new AttendanceStatisticsVO();
        vo.setEmployeeId(employeeId);
        vo.setEmployeeNo(employee.getEmployeeNo());
        vo.setEmployeeName(employee.getEmployeeName());
        vo.setDeptName(employee.getDeptName());
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);

        // 获取考勤记录列表
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);

        // 计算统计数据
        int shouldWorkDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        vo.setShouldWorkDays(shouldWorkDays);

        int actualWorkDays = (int) attendances.stream()
                .filter(a -> a.getClockInTime() != null)
                .count();
        vo.setActualWorkDays(actualWorkDays);

        vo.setLateTimes(attendanceRepository.countLateTimes(employeeId, startDate, endDate));
        vo.setEarlyLeaveTimes(attendanceRepository.countEarlyLeaveTimes(employeeId, startDate, endDate));

        // 统计迟到总分钟
        int totalLateMinutes = attendances.stream()
                .filter(a -> a.getLateMinutes() != null && a.getLateMinutes() > 0)
                .mapToInt(Attendance::getLateMinutes)
                .sum();
        vo.setTotalLateMinutes(totalLateMinutes);

        // 统计早退总分钟
        int totalEarlyMinutes = attendances.stream()
                .filter(a -> a.getEarlyMinutes() != null && a.getEarlyMinutes() > 0)
                .mapToInt(Attendance::getEarlyMinutes)
                .sum();
        vo.setTotalEarlyMinutes(totalEarlyMinutes);

        // 统计旷工天数
        int absentDays = shouldWorkDays - actualWorkDays;
        vo.setAbsentDays(absentDays);

        // 统计加班时长
        Double overtimeHours = attendanceRepository.sumOvertimeHours(employeeId, startDate, endDate);
        vo.setOvertimeHours(overtimeHours != null ? overtimeHours : 0.0);

        // 统计正常打卡次数
        long normalTimes = attendances.stream()
                .filter(a -> a.getAttendanceStatus() == AttendanceStatus.NORMAL)
                .count();
        vo.setNormalTimes(normalTimes);

        return vo;
    }

    @Override
    @Transactional
    public List<AttendanceVO> generateAttendances(LocalDate startDate, LocalDate endDate, String deptId) {
        List<AttendanceVO> result = new ArrayList<>();

        // 获取排班列表
        List<Schedule> schedules = scheduleRepository.findByDeptIdAndDateRange(deptId, startDate, endDate);

        for (Schedule schedule : schedules) {
            // 检查是否已存在考勤记录
            if (attendanceRepository.findByEmployeeIdAndAttendanceDateAndDeletedFalse(
                    schedule.getEmployeeId(), schedule.getScheduleDate()).isPresent()) {
                continue;
            }

            Employee employee = employeeRepository.findByIdAndDeletedFalse(schedule.getEmployeeId())
                    .orElse(null);
            if (employee == null) {
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setId(IdUtil.fastSimpleUUID());
            attendance.setEmployeeId(schedule.getEmployeeId());
            attendance.setEmployeeNo(schedule.getEmployeeNo());
            attendance.setEmployeeName(schedule.getEmployeeName());
            attendance.setDeptId(schedule.getDeptId());
            attendance.setDeptName(schedule.getDeptName());
            attendance.setAttendanceDate(schedule.getScheduleDate());
            attendance.setScheduleType(schedule.getScheduleType());
            attendance.setScheduleStart(schedule.getStartTime());
            attendance.setScheduleEnd(schedule.getEndTime());
            attendance.setScheduleId(schedule.getId());
            attendance.setAttendanceStatus(AttendanceStatus.ABSENT);

            attendance = attendanceRepository.save(attendance);
            result.add(convertToVO(attendance));
        }

        return result;
    }

    private Attendance createNewAttendance(Employee employee, LocalDate date) {
        Attendance attendance = new Attendance();
        attendance.setId(IdUtil.fastSimpleUUID());
        attendance.setEmployeeId(employee.getId());
        attendance.setEmployeeNo(employee.getEmployeeNo());
        attendance.setEmployeeName(employee.getEmployeeName());
        attendance.setDeptId(employee.getDeptId());
        attendance.setDeptName(employee.getDeptName());
        attendance.setAttendanceDate(date);

        // 获取当天排班信息
        Schedule schedule = scheduleRepository.findByEmployeeIdAndScheduleDateAndDeletedFalse(
                employee.getId(), date).orElse(null);
        if (schedule != null) {
            attendance.setScheduleType(schedule.getScheduleType());
            attendance.setScheduleStart(schedule.getStartTime());
            attendance.setScheduleEnd(schedule.getEndTime());
            attendance.setScheduleId(schedule.getId());
        }

        return attendance;
    }

    private void determineAttendanceStatus(Attendance attendance, LocalTime clockTime, boolean isClockIn) {
        // 如果是签到
        if (isClockIn) {
            if (attendance.getScheduleStart() != null) {
                long lateMinutes = calculateMinutes(attendance.getScheduleStart(), clockTime);
                if (lateMinutes > LATE_THRESHOLD_MINUTES) {
                    attendance.setAttendanceStatus(AttendanceStatus.LATE);
                    attendance.setLateMinutes((int) lateMinutes);
                } else {
                    attendance.setAttendanceStatus(AttendanceStatus.NORMAL);
                }
            } else {
                attendance.setAttendanceStatus(AttendanceStatus.NORMAL);
            }
        } else {
            // 如果是签退
            if (attendance.getScheduleEnd() != null) {
                long earlyMinutes = calculateMinutes(clockTime, attendance.getScheduleEnd());
                if (earlyMinutes > EARLY_LEAVE_THRESHOLD_MINUTES) {
                    attendance.setAttendanceStatus(AttendanceStatus.EARLY_LEAVE);
                    attendance.setEarlyMinutes((int) earlyMinutes);
                } else if (attendance.getAttendanceStatus() != AttendanceStatus.LATE) {
                    attendance.setAttendanceStatus(AttendanceStatus.NORMAL);
                }
            }
        }
    }

    private long calculateMinutes(LocalTime start, LocalTime end) {
        return java.time.Duration.between(start, end).toMinutes();
    }

    private AttendanceVO convertToVO(Attendance attendance) {
        AttendanceVO vo = new AttendanceVO();
        BeanUtil.copyProperties(attendance, vo);

        if (attendance.getScheduleType() != null) {
            vo.setScheduleType(attendance.getScheduleType().name());
        }
        if (attendance.getAttendanceStatus() != null) {
            vo.setAttendanceStatus(attendance.getAttendanceStatus().name());
        }
        if (attendance.getLeaveType() != null) {
            vo.setLeaveType(attendance.getLeaveType().name());
        }

        return vo;
    }
}