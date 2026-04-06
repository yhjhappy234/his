package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.AdmissionRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 入院记录Repository
 */
@Repository
public interface AdmissionRecordRepository extends JpaRepository<AdmissionRecord, String>, JpaSpecificationExecutor<AdmissionRecord> {

    /**
     * 根据住院ID查询
     */
    Optional<AdmissionRecord> findByAdmissionIdAndDeletedFalse(String admissionId);

    /**
     * 根据患者ID查询入院记录列表
     */
    List<AdmissionRecord> findByPatientIdAndDeletedFalseOrderByAdmissionDateDesc(String patientId);

    /**
     * 根据科室查询
     */
    Page<AdmissionRecord> findByDeptIdAndDeletedFalse(String deptId, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<AdmissionRecord> findByStatusAndDeletedFalse(EmrStatus status, Pageable pageable);

    /**
     * 根据科室和状态查询
     */
    Page<AdmissionRecord> findByDeptIdAndStatusAndDeletedFalse(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 查询患者最新入院记录
     */
    Optional<AdmissionRecord> findFirstByPatientIdAndDeletedFalseOrderByAdmissionDateDesc(String patientId);

    /**
     * 根据患者姓名模糊查询
     */
    Page<AdmissionRecord> findByPatientNameContainingAndDeletedFalse(String patientName, Pageable pageable);
}