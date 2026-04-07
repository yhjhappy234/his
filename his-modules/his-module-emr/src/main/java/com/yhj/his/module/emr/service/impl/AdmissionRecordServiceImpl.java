package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.AdmissionRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.AdmissionRecord;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.repository.AdmissionRecordRepository;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.service.AdmissionRecordService;
import com.yhj.his.module.emr.service.EmrTemplateService;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
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
 * 入院记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdmissionRecordServiceImpl implements AdmissionRecordService {

    private final AdmissionRecordRepository recordRepository;
    private final EmrTemplateRepository templateRepository;
    private final EmrTemplateService templateService;
    private final QcService qcService;

    @Override
    @Transactional
    public AdmissionRecord createRecord(AdmissionRecordSaveDTO dto) {
        AdmissionRecord record = new AdmissionRecord();
        mapDtoToEntity(dto, record);
        record.setStatus(EmrStatus.DRAFT);
        record.setRecordTime(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public AdmissionRecord updateRecord(String id, AdmissionRecordSaveDTO dto) {
        AdmissionRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的入院记录可以修改");
        }
        mapDtoToEntity(dto, record);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public void deleteRecord(String id) {
        AdmissionRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的入院记录可以删除");
        }
        record.setDeleted(true);
        recordRepository.save(record);
    }

    @Override
    public AdmissionRecord getRecordById(String id) {
        return recordRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("入院记录不存在: " + id));
    }

    @Override
    public Optional<AdmissionRecord> getRecordByAdmissionId(String admissionId) {
        return recordRepository.findByAdmissionIdAndDeletedFalse(admissionId);
    }

    @Override
    public Page<AdmissionRecord> listRecords(Pageable pageable) {
        Page<AdmissionRecord> page = recordRepository.findAll(pageable);
        List<AdmissionRecord> filtered = page.getContent().stream()
                .filter(r -> !r.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<AdmissionRecord> getRecordsByPatientId(String patientId) {
        return recordRepository.findByPatientIdAndDeletedFalseOrderByAdmissionDateDesc(patientId);
    }

    @Override
    public Page<AdmissionRecord> getRecordsByDeptId(String deptId, Pageable pageable) {
        return recordRepository.findByDeptIdAndDeletedFalse(deptId, pageable);
    }

    @Override
    public Page<AdmissionRecord> getRecordsByStatus(EmrStatus status, Pageable pageable) {
        return recordRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<AdmissionRecord> getRecordsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable) {
        return recordRepository.findByDeptIdAndStatusAndDeletedFalse(deptId, status, pageable);
    }

    @Override
    public Optional<AdmissionRecord> getLatestRecordByPatientId(String patientId) {
        return recordRepository.findFirstByPatientIdAndDeletedFalseOrderByAdmissionDateDesc(patientId);
    }

    @Override
    public Page<AdmissionRecord> searchByPatientName(String patientName, Pageable pageable) {
        return recordRepository.findByPatientNameContainingAndDeletedFalse(patientName, pageable);
    }

    @Override
    @Transactional
    public AdmissionRecord submitRecord(EmrSubmitDTO dto) {
        AdmissionRecord record = getRecordById(dto.getRecordId());
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的入院记录可以提交");
        }
        record.setStatus(EmrStatus.SUBMITTED);
        record.setSubmitTime(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public QcSubmitResultVO submitAdmissionRecord(EmrSubmitDTO dto) {
        submitRecord(dto);
        return qcService.performQcCheckAndReturn(dto.getRecordId(), "入院记录");
    }

    @Override
    @Transactional
    public AdmissionRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment) {
        AdmissionRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.SUBMITTED) {
            throw new RuntimeException("只有已提交状态的入院记录可以审核");
        }
        record.setAuditTime(LocalDateTime.now());
        if (approved) {
            record.setStatus(EmrStatus.AUDITED);
        } else {
            record.setStatus(EmrStatus.REJECTED);
        }
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public AdmissionRecord createFromTemplate(String templateId, AdmissionRecordSaveDTO dto) {
        EmrTemplate template = templateRepository.findById(templateId)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + templateId));
        templateService.incrementUseCount(templateId);
        AdmissionRecord record = createRecord(dto);
        log.info("应用模板 {} 创建入院记录", template.getTemplateName());
        return record;
    }

    private void mapDtoToEntity(AdmissionRecordSaveDTO dto, AdmissionRecord record) {
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setPatientName(dto.getPatientName());
        record.setGender(dto.getGender());
        record.setAge(dto.getAge());
        record.setDeptId(dto.getDeptId());
        record.setDeptName(dto.getDeptName());
        record.setWardId(dto.getWardId());
        record.setBedNo(dto.getBedNo());
        record.setChiefComplaint(dto.getChiefComplaint());
        record.setPresentIllness(dto.getPresentIllness());
        record.setPastHistory(dto.getPastHistory());
        record.setPersonalHistory(dto.getPersonalHistory());
        record.setMarriageHistory(dto.getMarriageHistory());
        record.setFamilyHistory(dto.getFamilyHistory());
        record.setAllergyHistory(dto.getAllergyHistory());
        record.setTemperature(dto.getTemperature());
        record.setPulse(dto.getPulse());
        record.setRespiration(dto.getRespiration());
        record.setBloodPressure(dto.getBloodPressure());
        record.setWeight(dto.getWeight());
        record.setHeight(dto.getHeight());
        record.setGeneralExam(dto.getGeneralExam());
        record.setSpecialistExam(dto.getSpecialistExam());
        record.setAuxiliaryExam(dto.getAuxiliaryExam());
        record.setAdmissionDiagnosisCode(dto.getAdmissionDiagnosisCode());
        record.setAdmissionDiagnosisName(dto.getAdmissionDiagnosisName());
        record.setTreatmentPlan(dto.getTreatmentPlan());
        record.setDoctorId(dto.getDoctorId());
        record.setDoctorName(dto.getDoctorName());
        record.setSuperiorDoctorId(dto.getSuperiorDoctorId());
        record.setSuperiorDoctorName(dto.getSuperiorDoctorName());
    }
}