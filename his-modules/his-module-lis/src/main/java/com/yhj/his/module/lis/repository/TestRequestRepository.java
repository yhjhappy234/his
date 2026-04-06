package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.enums.TestRequestStatus;
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
 * 检验申请Repository
 */
@Repository
public interface TestRequestRepository extends JpaRepository<TestRequest, String>, JpaSpecificationExecutor<TestRequest> {

    /**
     * 根据申请单号查询
     */
    Optional<TestRequest> findByRequestNo(String requestNo);

    /**
     * 根据患者ID查询
     */
    List<TestRequest> findByPatientId(String patientId);

    /**
     * 根据状态查询
     */
    List<TestRequest> findByStatus(TestRequestStatus status);

    /**
     * 根据就诊ID查询
     */
    List<TestRequest> findByVisitId(String visitId);

    /**
     * 根据申请时间范围查询
     */
    @Query("SELECT t FROM TestRequest t WHERE t.requestTime BETWEEN :startTime AND :endTime AND t.deleted = false")
    List<TestRequest> findByRequestTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据患者ID和状态查询
     */
    List<TestRequest> findByPatientIdAndStatusOrderByRequestTimeDesc(String patientId, TestRequestStatus status);

    /**
     * 查询急诊申请
     */
    @Query("SELECT t FROM TestRequest t WHERE t.emergency = true AND t.status NOT IN :excludedStatuses AND t.deleted = false ORDER BY t.requestTime ASC")
    List<TestRequest> findEmergencyRequests(@Param("excludedStatuses") List<TestRequestStatus> excludedStatuses);

    /**
     * 根据科室ID和时间范围查询
     */
    @Query("SELECT t FROM TestRequest t WHERE t.deptId = :deptId AND t.requestTime BETWEEN :startTime AND :endTime AND t.deleted = false")
    Page<TestRequest> findByDeptIdAndRequestTimeBetween(@Param("deptId") String deptId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    /**
     * 统计某状态的申请数量
     */
    @Query("SELECT COUNT(t) FROM TestRequest t WHERE t.status = :status AND t.deleted = false")
    long countByStatus(@Param("status") TestRequestStatus status);
}