package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.InformedConsent;
import com.yhj.his.module.emr.enums.ConsentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 知情同意书Repository
 */
@Repository
public interface InformedConsentRepository extends JpaRepository<InformedConsent, String>, JpaSpecificationExecutor<InformedConsent> {

    /**
     * 根据住院ID查询
     */
    List<InformedConsent> findByAdmissionIdAndDeletedFalse(String admissionId);

    /**
     * 根据就诊ID查询
     */
    List<InformedConsent> findByVisitIdAndDeletedFalse(String visitId);

    /**
     * 根据患者ID查询
     */
    List<InformedConsent> findByPatientIdAndDeletedFalse(String patientId);

    /**
     * 根据同意书类型查询
     */
    Page<InformedConsent> findByConsentTypeAndDeletedFalse(ConsentType consentType, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<InformedConsent> findByStatusAndDeletedFalse(String status, Pageable pageable);

    /**
     * 根据住院ID和同意书类型查询
     */
    Optional<InformedConsent> findByAdmissionIdAndConsentTypeAndDeletedFalse(
            String admissionId, ConsentType consentType);

    /**
     * 根据住院ID和状态查询
     */
    List<InformedConsent> findByAdmissionIdAndStatusAndDeletedFalse(String admissionId, String status);

    /**
     * 查询待签署的同意书
     */
    List<InformedConsent> findByAdmissionIdAndStatus(String admissionId, String status);

    /**
     * 根据患者姓名模糊查询
     */
    Page<InformedConsent> findByPatientNameContainingAndDeletedFalse(String patientName, Pageable pageable);

    /**
     * 根据医生ID查询
     */
    Page<InformedConsent> findByDoctorIdAndDeletedFalse(String doctorId, Pageable pageable);
}