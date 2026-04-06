package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.NursingRecord;
import com.yhj.his.module.inpatient.enums.NursingRecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 护理记录Repository
 */
@Repository
public interface NursingRecordRepository extends JpaRepository<NursingRecord, String> {

    /**
     * 根据住院ID查询护理记录
     */
    List<NursingRecord> findByAdmissionId(String admissionId);

    /**
     * 根据住院ID分页查询护理记录
     */
    Page<NursingRecord> findByAdmissionId(String admissionId, Pageable pageable);

    /**
     * 根据住院ID和记录类型查询
     */
    List<NursingRecord> findByAdmissionIdAndRecordType(String admissionId, NursingRecordType recordType);

    /**
     * 根据患者ID查询护理记录
     */
    List<NursingRecord> findByPatientId(String patientId);

    /**
     * 根据记录时间范围查询
     */
    @Query("SELECT n FROM NursingRecord n WHERE n.admissionId = :admissionId AND n.recordTime BETWEEN :startTime AND :endTime ORDER BY n.recordTime")
    List<NursingRecord> findByAdmissionIdAndRecordTimeBetween(
            @Param("admissionId") String admissionId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最后一次生命体征记录
     */
    @Query("SELECT n FROM NursingRecord n WHERE n.admissionId = :admissionId AND n.recordType = 'VITAL_SIGNS' ORDER BY n.recordTime DESC LIMIT 1")
    NursingRecord findLastVitalSigns(@Param("admissionId") String admissionId);

    /**
     * 统计住院护理记录数量
     */
    @Query("SELECT COUNT(n) FROM NursingRecord n WHERE n.admissionId = :admissionId")
    Long countByAdmissionId(@Param("admissionId") String admissionId);
}