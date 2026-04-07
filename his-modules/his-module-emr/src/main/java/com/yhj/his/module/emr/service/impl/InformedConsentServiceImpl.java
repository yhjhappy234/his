package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.InformedConsentSaveDTO;
import com.yhj.his.module.emr.dto.SignatureDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.entity.InformedConsent;
import com.yhj.his.module.emr.enums.ConsentType;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.repository.InformedConsentRepository;
import com.yhj.his.module.emr.service.EmrTemplateService;
import com.yhj.his.module.emr.service.InformedConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 知情同意书服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InformedConsentServiceImpl implements InformedConsentService {

    private final InformedConsentRepository consentRepository;
    private final EmrTemplateRepository templateRepository;
    private final EmrTemplateService templateService;

    @Override
    @Transactional
    public InformedConsent createConsent(InformedConsentSaveDTO dto) {
        InformedConsent consent = new InformedConsent();
        mapDtoToEntity(dto, consent);
        consent.setStatus("待签署");
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public InformedConsent updateConsent(String id, InformedConsentSaveDTO dto) {
        InformedConsent consent = getConsentById(id);
        if (!"待签署".equals(consent.getStatus())) {
            throw new RuntimeException("只有待签署状态的知情同意书可以修改");
        }
        mapDtoToEntity(dto, consent);
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public void deleteConsent(String id) {
        InformedConsent consent = getConsentById(id);
        if (!"待签署".equals(consent.getStatus())) {
            throw new RuntimeException("只有待签署状态的知情同意书可以删除");
        }
        consent.setDeleted(true);
        consentRepository.save(consent);
    }

    @Override
    public InformedConsent getConsentById(String id) {
        return consentRepository.findById(id)
                .filter(c -> !c.getDeleted())
                .orElseThrow(() -> new RuntimeException("知情同意书不存在: " + id));
    }

    @Override
    public List<InformedConsent> getConsentsByAdmissionId(String admissionId) {
        return consentRepository.findByAdmissionIdAndDeletedFalse(admissionId);
    }

    @Override
    public List<InformedConsent> getConsentsByVisitId(String visitId) {
        return consentRepository.findByVisitIdAndDeletedFalse(visitId);
    }

    @Override
    public List<InformedConsent> getConsentsByPatientId(String patientId) {
        return consentRepository.findByPatientIdAndDeletedFalse(patientId);
    }

    @Override
    public Page<InformedConsent> listConsents(Pageable pageable) {
        Page<InformedConsent> page = consentRepository.findAll(pageable);
        List<InformedConsent> filtered = page.getContent().stream()
                .filter(c -> !c.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Page<InformedConsent> getConsentsByType(ConsentType consentType, Pageable pageable) {
        return consentRepository.findByConsentTypeAndDeletedFalse(consentType, pageable);
    }

    @Override
    public Page<InformedConsent> getConsentsByStatus(String status, Pageable pageable) {
        return consentRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Optional<InformedConsent> getConsentByAdmissionIdAndType(String admissionId, ConsentType consentType) {
        return consentRepository.findByAdmissionIdAndConsentTypeAndDeletedFalse(admissionId, consentType);
    }

    @Override
    public List<InformedConsent> getPendingConsentsByAdmissionId(String admissionId) {
        return consentRepository.findByAdmissionIdAndStatusAndDeletedFalse(admissionId, "待签署");
    }

    @Override
    public Page<InformedConsent> searchByPatientName(String patientName, Pageable pageable) {
        return consentRepository.findByPatientNameContainingAndDeletedFalse(patientName, pageable);
    }

    @Override
    public Page<InformedConsent> getConsentsByDoctorId(String doctorId, Pageable pageable) {
        return consentRepository.findByDoctorIdAndDeletedFalse(doctorId, pageable);
    }

    @Override
    @Transactional
    public InformedConsent doctorSign(SignatureDTO dto) {
        InformedConsent consent = getConsentById(dto.getConsentId());
        consent.setDoctorSignature(dto.getDoctorSignature());
        consent.setSignTime(LocalDateTime.now());
        checkAndCompleteSigning(consent);
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public InformedConsent patientSign(SignatureDTO dto) {
        InformedConsent consent = getConsentById(dto.getConsentId());
        consent.setPatientSignature(dto.getPatientSignature());
        consent.setPatientSignTime(LocalDateTime.now());
        checkAndCompleteSigning(consent);
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public InformedConsent agentSign(SignatureDTO dto) {
        InformedConsent consent = getConsentById(dto.getConsentId());
        consent.setAgentName(dto.getAgentName());
        consent.setAgentRelation(dto.getAgentRelation());
        consent.setAgentIdCard(dto.getAgentIdCard());
        consent.setAgentSignature(dto.getAgentSignature());
        consent.setAgentSignTime(LocalDateTime.now());
        checkAndCompleteSigning(consent);
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public InformedConsent refuseSign(String id, String reason) {
        InformedConsent consent = getConsentById(id);
        consent.setStatus("已拒绝");
        consent.setRefuseReason(reason);
        return consentRepository.save(consent);
    }

    @Override
    @Transactional
    public InformedConsent createFromTemplate(String templateId, InformedConsentSaveDTO dto) {
        EmrTemplate template = templateRepository.findById(templateId)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + templateId));
        templateService.incrementUseCount(templateId);
        dto.setTemplateId(templateId);
        InformedConsent consent = createConsent(dto);
        log.info("应用模板 {} 创建知情同意书", template.getTemplateName());
        return consent;
    }

    @Override
    @Transactional
    public InformedConsent createForOperation(String operationId, ConsentType consentType, InformedConsentSaveDTO dto) {
        dto.setOperationId(operationId);
        dto.setConsentType(consentType);
        return createConsent(dto);
    }

    private void checkAndCompleteSigning(InformedConsent consent) {
        // 检查是否所有必要签名都已完成
        boolean doctorSigned = consent.getDoctorSignature() != null && consent.getSignTime() != null;
        boolean patientSigned = consent.getPatientSignature() != null && consent.getPatientSignTime() != null;
        boolean agentSigned = consent.getAgentSignature() != null && consent.getAgentSignTime() != null;

        if (doctorSigned && (patientSigned || agentSigned)) {
            consent.setStatus("已签署");
        }
    }

    private void mapDtoToEntity(InformedConsentSaveDTO dto, InformedConsent consent) {
        consent.setAdmissionId(dto.getAdmissionId());
        consent.setVisitId(dto.getVisitId());
        consent.setPatientId(dto.getPatientId());
        consent.setPatientName(dto.getPatientName());
        consent.setConsentType(dto.getConsentType());
        consent.setConsentName(dto.getConsentName());
        consent.setConsentContent(dto.getConsentContent());
        consent.setRiskDescription(dto.getRiskDescription());
        consent.setDoctorId(dto.getDoctorId());
        consent.setDoctorName(dto.getDoctorName());
        consent.setOperationId(dto.getOperationId());
        consent.setTemplateId(dto.getTemplateId());
    }
}