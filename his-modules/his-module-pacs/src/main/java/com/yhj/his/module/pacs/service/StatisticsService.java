package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.pacs.vo.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务接口
 */
public interface StatisticsService {

    /**
     * 检查工作量统计(按日期)
     */
    Map<String, Object> getExamWorkloadByDate(LocalDate startDate, LocalDate endDate);

    /**
     * 检查工作量统计(按检查类型)
     */
    Map<String, Object> getExamWorkloadByType(LocalDate startDate, LocalDate endDate);

    /**
     * 检查工作量统计(按机房)
     */
    Map<String, Object> getExamWorkloadByRoom(LocalDate startDate, LocalDate endDate);

    /**
     * 设备使用率统计
     */
    Map<String, Object> getEquipmentUsageRate(LocalDate startDate, LocalDate endDate);

    /**
     * 报告时限统计
     */
    Map<String, Object> getReportTurnaroundStats(LocalDate startDate, LocalDate endDate);

    /**
     * 阳性率统计
     */
    Map<String, Object> getPositiveRateStats(LocalDate startDate, LocalDate endDate);

    /**
     * 今日工作量概览
     */
    Map<String, Object> getTodayOverview();

    /**
     * 待处理任务统计
     */
    Map<String, Object> getPendingTaskStats();

    /**
     * 申请状态分布
     */
    List<Map<String, Object>> getRequestStatusDistribution();

    /**
     * 报告状态分布
     */
    List<Map<String, Object>> getReportStatusDistribution();

    /**
     * 急诊检查统计
     */
    Map<String, Object> getEmergencyExamStats(LocalDate startDate, LocalDate endDate);
}