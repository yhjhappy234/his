package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.ProgressRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
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
 * 病程记录Repository
 */
@Repository
public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, String>, JpaSpecificationExecutor<ProgressRecord> {

    /**
     * 根据住院ID查询病程记录列表
     */
    List<ProgressRecord> findByAdmissionIdAndDeletedFalseOrderByRecordDateDescRecordTimeDesc(String admissionId);

    /**
     * 根据住院ID和记录类型查询
     */
    List<ProgressRecord> findByAdmissionIdAndRecordTypeAndDeletedFalseOrderByRecordDateDesc(
            String admissionId, ProgressRecordType recordType);

    /**
     * 根据住院ID和日期查询
     */
    List<ProgressRecord> findByAdmissionIdAndRecordDateAndDeletedFalse(String admissionId, LocalDate recordDate);

    /**
     * 根据患者ID查询
     */
    Page<ProgressRecord> findByPatientIdAndDeletedFalse(String patientId, Pageable pageable);

    /**
     * 根据医生ID查询
     */
    Page<ProgressRecord> findByDoctorIdAndDeletedFalse(String doctorId, Pageable pageable);

    /**
     * 查询首次病程记录
     */
    Optional<ProgressRecord> findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
            String admissionId, ProgressRecordType recordType);

    /**
     * 根据状态查询
     */
    Page<ProgressRecord> findByStatusAndDeletedFalse(EmrStatus status, Pageable pageable);

    /**
     * 查询住院期间的病程记录
     */
    @Query("SELECT p FROM ProgressRecord p WHERE p.admissionId = :admissionId " +
           "AND p.recordDate >= :startDate AND p.recordDate <= :endDate AND p.deleted = false " +
           "ORDER BY p.recordDate DESC, p.recordTime DESC")
    List<ProgressRecord> findByAdmissionIdAndDateRange(
            @Param("admissionId") String admissionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计住院期间的病程记录数
     */
    @Query("SELECT COUNT(p) FROM ProgressRecord p WHERE p.admissionId = :admissionId AND p.deleted = false")
    Long countByAdmissionId(@Param("admissionId") String admissionId);

    /**
     * 查询待审核的病程记录
     */
    @Query("SELECT p FROM ProgressRecord p WHERE p.admissionId = :admissionId " +
           "AND p.status = :status AND p.deleted = false")
    List<ProgressRecord> findByAdmissionIdAndStatus(
            @Param("admissionId") String admissionId, @Param("status") EmrStatus status);
}