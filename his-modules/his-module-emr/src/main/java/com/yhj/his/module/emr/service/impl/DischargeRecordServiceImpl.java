package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.DischargeRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.DischargeRecord;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.repository.DischargeRecordRepository;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.service.DischargeRecordService;
import com.yhj.his.module.emr.service.EmrTemplateService;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 出院记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DischargeRecordServiceImpl implements DischargeRecordService {

    private final DischargeRecordRepository recordRepository;
    private final EmrTemplateRepository templateRepository;
    private final EmrTemplateService templateService;
    private final QcService qcService;

    @Override
    @Transactional
    public DischargeRecord createRecord(DischargeRecordSaveDTO dto) {
        DischargeRecord record = new DischargeRecord();
        mapDtoToEntity(dto, record);
        record.setStatus(EmrStatus.DRAFT);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public DischargeRecord updateRecord(String id, DischargeRecordSaveDTO dto) {
        DischargeRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的出院记录可以修改");
        }
        mapDtoToEntity(dto, record);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public void deleteRecord(String id) {
        DischargeRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的出院记录可以删除");
        }
        record.setDeleted(true);
        recordRepository.save(record);
    }

    @Override
    public DischargeRecord getRecordById(String id) {
        return recordRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("出院记录不存在: " + id));
    }

    @Override
    public Optional<DischargeRecord> getRecordByAdmissionId(String admissionId) {
        return recordRepository.findByAdmissionIdAndDeletedFalse(admissionId);
    }

    @Override
    public Page<DischargeRecord> listRecords(Pageable pageable) {
        Page<DischargeRecord> page = recordRepository.findAll(pageable);
        List<DischargeRecord> filtered = page.getContent().stream()
                .filter(r -> !r.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<DischargeRecord> getRecordsByPatientId(String patientId) {
        return recordRepository.findByPatientIdAndDeletedFalseOrderByDischargeDateDesc(patientId);
    }

    @Override
    public Page<DischargeRecord> getRecordsByDeptId(String deptId, Pageable pageable) {
        return recordRepository.findByDeptIdAndDeletedFalse(deptId, pageable);
    }

    @Override
    public Page<DischargeRecord> getRecordsByStatus(EmrStatus status, Pageable pageable) {
        return recordRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<DischargeRecord> getRecordsByDeptIdAndStatus(String deptId, EmrStatus status, Pageable pageable) {
        return recordRepository.findByDeptIdAndStatusAndDeletedFalse(deptId, status, pageable);
    }

    @Override
    public Optional<DischargeRecord> getLatestRecordByPatientId(String patientId) {
        return recordRepository.findFirstByPatientIdAndDeletedFalseOrderByDischargeDateDesc(patientId);
    }

    @Override
    public Page<DischargeRecord> searchByPatientName(String patientName, Pageable pageable) {
        return recordRepository.findByPatientNameContainingAndDeletedFalse(patientName, pageable);
    }

    @Override
    @Transactional
    public DischargeRecord submitRecord(EmrSubmitDTO dto) {
        DischargeRecord record = getRecordById(dto.getRecordId());
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的出院记录可以提交");
        }
        record.setStatus(EmrStatus.SUBMITTED);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public QcSubmitResultVO submitDischargeRecord(EmrSubmitDTO dto) {
        submitRecord(dto);
        return qcService.performQcCheckAndReturn(dto.getRecordId(), "出院记录");
    }

    @Override
    @Transactional
    public DischargeRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment) {
        DischargeRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.SUBMITTED) {
            throw new RuntimeException("只有已提交状态的出院记录可以审核");
        }
        if (approved) {
            record.setStatus(EmrStatus.AUDITED);
        } else {
            record.setStatus(EmrStatus.REJECTED);
        }
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public DischargeRecord createFromTemplate(String templateId, DischargeRecordSaveDTO dto) {
        EmrTemplate template = templateRepository.findById(templateId)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + templateId));
        templateService.incrementUseCount(templateId);
        DischargeRecord record = createRecord(dto);
        log.info("应用模板 {} 创建出院记录", template.getTemplateName());
        return record;
    }

    private void mapDtoToEntity(DischargeRecordSaveDTO dto, DischargeRecord record) {
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setPatientName(dto.getPatientName());
        record.setAdmissionDate(dto.getAdmissionDate());
        record.setDischargeDate(dto.getDischargeDate());
        record.setHospitalDays(dto.getHospitalDays());
        record.setAdmissionSituation(dto.getAdmissionSituation());
        record.setTreatmentProcess(dto.getTreatmentProcess());
        record.setDischargeDiagnosisCode(dto.getDischargeDiagnosisCode());
        record.setDischargeDiagnosisName(dto.getDischargeDiagnosisName());
        record.setDischargeCondition(dto.getDischargeCondition());
        record.setDischargeAdvice(dto.getDischargeAdvice());
        record.setDischargeMedication(dto.getDischargeMedication());
        record.setFollowUpDate(dto.getFollowUpDate());
        record.setFollowUpDept(dto.getFollowUpDept());
        record.setDoctorId(dto.getDoctorId());
        record.setDoctorName(dto.getDoctorName());
        record.setDeptId(dto.getDeptId());
        record.setDeptName(dto.getDeptName());
    }
}