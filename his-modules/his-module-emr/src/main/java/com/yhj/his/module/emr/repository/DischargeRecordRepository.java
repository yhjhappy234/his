package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.DischargeRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 出院记录Repository
 */
@Repository
public interface DischargeRecordRepository extends JpaRepository<DischargeRecord, String>, JpaSpecificationExecutor<DischargeRecord> {

    /**
     * 根据住院ID查询
     */
    Optional<DischargeRecord> findByAdmissionIdAndDeletedFalse(String admissionId);

    /**
     * 根据患者ID查询出院记录列表
     */
    List<DischargeRecord> findByPatientIdAndDeletedFalseOrderByDischargeDateDesc(String patientId);

    /**
     * 根据科室查询
     */
    Page<DischargeRecord> findByDeptIdAndDeletedFalse(String deptId, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<DischargeRecord> findByStatusAndDeletedFalse(EmrStatus status, Pageable pageable);

    /**
     * 查询患者最新出院记录
     */
    Optional<DischargeRecord> findFirstByPatientIdAndDeletedFalseOrderByDischargeDateDesc(String patientId);

    /**
     * 根据科室和状态查询
     */
    Page<DischargeRecord> findByDeptIdAndStatusAndDeletedFalse(String deptId, EmrStatus status, Pageable pageable);

    /**
     * 根据患者姓名模糊查询
     */
    Page<DischargeRecord> findByPatientNameContainingAndDeletedFalse(String patientName, Pageable pageable);
}