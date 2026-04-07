package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.OperationRecordSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.entity.OperationRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.repository.OperationRecordRepository;
import com.yhj.his.module.emr.service.EmrTemplateService;
import com.yhj.his.module.emr.service.OperationRecordService;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 手术记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationRecordServiceImpl implements OperationRecordService {

    private final OperationRecordRepository recordRepository;
    private final EmrTemplateRepository templateRepository;
    private final EmrTemplateService templateService;
    private final QcService qcService;

    @Override
    @Transactional
    public OperationRecord createRecord(OperationRecordSaveDTO dto) {
        OperationRecord record = new OperationRecord();
        mapDtoToEntity(dto, record);
        record.setStatus(EmrStatus.DRAFT);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public OperationRecord updateRecord(String id, OperationRecordSaveDTO dto) {
        OperationRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的手术记录可以修改");
        }
        mapDtoToEntity(dto, record);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public void deleteRecord(String id) {
        OperationRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的手术记录可以删除");
        }
        record.setDeleted(true);
        recordRepository.save(record);
    }

    @Override
    public OperationRecord getRecordById(String id) {
        return recordRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("手术记录不存在: " + id));
    }

    @Override
    public List<OperationRecord> getRecordsByAdmissionId(String admissionId) {
        return recordRepository.findByAdmissionIdAndDeletedFalseOrderByOperationDateDesc(admissionId);
    }

    @Override
    public List<OperationRecord> getRecordsByAdmissionIdAndDateRange(String admissionId, LocalDate startDate, LocalDate endDate) {
        return recordRepository.findByAdmissionIdAndDateRange(admissionId, startDate, endDate);
    }

    @Override
    public List<OperationRecord> getRecordsByPatientId(String patientId) {
        return recordRepository.findByPatientIdAndDeletedFalseOrderByOperationDateDesc(patientId);
    }

    @Override
    public Page<OperationRecord> listRecords(Pageable pageable) {
        Page<OperationRecord> page = recordRepository.findAll(pageable);
        List<OperationRecord> filtered = page.getContent().stream()
                .filter(r -> !r.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Page<OperationRecord> getRecordsByDeptId(String deptId, Pageable pageable) {
        return recordRepository.findByDeptIdAndDeletedFalse(deptId, pageable);
    }

    @Override
    public Page<OperationRecord> getRecordsBySurgeonId(String surgeonId, Pageable pageable) {
        return recordRepository.findBySurgeonIdAndDeletedFalse(surgeonId, pageable);
    }

    @Override
    public Page<OperationRecord> getRecordsByOperationDate(LocalDate operationDate, Pageable pageable) {
        return recordRepository.findByOperationDateAndDeletedFalse(operationDate, pageable);
    }

    @Override
    public Page<OperationRecord> getRecordsByStatus(EmrStatus status, Pageable pageable) {
        return recordRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Page<OperationRecord> searchByPatientName(String patientName, Pageable pageable) {
        return recordRepository.findByPatientNameContainingAndDeletedFalse(patientName, pageable);
    }

    @Override
    public Page<OperationRecord> searchByOperationName(String operationName, Pageable pageable) {
        return recordRepository.findByOperationNameContainingAndDeletedFalse(operationName, pageable);
    }

    @Override
    public Long countBySurgeonId(String surgeonId) {
        return recordRepository.countBySurgeonId(surgeonId);
    }

    @Override
    @Transactional
    public OperationRecord submitRecord(EmrSubmitDTO dto) {
        OperationRecord record = getRecordById(dto.getRecordId());
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的手术记录可以提交");
        }
        record.setStatus(EmrStatus.SUBMITTED);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public QcSubmitResultVO submitOperationRecord(EmrSubmitDTO dto) {
        submitRecord(dto);
        return qcService.performQcCheckAndReturn(dto.getRecordId(), "手术记录");
    }

    @Override
    @Transactional
    public OperationRecord auditRecord(String id, boolean approved, String auditorId, String auditorName, String comment) {
        OperationRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.SUBMITTED) {
            throw new RuntimeException("只有已提交状态的手术记录可以审核");
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
    public OperationRecord createFromTemplate(String templateId, OperationRecordSaveDTO dto) {
        EmrTemplate template = templateRepository.findById(templateId)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + templateId));
        templateService.incrementUseCount(templateId);
        OperationRecord record = createRecord(dto);
        log.info("应用模板 {} 创建手术记录", template.getTemplateName());
        return record;
    }

    private void mapDtoToEntity(OperationRecordSaveDTO dto, OperationRecord record) {
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setPatientName(dto.getPatientName());
        record.setOperationDate(dto.getOperationDate());
        record.setStartTime(dto.getStartTime());
        record.setEndTime(dto.getEndTime());
        record.setOperationDuration(dto.getOperationDuration());
        record.setPreOpDiagnosis(dto.getPreOpDiagnosis());
        record.setPostOpDiagnosis(dto.getPostOpDiagnosis());
        record.setOperationName(dto.getOperationName());
        record.setOperationCode(dto.getOperationCode());
        record.setSurgeonId(dto.getSurgeonId());
        record.setSurgeonName(dto.getSurgeonName());
        record.setAssistants(dto.getAssistants());
        record.setAnesthesiologistId(dto.getAnesthesiologistId());
        record.setAnesthesiologistName(dto.getAnesthesiologistName());
        record.setAnesthesiaMethod(dto.getAnesthesiaMethod());
        record.setOperatingRoom(dto.getOperatingRoom());
        record.setScrubNurse(dto.getScrubNurse());
        record.setCirculatingNurse(dto.getCirculatingNurse());
        record.setIncision(dto.getIncision());
        record.setProcedureDetail(dto.getProcedureDetail());
        record.setOperationFindings(dto.getOperationFindings());
        record.setSpecimens(dto.getSpecimens());
        record.setComplications(dto.getComplications());
        record.setBloodLoss(dto.getBloodLoss());
        record.setTransfusion(dto.getTransfusion());
        record.setImplants(dto.getImplants());
        record.setDeptId(dto.getDeptId());
        record.setDeptName(dto.getDeptName());
    }
}