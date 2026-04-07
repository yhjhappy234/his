package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.ScheduleCreateRequest;
import com.yhj.his.module.outpatient.entity.Schedule;
import com.yhj.his.module.outpatient.vo.ScheduleVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排班服务接口
 */
public interface ScheduleService {

    /**
     * 创建排班
     */
    ScheduleVO createSchedule(ScheduleCreateRequest request);

    /**
     * 更新排班
     */
    ScheduleVO updateSchedule(String id, ScheduleCreateRequest request);

    /**
     * 根据ID查询排班
     */
    Optional<Schedule> findById(String id);

    /**
     * 获取排班详情VO
     */
    ScheduleVO getScheduleDetail(String id);

    /**
     * 分页查询排班列表
     */
    PageResult<ScheduleVO> listSchedules(String deptId, String doctorId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询某日排班列表
     */
    List<ScheduleVO> listSchedulesByDate(LocalDate date);

    /**
     * 查询科室某日排班列表
     */
    List<ScheduleVO> listSchedulesByDeptAndDate(String deptId, LocalDate date);

    /**
     * 查询医生排班列表(日期范围)
     */
    List<ScheduleVO> listSchedulesByDoctor(String doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * 删除排班
     */
    void deleteSchedule(String id);

    /**
     * 停诊
     */
    ScheduleVO stopSchedule(String id, String reason);

    /**
     * 恢复排班
     */
    ScheduleVO restoreSchedule(String id);

    /**
     * 更新号源数量
     */
    boolean updateQuota(String scheduleId, int totalQuota);

    /**
     * 查询可预约排班
     */
    List<ScheduleVO> listAvailableSchedules(String deptId, LocalDate date);
}