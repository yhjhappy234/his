package com.yhj.his.module.lis.repository;

import com.yhj.his.module.lis.entity.CriticalValue;
import com.yhj.his.module.lis.enums.CriticalValueStatus;
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
 * 危急值记录Repository
 */
@Repository
public interface CriticalValueRepository extends JpaRepository<CriticalValue, String>, JpaSpecificationExecutor<CriticalValue> {

    /**
     * 根据申请ID查询
     */
    List<CriticalValue> findByRequestId(String requestId);

    /**
     * 根据患者ID查询
     */
    List<CriticalValue> findByPatientIdOrderByDetectTimeDesc(String patientId);

    /**
     * 根据结果ID查询
     */
    Optional<CriticalValue> findByResultId(String resultId);

    /**
     * 根据状态查询
     */
    List<CriticalValue> findByStatus(CriticalValueStatus status);

    /**
     * 查询待处理危急值
     */
    @Query("SELECT c FROM CriticalValue c WHERE c.status IN ('PENDING', 'NOTIFIED') AND c.deleted = false ORDER BY c.detectTime ASC")
    List<CriticalValue> findPendingCriticalValues();

    /**
     * 查询未确认危急值
     */
    @Query("SELECT c FROM CriticalValue c WHERE c.status = 'NOTIFIED' AND c.deleted = false")
    List<CriticalValue> findNotifiedCriticalValues();

    /**
     * 根据发现时间范围查询
     */
    @Query("SELECT c FROM CriticalValue c WHERE c.detectTime BETWEEN :startTime AND :endTime AND c.deleted = false")
    Page<CriticalValue> findByDetectTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, Pageable pageable);

    /**
     * 统计某状态的危急值数量
     */
    @Query("SELECT COUNT(c) FROM CriticalValue c WHERE c.status = :status AND c.deleted = false")
    long countByStatus(@Param("status") CriticalValueStatus status);

    /**
     * 统计患者未处理危急值数量
     */
    @Query("SELECT COUNT(c) FROM CriticalValue c WHERE c.patientId = :patientId AND c.status NOT IN ('HANDLED', 'CLOSED') AND c.deleted = false")
    long countPendingByPatientId(@Param("patientId") String patientId);
}