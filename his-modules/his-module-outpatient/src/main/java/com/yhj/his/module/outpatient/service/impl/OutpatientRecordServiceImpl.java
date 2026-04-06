package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.RecordSaveRequest;
import com.yhj.his.module.outpatient.entity.OutpatientRecord;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.OutpatientRecordRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.OutpatientRecordService;
import com.yhj.his.module.outpatient.vo.OutpatientRecordVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 门诊病历服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutpatientRecordServiceImpl implements OutpatientRecordService {

    private final OutpatientRecordRepository outpatientRecordRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional
    public OutpatientRecordVO saveDraft(RecordSaveRequest request) {
        log.info("保存病历草稿: registrationId={}", request.getRegistrationId());

        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("挂号记录不存在"));

        OutpatientRecord record = findOrCreateRecord(request.getRegistrationId(), registration);

        // 更新病历内容
        updateRecordFromRequest(record, request);
        record.setStatus("草稿");

        record = outpatientRecordRepository.save(record);
        log.info("病历草稿保存成功: recordId={}", record.getId());

        return convertToVO(record);
    }

    @Override
    @Transactional
    public OutpatientRecordVO submitRecord(RecordSaveRequest request) {
        log.info("提交病历: registrationId={}", request.getRegistrationId());

        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("挂号记录不存在"));

        OutpatientRecord record = findOrCreateRecord(request.getRegistrationId(), registration);

        // 更新病历内容
        updateRecordFromRequest(record, request);
        record.setStatus("已提交");
        record.setSubmitTime(LocalDateTime.now());

        record = outpatientRecordRepository.save(record);

        // 更新挂号状态
        registration.setStatus("已就诊");
        registration.setVisitStatus("已完成");
        registrationRepository.save(registration);

        log.info("病历提交成功: recordId={}", record.getId());

        return convertToVO(record);
    }

    @Override
    public Optional<OutpatientRecord> findById(String id) {
        return outpatientRecordRepository.findById(id);
    }

    @Override
    public Optional<OutpatientRecord> findByRegistrationId(String registrationId) {
        return outpatientRecordRepository.findByRegistrationId(registrationId);
    }

    @Override
    public OutpatientRecordVO getRecordDetail(String id) {
        OutpatientRecord record = outpatientRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("病历不存在"));
        return convertToVO(record);
    }

    @Override
    public OutpatientRecordVO getRecordByRegistrationId(String registrationId) {
        Optional<OutpatientRecord> recordOpt = outpatientRecordRepository.findByRegistrationId(registrationId);
        return recordOpt.map(this::convertToVO).orElse(null);
    }

    @Override
    public PageResult<OutpatientRecordVO> listRecords(String patientId, String doctorId,
            String deptId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<OutpatientRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            if (deptId != null && !deptId.isEmpty()) {
                predicates.add(cb.equal(root.get("deptId"), deptId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("visitDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("visitDate"), endDate));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<OutpatientRecord> page = outpatientRecordRepository.findAll(spec, pageable);
        List<OutpatientRecordVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(),
                pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public List<OutpatientRecordVO> listPatientRecords(String patientId) {
        return outpatientRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OutpatientRecordVO> listRecentRecords(String patientId, int limit) {
        return outpatientRecordRepository.findRecentRecords(patientId, limit).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void voidRecord(String id, String reason) {
        log.info("作废病历: id={}, reason={}", id, reason);

        OutpatientRecord record = outpatientRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("病历不存在"));

        if (!record.getStatus().equals("已提交")) {
            throw new IllegalArgumentException("只能作废已提交的病历");
        }

        record.setStatus("已作废");
        record.setRemark(reason);
        outpatientRecordRepository.save(record);

        log.info("病历作废成功: id={}", id);
    }

    @Override
    @Transactional
    public OutpatientRecordVO updateRecord(String id, RecordSaveRequest request) {
        log.info("更新病历: id={}", id);

        OutpatientRecord record = outpatientRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("病历不存在"));

        if (!record.getStatus().equals("草稿")) {
            throw new IllegalArgumentException("只能修改草稿状态的病历");
        }

        updateRecordFromRequest(record, request);
        record = outpatientRecordRepository.save(record);

        log.info("病历更新成功: id={}", id);

        return convertToVO(record);
    }

    /**
     * 查找或创建病历
     */
    private OutpatientRecord findOrCreateRecord(String registrationId, Registration registration) {
        Optional<OutpatientRecord> existingRecord = outpatientRecordRepository.findByRegistrationId(registrationId);

        if (existingRecord.isPresent()) {
            return existingRecord.get();
        }

        OutpatientRecord record = new OutpatientRecord();
        record.setRegistrationId(registrationId);
        record.setPatientId(registration.getPatientId());
        record.setPatientName(registration.getPatientName());
        record.setVisitNo(registration.getVisitNo());
        record.setDeptId(registration.getDeptId());
        record.setDeptName(registration.getDeptName());
        record.setDoctorId(registration.getDoctorId());
        record.setDoctorName(registration.getDoctorName());
        record.setVisitDate(registration.getScheduleDate());

        return record;
    }

    /**
     * 从请求更新病历
     */
    private void updateRecordFromRequest(OutpatientRecord record, RecordSaveRequest request) {
        record.setChiefComplaint(request.getChiefComplaint());
        record.setPresentIllness(request.getPresentIllness());
        record.setPastHistory(request.getPastHistory());
        record.setAllergyHistory(request.getAllergyHistory());
        record.setPersonalHistory(request.getPersonalHistory());
        record.setFamilyHistory(request.getFamilyHistory());
        record.setTemperature(request.getTemperature());
        record.setPulse(request.getPulse());
        record.setRespiration(request.getRespiration());
        record.setBloodPressure(request.getBloodPressure());
        record.setHeight(request.getHeight());
        record.setWeight(request.getWeight());
        record.setPhysicalExam(request.getPhysicalExam());
        record.setAuxiliaryExam(request.getAuxiliaryExam());
        record.setDiagnosisCode(request.getDiagnosisCode());
        record.setDiagnosisName(request.getDiagnosisName());
        record.setDiagnosisType(request.getDiagnosisType());
        record.setTreatmentPlan(request.getTreatmentPlan());
        record.setMedicalAdvice(request.getMedicalAdvice());
    }

    /**
     * 转换为VO
     */
    private OutpatientRecordVO convertToVO(OutpatientRecord record) {
        OutpatientRecordVO vo = new OutpatientRecordVO();
        vo.setRecordId(record.getId());
        vo.setRegistrationId(record.getRegistrationId());
        vo.setPatientId(record.getPatientId());
        vo.setPatientName(record.getPatientName());
        vo.setVisitNo(record.getVisitNo());
        vo.setDeptId(record.getDeptId());
        vo.setDeptName(record.getDeptName());
        vo.setDoctorId(record.getDoctorId());
        vo.setDoctorName(record.getDoctorName());
        vo.setVisitDate(record.getVisitDate());
        vo.setChiefComplaint(record.getChiefComplaint());
        vo.setPresentIllness(record.getPresentIllness());
        vo.setPastHistory(record.getPastHistory());
        vo.setAllergyHistory(record.getAllergyHistory());
        vo.setPersonalHistory(record.getPersonalHistory());
        vo.setFamilyHistory(record.getFamilyHistory());
        vo.setTemperature(record.getTemperature());
        vo.setPulse(record.getPulse());
        vo.setRespiration(record.getRespiration());
        vo.setBloodPressure(record.getBloodPressure());
        vo.setHeight(record.getHeight());
        vo.setWeight(record.getWeight());
        vo.setPhysicalExam(record.getPhysicalExam());
        vo.setAuxiliaryExam(record.getAuxiliaryExam());
        vo.setDiagnosisCode(record.getDiagnosisCode());
        vo.setDiagnosisName(record.getDiagnosisName());
        vo.setDiagnosisType(record.getDiagnosisType());
        vo.setTreatmentPlan(record.getTreatmentPlan());
        vo.setMedicalAdvice(record.getMedicalAdvice());
        vo.setStatus(record.getStatus());
        vo.setCreateTime(record.getCreateTime());
        vo.setSubmitTime(record.getSubmitTime());
        return vo;
    }
}