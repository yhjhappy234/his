package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.OutpatientPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊处方Repository
 */
@Repository
public interface OutpatientPrescriptionRepository extends JpaRepository<OutpatientPrescription, String>, JpaSpecificationExecutor<OutpatientPrescription> {

    /**
     * 根据处方号查询
     */
    Optional<OutpatientPrescription> findByPrescriptionNo(String prescriptionNo);

    /**
     * 根据挂号ID查询处方列表
     */
    List<OutpatientPrescription> findByRegistrationId(String registrationId);

    /**
     * 根据患者ID查询处方列表
     */
    List<OutpatientPrescription> findByPatientIdOrderByPrescriptionDateDesc(String patientId);

    /**
     * 根据医生ID和日期查询处方列表
     */
    List<OutpatientPrescription> findByDoctorIdAndPrescriptionDate(String doctorId, LocalDate prescriptionDate);

    /**
     * 根据收费状态查询处方列表
     */
    List<OutpatientPrescription> findByPayStatus(String payStatus);

    /**
     * 根据挂号ID和收费状态查询处方列表
     */
    List<OutpatientPrescription> findByRegistrationIdAndPayStatus(String registrationId, String payStatus);

    /**
     * 根据状态查询处方列表
     */
    List<OutpatientPrescription> findByStatus(String status);
}