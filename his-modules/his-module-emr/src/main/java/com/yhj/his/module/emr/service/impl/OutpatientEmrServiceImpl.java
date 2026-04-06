package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.OutpatientEmrSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.entity.OutpatientEmr;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.repository.OutpatientEmrRepository;
import com.yhj.his.module.emr.service.EmrTemplateService;
import com.yhj.his.module.emr.service.OutpatientEmrService;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 门诊病历服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutpatientEmrServiceImpl implements OutpatientEmrService {

    private final OutpatientEmrRepository emrRepository;
    private final EmrTemplateRepository templateRepository;
    private final EmrTemplateService templateService;
    private final QcService qcService;

    @Override
    @Transactional
    public OutpatientEmr createEmr(OutpatientEmrSaveDTO dto) {
        OutpatientEmr emr = new OutpatientEmr();
        mapDtoToEntity(dto, emr);
        emr.setVisitDate(LocalDate.now());
        emr.setStatus(EmrStatus.DRAFT);
        if (dto.getTemplateId() != null) {
            templateService.incrementUseCount(dto.getTemplateId());
        }
        return emrRepository.save(emr);
    }

    @Override
    @Transactional
    public OutpatientEmr updateEmr(String id, OutpatientEmrSaveDTO dto) {
        OutpatientEmr emr = getEmrById(id);
        if (emr.getStatus() != EmrStatus.DRAFT && emr.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的病历可以修改");
        }
        mapDtoToEntity(dto, emr);
        return emrRepository.save(emr);
    }

    @Override
    @Transactional
    public void deleteEmr(String id) {
        OutpatientEmr emr = getEmrById(id);
        if (emr.getStatus() != EmrStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的病历可以删除");
        }
        emr.setDeleted(true);
        emrRepository.save(emr);
    }

    @Override
    public OutpatientEmr getEmrById(String id) {
        return emrRepository.findById(id)
                .filter(e -> !e.getDeleted())
                .orElseThrow(() -> new RuntimeException("门诊病历不存在: " + id));
    }

    @Override
    public Optional<OutpatientEmr> getEmrByVisitId(String visitId) {
        return emrRepository.findByVisitIdAndDeletedFalse(visitId);
    }

    @Override
    public Page<OutpatientEmr> listEmrs(Pageable pageable) {
        Page<OutpatientEmr> page = emrRepository.findAll(pageable);
        List<OutpatientEmr> filtered = page.getContent().stream()
                .filter(e -> !e.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<OutpatientEmr> getEmrsByPatientId(String patientId) {
        return emrRepository.findByPatientIdAndDeletedFalseOrderByVisitDateDesc(patientId);
    }

    @Override
    public Page<OutpatientEmr> getEmrsByDoctorId(String doctorId, Pageable pageable) {
        return emrRepository.findByDoctorIdAndDeletedFalse(doctorId, pageable);
    }

    @Override
    public Page<OutpatientEmr> getEmrsByDeptIdAndDate(String deptId, LocalDate visitDate, Pageable pageable) {
        return emrRepository.findByDeptIdAndVisitDateAndDeletedFalse(deptId, visitDate, pageable);
    }

    @Override
    public Page<OutpatientEmr> getEmrsByStatus(EmrStatus status, Pageable pageable) {
        return emrRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<OutpatientEmr> getEmrsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable) {
        return emrRepository.findByDeptIdAndStatusAndDeletedFalse(deptId, status, pageable);
    }

    @Override
    public Optional<OutpatientEmr> getLatestEmrByPatientId(String patientId) {
        return emrRepository.findFirstByPatientIdAndDeletedFalseOrderByVisitDateDesc(patientId);
    }

    @Override
    public Page<OutpatientEmr> searchByPatientName(String patientName, Pageable pageable) {
        return emrRepository.findByPatientNameContainingAndDeletedFalse(patientName, pageable);
    }

    @Override
    @Transactional
    public OutpatientEmr submitEmr(EmrSubmitDTO dto) {
        OutpatientEmr emr = getEmrById(dto.getRecordId());
        if (emr.getStatus() != EmrStatus.DRAFT && emr.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的病历可以提交");
        }
        emr.setStatus(EmrStatus.SUBMITTED);
        emr.setSubmitTime(LocalDateTime.now());
        return emrRepository.save(emr);
    }

    @Override
    @Transactional
    public QcSubmitResultVO submitOutpatientEmr(EmrSubmitDTO dto) {
        submitEmr(dto);
        return qcService.performQcCheckAndReturn(dto.getRecordId(), "门诊病历");
    }

    @Override
    @Transactional
    public OutpatientEmr auditEmr(String id, boolean approved, String auditorId, String auditorName, String comment) {
        OutpatientEmr emr = getEmrById(id);
        if (emr.getStatus() != EmrStatus.SUBMITTED) {
            throw new RuntimeException("只有已提交状态的病历可以审核");
        }
        emr.setAuditorId(auditorId);
        emr.setAuditorName(auditorName);
        emr.setAuditComment(comment);
        emr.setAuditTime(LocalDateTime.now());
        if (approved) {
            emr.setStatus(EmrStatus.AUDITED);
        } else {
            emr.setStatus(EmrStatus.REJECTED);
        }
        return emrRepository.save(emr);
    }

    @Override
    public Long countByDeptIdAndVisitDate(String deptId, LocalDate visitDate) {
        return emrRepository.countByDeptIdAndVisitDate(deptId, visitDate);
    }

    @Override
    @Transactional
    public OutpatientEmr createFromTemplate(String templateId, OutpatientEmrSaveDTO dto) {
        EmrTemplate template = templateRepository.findById(templateId)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + templateId));
        dto.setTemplateId(templateId);
        OutpatientEmr emr = createEmr(dto);
        // 应用模板内容
        if (template.getTemplateContent() != null) {
            // 可以根据模板内容填充默认值
            log.info("应用模板 {} 创建门诊病历", template.getTemplateName());
        }
        return emr;
    }

    private void mapDtoToEntity(OutpatientEmrSaveDTO dto, OutpatientEmr emr) {
        emr.setVisitId(dto.getVisitId());
        emr.setPatientId(dto.getPatientId());
        emr.setPatientName(dto.getPatientName());
        emr.setDeptId(dto.getDeptId());
        emr.setDeptName(dto.getDeptName());
        emr.setDoctorId(dto.getDoctorId());
        emr.setDoctorName(dto.getDoctorName());
        emr.setChiefComplaint(dto.getChiefComplaint());
        emr.setPresentIllness(dto.getPresentIllness());
        emr.setPastHistory(dto.getPastHistory());
        emr.setPersonalHistory(dto.getPersonalHistory());
        emr.setFamilyHistory(dto.getFamilyHistory());
        emr.setAllergyHistory(dto.getAllergyHistory());
        emr.setTemperature(dto.getTemperature());
        emr.setPulse(dto.getPulse());
        emr.setRespiration(dto.getRespiration());
        emr.setBloodPressure(dto.getBloodPressure());
        emr.setWeight(dto.getWeight());
        emr.setHeight(dto.getHeight());
        emr.setGeneralExam(dto.getGeneralExam());
        emr.setSpecialistExam(dto.getSpecialistExam());
        emr.setAuxiliaryExam(dto.getAuxiliaryExam());
        emr.setPrimaryDiagnosisCode(dto.getPrimaryDiagnosisCode());
        emr.setPrimaryDiagnosisName(dto.getPrimaryDiagnosisName());
        emr.setSecondaryDiagnosis(dto.getSecondaryDiagnosis());
        emr.setTreatmentPlan(dto.getTreatmentPlan());
        emr.setMedicalAdvice(dto.getMedicalAdvice());
        emr.setTemplateId(dto.getTemplateId());
    }
}