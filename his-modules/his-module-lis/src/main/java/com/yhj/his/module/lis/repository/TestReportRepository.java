package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.TestReport;
import com.yhj.his.module.lis.enums.TestReportStatus;
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
 * 检验报告Repository
 */
@Repository
public interface TestReportRepository extends JpaRepository<TestReport, String>, JpaSpecificationExecutor<TestReport> {

    /**
     * 根据报告编号查询
     */
    Optional<TestReport> findByReportNo(String reportNo);

    /**
     * 根据申请ID查询
     */
    Optional<TestReport> findByRequestId(String requestId);

    /**
     * 根据患者ID查询
     */
    List<TestReport> findByPatientIdOrderByReportTimeDesc(String patientId);

    /**
     * 根据状态查询
     */
    List<TestReport> findByStatus(TestReportStatus status);

    /**
     * 查询待审核报告
     */
    @Query("SELECT t FROM TestReport t WHERE t.status = 'PENDING_AUDIT' AND t.deleted = false ORDER BY t.reportTime ASC")
    List<TestReport> findPendingAuditReports();

    /**
     * 查询危急值报告
     */
    @Query("SELECT t FROM TestReport t WHERE t.criticalReport = true AND t.status NOT IN :excludedStatuses AND t.deleted = false")
    List<TestReport> findCriticalReports(@Param("excludedStatuses") List<TestReportStatus> excludedStatuses);

    /**
     * 根据报告时间范围查询
     */
    @Query("SELECT t FROM TestReport t WHERE t.reportTime BETWEEN :startTime AND :endTime AND t.deleted = false")
    Page<TestReport> findByReportTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    /**
     * 根据患者ID和状态查询
     */
    List<TestReport> findByPatientIdAndStatus(String patientId, TestReportStatus status);

    /**
     * 统计某状态的报告数量
     */
    @Query("SELECT COUNT(t) FROM TestReport t WHERE t.status = :status AND t.deleted = false")
    long countByStatus(@Param("status") TestReportStatus status);
}