package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 绩效评分服务接口
 */
public interface PerformanceEvaluationService {

    /**
     * 创建绩效评分
     */
    PerformanceEvaluation createPerformanceEvaluation(PerformanceEvaluation evaluation);

    /**
     * 更新绩效评分
     */
    PerformanceEvaluation updatePerformanceEvaluation(PerformanceEvaluation evaluation);

    /**
     * 根据ID删除绩效评分（逻辑删除）
     */
    void deletePerformanceEvaluation(String id);

    /**
     * 根据ID获取绩效评分
     */
    Optional<PerformanceEvaluation> getPerformanceEvaluationById(String id);

    /**
     * 根据考核单号获取绩效评分
     */
    Optional<PerformanceEvaluation> getPerformanceEvaluationByNo(String evaluationNo);

    /**
     * 根据员工ID获取绩效评分列表
     */
    List<PerformanceEvaluation> getPerformanceEvaluationsByEmployeeId(String employeeId);

    /**
     * 根据员工ID和考核周期获取绩效评分
     */
    Optional<PerformanceEvaluation> getPerformanceEvaluationByEmployeeAndPeriod(
            String employeeId, LocalDate periodStart, LocalDate periodEnd);

    /**
     * 根据审批状态获取绩效评分列表
     */
    List<PerformanceEvaluation> getPerformanceEvaluationsByApproveStatus(ApprovalStatus approveStatus);

    /**
     * 根据审核人ID获取待审核绩效评分
     */
    List<PerformanceEvaluation> getPendingPerformanceEvaluationsByApprover(String approverId);

    /**
     * 分页查询绩效评分
     */
    Page<PerformanceEvaluation> searchPerformanceEvaluations(String employeeId, String deptId,
                                                              ApprovalStatus approveStatus, String evaluationType,
                                                              LocalDate periodStart, LocalDate periodEnd,
                                                              Pageable pageable);

    /**
     * 查询员工某考核周期的绩效评分
     */
    Optional<PerformanceEvaluation> getPerformanceEvaluationByEmployeeAndTypeAndDate(
            String employeeId, String evaluationType, LocalDate date);

    /**
     * 审核绩效评分
     */
    PerformanceEvaluation approvePerformanceEvaluation(String evaluationId, ApprovalStatus approveStatus,
                                                         String approverId, String approverName, String approveRemark);

    /**
     * 计算绩效总分
     */
    BigDecimal calculateTotalScore(String evaluationId);

    /**
     * 根据分数确定考核等级
     */
    String determineEvaluationLevel(BigDecimal totalScore);

    /**
     * 批量创建绩效评分
     */
    List<PerformanceEvaluation> batchCreatePerformanceEvaluations(List<String> employeeIds,
                                                                    LocalDate periodStart, LocalDate periodEnd,
                                                                    String evaluationType);

    /**
     * 检查考核单号是否存在
     */
    boolean existsByEvaluationNo(String evaluationNo);

    /**
     * 生成考核单号
     */
    String generateEvaluationNo();

    /**
     * 获取员工最近一次绩效评分
     */
    Optional<PerformanceEvaluation> getLatestPerformanceEvaluation(String employeeId);
}