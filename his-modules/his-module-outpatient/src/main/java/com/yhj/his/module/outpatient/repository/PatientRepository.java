package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 患者信息Repository
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, String>, JpaSpecificationExecutor<Patient> {

    /**
     * 根据患者ID查询
     */
    Optional<Patient> findByPatientId(String patientId);

    /**
     * 根据身份证号查询
     */
    Optional<Patient> findByIdCardNo(String idCardNo);

    /**
     * 根据医保卡号查询
     */
    Optional<Patient> findByMedicalInsuranceNo(String medicalInsuranceNo);

    /**
     * 根据手机号查询
     */
    Optional<Patient> findByPhone(String phone);

    /**
     * 检查患者ID是否存在
     */
    boolean existsByPatientId(String patientId);

    /**
     * 检查身份证号是否存在
     */
    boolean existsByIdCardNo(String idCardNo);
}