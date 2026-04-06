package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Attendance;
import com.yhj.his.module.hr.enums.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤服务接口
 */
public interface AttendanceService {

    /**
     * 打卡
     */
    AttendanceVO clock(AttendanceClockDTO dto);

    /**
     * 签退
     */
    AttendanceVO clockOut(AttendanceClockDTO dto);

    /**
     * 获取考勤详情
     */
    AttendanceVO getAttendanceById(String attendanceId);

    /**
     * 根据员工和日期获取考勤记录
     */
    AttendanceVO getAttendanceByEmployeeAndDate(String employeeId, LocalDate attendanceDate);

    /**
     * 根据员工和日期范围获取考勤列表
     */
    List<AttendanceVO> listAttendancesByEmployeeAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询考勤
     */
    PageResult<AttendanceVO> listAttendances(String deptId, String employeeId, AttendanceStatus status, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);

    /**
     * 获取考勤统计
     */
    AttendanceStatisticsVO getAttendanceStatistics(String employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 批量生成考勤记录（根据排班）
     */
    List<AttendanceVO> generateAttendances(LocalDate startDate, LocalDate endDate, String deptId);
}