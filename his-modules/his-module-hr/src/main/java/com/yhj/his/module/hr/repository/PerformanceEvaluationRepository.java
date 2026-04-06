package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.PerformanceEvaluation;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 绩效评分Repository
 */
@Repository
public interface PerformanceEvaluationRepository extends JpaRepository<PerformanceEvaluation, String>, JpaSpecificationExecutor<PerformanceEvaluation> {

    /**
     * 根据考核单号查找绩效评分
     */
    Optional<PerformanceEvaluation> findByEvaluationNo(String evaluationNo);

    /**
     * 根据员工ID查找绩效评分列表
     */
    List<PerformanceEvaluation> findByEmployeeIdAndDeletedFalseOrderByPeriodStartDesc(String employeeId);

    /**
     * 根据员工ID和考核周期查找绩效评分
     */
    Optional<PerformanceEvaluation> findByEmployeeIdAndPeriodStartAndPeriodEndAndDeletedFalse(
            String employeeId, LocalDate periodStart, LocalDate periodEnd);

    /**
     * 根据审批状态查找绩效评分列表
     */
    List<PerformanceEvaluation> findByApproveStatusAndDeletedFalseOrderByEvaluateTimeDesc(ApprovalStatus approveStatus);

    /**
     * 根据审核人ID查找待审核绩效评分
     */
    @Query("SELECT p FROM PerformanceEvaluation p WHERE p.deleted = false " +
           "AND p.approverId = :approverId " +
           "AND p.approveStatus = 'PENDING' " +
           "ORDER BY p.evaluateTime ASC")
    List<PerformanceEvaluation> findPendingByApprover(@Param("approverId") String approverId);

    /**
     * 分页查询绩效评分
     */
    @Query("SELECT p FROM PerformanceEvaluation p WHERE p.deleted = false " +
           "AND (:employeeId IS NULL OR p.employeeId = :employeeId) " +
           "AND (:deptId IS NULL OR p.deptId = :deptId) " +
           "AND (:approveStatus IS NULL OR p.approveStatus = :approveStatus) " +
           "AND (:evaluationType IS NULL OR p.evaluationType = :evaluationType) " +
           "AND (:periodStart IS NULL OR p.periodStart >= :periodStart) " +
           "AND (:periodEnd IS NULL OR p.periodEnd <= :periodEnd)")
    Page<PerformanceEvaluation> findByConditions(@Param("employeeId") String employeeId,
                                                  @Param("deptId") String deptId,
                                                  @Param("approveStatus") ApprovalStatus approveStatus,
                                                  @Param("evaluationType") String evaluationType,
                                                  @Param("periodStart") LocalDate periodStart,
                                                  @Param("periodEnd") LocalDate periodEnd,
                                                  Pageable pageable);

    /**
     * 查询员工某考核周期的绩效评分
     */
    @Query("SELECT p FROM PerformanceEvaluation p WHERE p.deleted = false " +
           "AND p.employeeId = :employeeId " +
           "AND p.evaluationType = :evaluationType " +
           "AND p.periodStart <= :date " +
           "AND p.periodEnd >= :date")
    Optional<PerformanceEvaluation> findByEmployeeAndTypeAndDate(@Param("employeeId") String employeeId,
                                                                  @Param("evaluationType") String evaluationType,
                                                                  @Param("date") LocalDate date);

    /**
     * 检查考核单号是否存在
     */
    boolean existsByEvaluationNo(String evaluationNo);

    /**
     * 根据ID查找未删除的绩效评分
     */
    Optional<PerformanceEvaluation> findByIdAndDeletedFalse(String id);
}