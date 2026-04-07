package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Schedule;
import com.yhj.his.module.hr.enums.ScheduleType;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务接口
 */
public interface ScheduleService {

    /**
     * 创建排班
     */
    ScheduleVO createSchedule(ScheduleCreateDTO dto);

    /**
     * 更新排班
     */
    ScheduleVO updateSchedule(String scheduleId, ScheduleCreateDTO dto);

    /**
     * 删除排班
     */
    void deleteSchedule(String scheduleId);

    /**
     * 获取排班详情
     */
    ScheduleVO getScheduleById(String scheduleId);

    /**
     * 批量创建排班
     */
    List<ScheduleVO> batchCreateSchedule(BatchScheduleDTO dto);

    /**
     * 根据员工和日期获取排班
     */
    ScheduleVO getScheduleByEmployeeAndDate(String employeeId, LocalDate scheduleDate);

    /**
     * 根据员工和日期范围获取排班列表
     */
    List<ScheduleVO> listSchedulesByEmployeeAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据科室和日期范围获取排班列表
     */
    List<ScheduleVO> listSchedulesByDeptAndDateRange(String deptId, LocalDate startDate, LocalDate endDate);

    /**
     * 分页查询排班
     */
    PageResult<ScheduleVO> listSchedules(String deptId, String employeeId, ScheduleType scheduleType, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);

    /**
     * 调整排班（换班）
     */
    ScheduleVO adjustSchedule(String scheduleId, String newEmployeeId, String reason);
}