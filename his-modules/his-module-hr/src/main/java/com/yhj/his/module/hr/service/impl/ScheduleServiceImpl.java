package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Department;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.Schedule;
import com.yhj.his.module.hr.entity.ScheduleTemplate;
import com.yhj.his.module.hr.enums.ScheduleType;
import com.yhj.his.module.hr.repository.DepartmentRepository;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.ScheduleRepository;
import com.yhj.his.module.hr.repository.ScheduleTemplateRepository;
import com.yhj.his.module.hr.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排班服务实现
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ScheduleTemplateRepository templateRepository;

    @Override
    @Transactional
    public ScheduleVO createSchedule(ScheduleCreateDTO dto) {
        // 检查员工是否存在
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 检查科室是否存在
        Department dept = departmentRepository.findByIdAndDeletedFalse(dto.getDeptId())
                .orElseThrow(() -> new BusinessException("科室不存在"));

        // 检查是否已有排班
        scheduleRepository.findByEmployeeIdAndScheduleDateAndDeletedFalse(dto.getEmployeeId(), dto.getScheduleDate())
                .ifPresent(s -> { throw new BusinessException("该员工当天已有排班"); });

        Schedule schedule = new Schedule();
        BeanUtil.copyProperties(dto, schedule);
        schedule.setId(IdUtil.fastSimpleUUID());
        schedule.setEmployeeNo(employee.getEmployeeNo());
        schedule.setEmployeeName(employee.getEmployeeName());
        schedule.setDeptName(dept.getDeptName());

        // 设置班次类型
        ScheduleType scheduleType = ScheduleType.valueOf(dto.getScheduleType());
        schedule.setScheduleType(scheduleType);

        // 设置默认时间
        if (dto.getStartTime() == null) {
            schedule.setStartTime(parseTime(scheduleType.getStartTime()));
        }
        if (dto.getEndTime() == null) {
            schedule.setEndTime(parseTime(scheduleType.getEndTime()));
        }

        schedule = scheduleRepository.save(schedule);
        return convertToVO(schedule);
    }

    @Override
    @Transactional
    public ScheduleVO updateSchedule(String scheduleId, ScheduleCreateDTO dto) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        BeanUtil.copyProperties(dto, schedule, "id", "createTime", "deleted");

        // 更新班次类型
        if (dto.getScheduleType() != null) {
            ScheduleType scheduleType = ScheduleType.valueOf(dto.getScheduleType());
            schedule.setScheduleType(scheduleType);

            if (dto.getStartTime() == null) {
                schedule.setStartTime(parseTime(scheduleType.getStartTime()));
            }
            if (dto.getEndTime() == null) {
                schedule.setEndTime(parseTime(scheduleType.getEndTime()));
            }
        }

        schedule = scheduleRepository.save(schedule);
        return convertToVO(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        schedule.setDeleted(true);
        scheduleRepository.save(schedule);
    }

    @Override
    public ScheduleVO getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));
        return convertToVO(schedule);
    }

    @Override
    @Transactional
    public List<ScheduleVO> batchCreateSchedule(BatchScheduleDTO dto) {
        List<ScheduleVO> result = new ArrayList<>();

        // 获取员工列表
        List<Employee> employees;
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            employees = dto.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findByIdAndDeletedFalse(id)
                            .orElseThrow(() -> new BusinessException("员工不存在: " + id)))
                    .collect(Collectors.toList());
        } else {
            throw new BusinessException("请指定员工列表");
        }

        // 验证日期范围
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        // 遍历日期范围
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

        for (Employee employee : employees) {
            for (int i = 0; i < days; i++) {
                LocalDate date = dto.getStartDate().plusDays(i);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                int weekDay = dayOfWeek.getValue(); // 1-7

                // 查找对应星期的排班配置
                BatchScheduleDTO.DayScheduleConfig config = dto.getDayConfigs().stream()
                        .filter(c -> c.getWeekDay().equals(weekDay))
                        .findFirst()
                        .orElse(null);

                if (config != null && !config.getScheduleType().equals("REST")) {
                    ScheduleCreateDTO scheduleDTO = new ScheduleCreateDTO();
                    scheduleDTO.setEmployeeId(employee.getId());
                    scheduleDTO.setDeptId(employee.getDeptId());
                    scheduleDTO.setScheduleDate(date);
                    scheduleDTO.setScheduleType(config.getScheduleType());
                    scheduleDTO.setStartTime(parseTime(config.getStartTime()));
                    scheduleDTO.setEndTime(parseTime(config.getEndTime()));
                    scheduleDTO.setTemplateId(dto.getTemplateId());

                    try {
                        ScheduleVO vo = createSchedule(scheduleDTO);
                        result.add(vo);
                    } catch (BusinessException e) {
                        // 跳过已有排班的日期
                    }
                }
            }
        }

        return result;
    }

    @Override
    public ScheduleVO getScheduleByEmployeeAndDate(String employeeId, LocalDate scheduleDate) {
        Schedule schedule = scheduleRepository.findByEmployeeIdAndScheduleDateAndDeletedFalse(employeeId, scheduleDate)
                .orElse(null);
        return schedule != null ? convertToVO(schedule) : null;
    }

    @Override
    public List<ScheduleVO> listSchedulesByEmployeeAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = scheduleRepository.findByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVO> listSchedulesByDeptAndDateRange(String deptId, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = scheduleRepository.findByDeptIdAndDateRange(deptId, startDate, endDate);
        return schedules.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleVO> listSchedules(String deptId, String employeeId, ScheduleType scheduleType, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize) {
        Page<Schedule> page = scheduleRepository.findByConditions(deptId, employeeId, scheduleType, startDate, endDate, PageUtils.of(pageNum, pageSize));
        List<ScheduleVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public ScheduleVO adjustSchedule(String scheduleId, String newEmployeeId, String reason) {
        Schedule schedule = scheduleRepository.findByIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new BusinessException("排班不存在"));

        // 检查新员工是否存在
        Employee newEmployee = employeeRepository.findByIdAndDeletedFalse(newEmployeeId)
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 检查新员工当天是否已有排班
        scheduleRepository.findByEmployeeIdAndScheduleDateAndDeletedFalse(newEmployeeId, schedule.getScheduleDate())
                .ifPresent(s -> { throw new BusinessException("新员工当天已有排班"); });

        // 更换员工
        schedule.setEmployeeId(newEmployeeId);
        schedule.setEmployeeNo(newEmployee.getEmployeeNo());
        schedule.setEmployeeName(newEmployee.getEmployeeName());
        schedule.setRemark("换班原因: " + reason);

        schedule = scheduleRepository.save(schedule);
        return convertToVO(schedule);
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr);
    }

    private ScheduleVO convertToVO(Schedule schedule) {
        ScheduleVO vo = new ScheduleVO();
        BeanUtil.copyProperties(schedule, vo);

        // 设置班次类型名称
        if (schedule.getScheduleType() != null) {
            vo.setScheduleType(schedule.getScheduleType().name());
        }

        // 设置星期几
        if (schedule.getScheduleDate() != null) {
            vo.setWeekDay(schedule.getScheduleDate().getDayOfWeek().getValue());
        }

        return vo;
    }
}