package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.IndicatorType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 绩效服务接口
 */
public interface PerformanceService {

    /**
     * 创建绩效评分
     */
    PerformanceEvaluationVO createEvaluation(PerformanceEvaluationCreateDTO dto, String evaluatorId, String evaluatorName);

    /**
     * 审核绩效评分
     */
    PerformanceEvaluationVO approveEvaluation(PerformanceApprovalDTO dto, String approverId, String approverName);

    /**
     * 获取绩效评分详情
     */
    PerformanceEvaluationVO getEvaluationById(String evaluationId);

    /**
     * 根据员工获取绩效评分列表
     */
    List<PerformanceEvaluationVO> listEvaluationsByEmployee(String employeeId);

    /**
     * 获取待审核绩效评分列表
     */
    List<PerformanceEvaluationVO> listPendingEvaluations(String approverId);

    /**
     * 分页查询绩效评分
     */
    PageResult<PerformanceEvaluationVO> listEvaluations(String employeeId, String deptId, ApprovalStatus approveStatus, String evaluationType, LocalDate periodStart, LocalDate periodEnd, Integer pageNum, Integer pageSize);

    /**
     * 计算绩效总分
     */
    void calculateTotalScore(String evaluationId);

    /**
     * 确定绩效等级
     */
    String determinePerformanceLevel(BigDecimal totalScore);
}