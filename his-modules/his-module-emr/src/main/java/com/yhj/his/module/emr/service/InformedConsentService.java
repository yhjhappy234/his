package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.InformedConsentSaveDTO;
import com.yhj.his.module.emr.dto.SignatureDTO;
import com.yhj.his.module.emr.entity.InformedConsent;
import com.yhj.his.module.emr.enums.ConsentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 知情同意书服务接口
 */
public interface InformedConsentService {

    /**
     * 创建知情同意书
     */
    InformedConsent createConsent(InformedConsentSaveDTO dto);

    /**
     * 更新知情同意书
     */
    InformedConsent updateConsent(String id, InformedConsentSaveDTO dto);

    /**
     * 删除知情同意书
     */
    void deleteConsent(String id);

    /**
     * 根据ID获取知情同意书
     */
    InformedConsent getConsentById(String id);

    /**
     * 根据住院ID查询知情同意书列表
     */
    List<InformedConsent> getConsentsByAdmissionId(String admissionId);

    /**
     * 根据就诊ID查询知情同意书列表
     */
    List<InformedConsent> getConsentsByVisitId(String visitId);

    /**
     * 根据患者ID查询知情同意书列表
     */
    List<InformedConsent> getConsentsByPatientId(String patientId);

    /**
     * 分页查询知情同意书
     */
    Page<InformedConsent> listConsents(Pageable pageable);

    /**
     * 根据同意书类型查询
     */
    Page<InformedConsent> getConsentsByType(ConsentType consentType, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<InformedConsent> getConsentsByStatus(String status, Pageable pageable);

    /**
     * 根据住院ID和同意书类型查询
     */
    Optional<InformedConsent> getConsentByAdmissionIdAndType(String admissionId, ConsentType consentType);

    /**
     * 查询待签署的同意书
     */
    List<InformedConsent> getPendingConsentsByAdmissionId(String admissionId);

    /**
     * 根据患者姓名模糊查询
     */
    Page<InformedConsent> searchByPatientName(String patientName, Pageable pageable);

    /**
     * 根据医生ID查询
     */
    Page<InformedConsent> getConsentsByDoctorId(String doctorId, Pageable pageable);

    /**
     * 医生签名
     */
    InformedConsent doctorSign(SignatureDTO dto);

    /**
     * 患者签名
     */
    InformedConsent patientSign(SignatureDTO dto);

    /**
     * 代理人签名
     */
    InformedConsent agentSign(SignatureDTO dto);

    /**
     * 拒绝签署
     */
    InformedConsent refuseSign(String id, String reason);

    /**
     * 从模板创建知情同意书
     */
    InformedConsent createFromTemplate(String templateId, InformedConsentSaveDTO dto);

    /**
     * 为手术创建知情同意书
     */
    InformedConsent createForOperation(String operationId, ConsentType consentType, InformedConsentSaveDTO dto);
}