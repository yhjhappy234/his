package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.ProgressRecordSaveDTO;
import com.yhj.his.module.emr.entity.ProgressRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import com.yhj.his.module.emr.repository.ProgressRecordRepository;
import com.yhj.his.module.emr.service.ProgressRecordService;
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
 * 病程记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressRecordServiceImpl implements ProgressRecordService {

    private final ProgressRecordRepository recordRepository;
    private final QcService qcService;

    @Override
    @Transactional
    public ProgressRecord createRecord(ProgressRecordSaveDTO dto) {
        ProgressRecord record = new ProgressRecord();
        mapDtoToEntity(dto, record);
        record.setStatus(EmrStatus.DRAFT);
        record.setRecordTime(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public ProgressRecord updateRecord(String id, ProgressRecordSaveDTO dto) {
        ProgressRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的病程记录可以修改");
        }
        mapDtoToEntity(dto, record);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public void deleteRecord(String id) {
        ProgressRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的病程记录可以删除");
        }
        record.setDeleted(true);
        recordRepository.save(record);
    }

    @Override
    public ProgressRecord getRecordById(String id) {
        return recordRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("病程记录不存在: " + id));
    }

    @Override
    public List<ProgressRecord> getRecordsByAdmissionId(String admissionId) {
        return recordRepository.findByAdmissionIdAndDeletedFalseOrderByRecordDateDescRecordTimeDesc(admissionId);
    }

    @Override
    public List<ProgressRecord> getRecordsByAdmissionIdAndType(String admissionId, ProgressRecordType recordType) {
        return recordRepository.findByAdmissionIdAndRecordTypeAndDeletedFalseOrderByRecordDateDesc(admissionId, recordType);
    }

    @Override
    public List<ProgressRecord> getRecordsByAdmissionIdAndDate(String admissionId, LocalDate recordDate) {
        return recordRepository.findByAdmissionIdAndRecordDateAndDeletedFalse(admissionId, recordDate);
    }

    @Override
    public List<ProgressRecord> getRecordsByAdmissionIdAndDateRange(String admissionId, LocalDate startDate, LocalDate endDate) {
        return recordRepository.findByAdmissionIdAndDateRange(admissionId, startDate, endDate);
    }

    @Override
    public Page<ProgressRecord> listRecords(Pageable pageable) {
        Page<ProgressRecord> page = recordRepository.findAll(pageable);
        List<ProgressRecord> filtered = page.getContent().stream()
                .filter(r -> !r.getDeleted())
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public Page<ProgressRecord> getRecordsByPatientId(String patientId, Pageable pageable) {
        return recordRepository.findByPatientIdAndDeletedFalse(patientId, pageable);
    }

    @Override
    public Page<ProgressRecord> getRecordsByDoctorId(String doctorId, Pageable pageable) {
        return recordRepository.findByDoctorIdAndDeletedFalse(doctorId, pageable);
    }

    @Override
    public Optional<ProgressRecord> getFirstProgressRecord(String admissionId) {
        return recordRepository.findFirstByAdmissionIdAndRecordTypeAndDeletedFalse(
                admissionId, ProgressRecordType.FIRST_PROGRESS);
    }

    @Override
    public Page<ProgressRecord> getRecordsByStatus(EmrStatus status, Pageable pageable) {
        return recordRepository.findByStatusAndDeletedFalse(status, pageable);
    }

    @Override
    public Long countByAdmissionId(String admissionId) {
        return recordRepository.countByAdmissionId(admissionId);
    }

    @Override
    @Transactional
    public ProgressRecord submitRecord(EmrSubmitDTO dto) {
        ProgressRecord record = getRecordById(dto.getRecordId());
        if (record.getStatus() != EmrStatus.DRAFT && record.getStatus() != EmrStatus.REJECTED) {
            throw new RuntimeException("只有草稿或退回状态的病程记录可以提交");
        }
        record.setStatus(EmrStatus.SUBMITTED);
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public QcSubmitResultVO submitProgressRecord(EmrSubmitDTO dto) {
        submitRecord(dto);
        return qcService.performQcCheckAndReturn(dto.getRecordId(), "病程记录");
    }

    @Override
    @Transactional
    public ProgressRecord auditRecord(String id, boolean approved, String reviewerId, String reviewerName) {
        ProgressRecord record = getRecordById(id);
        if (record.getStatus() != EmrStatus.SUBMITTED) {
            throw new RuntimeException("只有已提交状态的病程记录可以审核");
        }
        record.setReviewerId(reviewerId);
        record.setReviewerName(reviewerName);
        record.setReviewTime(LocalDateTime.now());
        if (approved) {
            record.setStatus(EmrStatus.AUDITED);
        } else {
            record.setStatus(EmrStatus.REJECTED);
        }
        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public ProgressRecord createFirstProgressRecord(ProgressRecordSaveDTO dto) {
        // 检查是否已存在首次病程记录
        if (getFirstProgressRecord(dto.getAdmissionId()).isPresent()) {
            throw new RuntimeException("该住院记录已存在首次病程记录");
        }
        dto.setRecordType(ProgressRecordType.FIRST_PROGRESS);
        dto.setRecordTitle("首次病程记录");
        return createRecord(dto);
    }

    @Override
    @Transactional
    public ProgressRecord createChiefRoundRecord(ProgressRecordSaveDTO dto) {
        dto.setRecordType(ProgressRecordType.CHIEF_ROUND);
        dto.setRecordTitle("上级医师查房记录");
        return createRecord(dto);
    }

    private void mapDtoToEntity(ProgressRecordSaveDTO dto, ProgressRecord record) {
        record.setAdmissionId(dto.getAdmissionId());
        record.setPatientId(dto.getPatientId());
        record.setPatientName(dto.getPatientName());
        record.setRecordType(dto.getRecordType());
        record.setRecordTitle(dto.getRecordTitle());
        record.setRecordDate(dto.getRecordDate());
        record.setRecordContent(dto.getRecordContent());
        record.setDoctorId(dto.getDoctorId());
        record.setDoctorName(dto.getDoctorName());
        record.setDoctorTitle(dto.getDoctorTitle());
        record.setOperationId(dto.getOperationId());
        record.setConsultationId(dto.getConsultationId());
    }
}