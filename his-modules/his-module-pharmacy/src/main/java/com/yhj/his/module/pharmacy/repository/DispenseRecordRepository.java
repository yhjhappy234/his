package com.yhj.his.module.pharmacy.repository;

import com.yhj.his.module.pharmacy.entity.DispenseRecord;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.enums.VisitType;
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
 * 发药记录Repository
 */
@Repository
public interface DispenseRecordRepository extends JpaRepository<DispenseRecord, String>, JpaSpecificationExecutor<DispenseRecord> {

    /**
     * 根据发药单号查询
     */
    Optional<DispenseRecord> findByDispenseNo(String dispenseNo);

    /**
     * 根据处方ID查询
     */
    Optional<DispenseRecord> findByPrescriptionId(String prescriptionId);

    /**
     * 根据处方号查询
     */
    Optional<DispenseRecord> findByPrescriptionNo(String prescriptionNo);

    /**
     * 根据患者ID查询
     */
    List<DispenseRecord> findByPatientId(String patientId);

    /**
     * 根据药房ID查询
     */
    List<DispenseRecord> findByPharmacyId(String pharmacyId);

    /**
     * 查询待发药记录
     */
    @Query("SELECT d FROM DispenseRecord d WHERE d.pharmacyId = :pharmacyId AND d.dispenseStatus = 'PENDING' AND d.deleted = false ORDER BY d.createTime ASC")
    List<DispenseRecord> findPendingDispense(@Param("pharmacyId") String pharmacyId);

    /**
     * 查询待审核记录
     */
    @Query("SELECT d FROM DispenseRecord d WHERE d.pharmacyId = :pharmacyId AND d.auditStatus = 'PENDING' AND d.deleted = false ORDER BY d.createTime ASC")
    List<DispenseRecord> findPendingAudit(@Param("pharmacyId") String pharmacyId);

    /**
     * 综合查询发药记录
     */
    @Query("SELECT d FROM DispenseRecord d WHERE d.deleted = false " +
           "AND (:pharmacyId IS NULL OR d.pharmacyId = :pharmacyId) " +
           "AND (:patientId IS NULL OR d.patientId = :patientId) " +
           "AND (:prescriptionNo IS NULL OR d.prescriptionNo = :prescriptionNo) " +
           "AND (:auditStatus IS NULL OR d.auditStatus = :auditStatus) " +
           "AND (:dispenseStatus IS NULL OR d.dispenseStatus = :dispenseStatus) " +
           "AND (:visitType IS NULL OR d.visitType = :visitType) " +
           "AND (:startTime IS NULL OR d.createTime >= :startTime) " +
           "AND (:endTime IS NULL OR d.createTime <= :endTime) " +
           "ORDER BY d.createTime DESC")
    Page<DispenseRecord> queryDispenseRecords(@Param("pharmacyId") String pharmacyId,
                                               @Param("patientId") String patientId,
                                               @Param("prescriptionNo") String prescriptionNo,
                                               @Param("auditStatus") AuditStatus auditStatus,
                                               @Param("dispenseStatus") DispenseStatus dispenseStatus,
                                               @Param("visitType") VisitType visitType,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               Pageable pageable);

    /**
     * 查询最新发药单号
     */
    @Query("SELECT MAX(d.dispenseNo) FROM DispenseRecord d WHERE d.dispenseNo LIKE :prefix")
    String findMaxDispenseNo(@Param("prefix") String prefix);

    /**
     * 根据处方ID和发药状态查询
     */
    Optional<DispenseRecord> findByPrescriptionIdAndDispenseStatus(String prescriptionId, DispenseStatus dispenseStatus);

    /**
     * 根据药房ID和审核状态查询
     */
    List<DispenseRecord> findByPharmacyIdAndAuditStatus(String pharmacyId, AuditStatus auditStatus);

    /**
     * 根据药房ID和审核状态和发药状态查询
     */
    List<DispenseRecord> findByPharmacyIdAndAuditStatusAndDispenseStatus(String pharmacyId, AuditStatus auditStatus, DispenseStatus dispenseStatus);

    /**
     * 根据患者ID查询(按创建时间降序)
     */
    @Query("SELECT d FROM DispenseRecord d WHERE d.patientId = :patientId AND d.deleted = false ORDER BY d.createTime DESC")
    List<DispenseRecord> findByPatientIdOrderByCreateTimeDesc(@Param("patientId") String patientId);

    /**
     * 综合查询发药记录(简化版)
     */
    @Query("SELECT d FROM DispenseRecord d WHERE d.deleted = false " +
           "AND (:pharmacyId IS NULL OR d.pharmacyId = :pharmacyId) " +
           "AND (:patientId IS NULL OR d.patientId = :patientId) " +
           "AND (:prescriptionNo IS NULL OR d.prescriptionNo = :prescriptionNo) " +
           "AND (:auditStatus IS NULL OR d.auditStatus = :auditStatus) " +
           "AND (:dispenseStatus IS NULL OR d.dispenseStatus = :dispenseStatus) " +
           "AND (:startDate IS NULL OR d.createTime >= :startDate) " +
           "AND (:endDate IS NULL OR d.createTime <= :endDate) " +
           "ORDER BY d.createTime DESC")
    Page<DispenseRecord> queryRecords(@Param("pharmacyId") String pharmacyId,
                                      @Param("patientId") String patientId,
                                      @Param("prescriptionNo") String prescriptionNo,
                                      @Param("auditStatus") AuditStatus auditStatus,
                                      @Param("dispenseStatus") DispenseStatus dispenseStatus,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);
}