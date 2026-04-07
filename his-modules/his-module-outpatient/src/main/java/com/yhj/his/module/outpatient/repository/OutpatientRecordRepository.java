package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.OutpatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊病历Repository
 */
@Repository
public interface OutpatientRecordRepository extends JpaRepository<OutpatientRecord, String>, JpaSpecificationExecutor<OutpatientRecord> {

    /**
     * 根据挂号ID查询病历
     */
    Optional<OutpatientRecord> findByRegistrationId(String registrationId);

    /**
     * 根据患者ID查询病历列表(按就诊日期倒序)
     */
    List<OutpatientRecord> findByPatientIdOrderByVisitDateDesc(String patientId);

    /**
     * 根据患者ID和日期范围查询病历列表
     */
    List<OutpatientRecord> findByPatientIdAndVisitDateBetween(String patientId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据医生ID和日期查询病历列表
     */
    List<OutpatientRecord> findByDoctorIdAndVisitDate(String doctorId, LocalDate visitDate);

    /**
     * 根据状态查询病历列表
     */
    List<OutpatientRecord> findByStatus(String status);

    /**
     * 查询患者历史病历(最近N次)
     */
    @Query("SELECT r FROM OutpatientRecord r WHERE r.patientId = :patientId AND r.status = '已提交' ORDER BY r.visitDate DESC LIMIT :limit")
    List<OutpatientRecord> findRecentRecords(@Param("patientId") String patientId, @Param("limit") int limit);
}