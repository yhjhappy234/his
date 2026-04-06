package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.TestResult;
import com.yhj.his.module.lis.enums.ResultFlag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 检验结果Repository
 */
@Repository
public interface TestResultRepository extends JpaRepository<TestResult, String>, JpaSpecificationExecutor<TestResult> {

    /**
     * 根据申请ID查询所有结果
     */
    List<TestResult> findByRequestId(String requestId);

    /**
     * 根据样本ID查询所有结果
     */
    List<TestResult> findBySampleId(String sampleId);

    /**
     * 根据申请ID和项目ID查询
     */
    @Query("SELECT t FROM TestResult t WHERE t.requestId = :requestId AND t.itemId = :itemId AND t.deleted = false")
    Optional<TestResult> findByRequestIdAndItemId(@Param("requestId") String requestId, @Param("itemId") String itemId);

    /**
     * 根据项目ID查询
     */
    List<TestResult> findByItemId(String itemId);

    /**
     * 查询危急值结果
     */
    @Query("SELECT t FROM TestResult t WHERE t.criticalFlag = true AND t.deleted = false")
    List<TestResult> findByCriticalFlagTrue();

    /**
     * 查询异常结果
     */
    @Query("SELECT t FROM TestResult t WHERE t.abnormalFlag = true AND t.deleted = false")
    List<TestResult> findByAbnormalFlagTrue();

    /**
     * 查询待审核结果
     */
    @Query("SELECT t FROM TestResult t WHERE t.auditorId IS NULL AND t.testTime IS NOT NULL AND t.deleted = false")
    List<TestResult> findPendingAuditResults();

    /**
     * 根据检测时间范围查询
     */
    @Query("SELECT t FROM TestResult t WHERE t.testTime BETWEEN :startTime AND :endTime AND t.deleted = false")
    Page<TestResult> findByTestTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    /**
     * 根据患者ID查询历史结果
     */
    @Query("SELECT t FROM TestResult t JOIN TestRequest r ON t.requestId = r.id WHERE r.patientId = :patientId AND t.itemId = :itemId AND t.deleted = false ORDER BY t.testTime DESC")
    List<TestResult> findHistoryResultsByPatientIdAndItemId(@Param("patientId") String patientId, @Param("itemId") String itemId);

    /**
     * 统计申请的结果数量
     */
    @Query("SELECT COUNT(t) FROM TestResult t WHERE t.requestId = :requestId AND t.deleted = false")
    long countByRequestId(@Param("requestId") String requestId);
}