package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.ExaminationRequestDto;
import com.yhj.his.module.outpatient.entity.ExaminationRequest;
import com.yhj.his.module.outpatient.entity.OutpatientRecord;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.ExaminationRequestRepository;
import com.yhj.his.module.outpatient.repository.OutpatientRecordRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.ExaminationRequestService;

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
 * 检查检验申请服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExaminationRequestServiceImpl implements ExaminationRequestService {

    private final ExaminationRequestRepository examinationRequestRepository;
    private final RegistrationRepository registrationRepository;
    private final OutpatientRecordRepository outpatientRecordRepository;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public ExaminationRequest createRequest(ExaminationRequestDto request) {
        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new BusinessException("挂号记录不存在"));

        if (!registration.getVisitStatus().equals("就诊中")) {
            throw new BusinessException("患者未在就诊中，无法开立检查检验申请");
        }

        // 获取病历中的诊断信息
        Optional<OutpatientRecord> record = outpatientRecordRepository.findByRegistrationId(request.getRegistrationId());

        ExaminationRequest examRequest = new ExaminationRequest();
        examRequest.setRequestNo(sequenceGenerator.generate("EXM", 8));
        examRequest.setRegistrationId(request.getRegistrationId());
        examRequest.setPatientId(request.getPatientId());
        examRequest.setPatientName(registration.getPatientName());
        examRequest.setGender(registration.getGender());
        examRequest.setAge(registration.getAge());
        examRequest.setDeptId(registration.getDeptId());
        examRequest.setDeptName(registration.getDeptName());
        examRequest.setDoctorId(registration.getDoctorId());
        examRequest.setDoctorName(registration.getDoctorName());
        examRequest.setRequestType(request.getRequestType());
        examRequest.setRequestDate(LocalDate.now());
        examRequest.setDiagnosisCode(request.getDiagnosisCode() != null ? request.getDiagnosisCode() :
                record.map(OutpatientRecord::getDiagnosisCode).orElse(null));
        examRequest.setDiagnosisName(request.getDiagnosisName() != null ? request.getDiagnosisName() :
                record.map(OutpatientRecord::getDiagnosisName).orElse(null));
        examRequest.setExamItems(request.getExamItems().stream().collect(Collectors.joining(",")));
        examRequest.setClinicalSummary(request.getClinicalSummary());
        examRequest.setIsEmergency(request.getIsEmergency() != null ? request.getIsEmergency() : false);
        examRequest.setPayStatus("未收费");
        examRequest.setStatus("待检查");
        examRequest.setRequestTime(LocalDateTime.now());
        examRequest.setRemark(request.getRemark());

        ExaminationRequest saved = examinationRequestRepository.save(examRequest);
        log.info("创建检查检验申请成功: requestNo={}", saved.getRequestNo());
        return saved;
    }

    @Override
    public Optional<ExaminationRequest> findById(String id) {
        return examinationRequestRepository.findById(id);
    }

    @Override
    public Optional<ExaminationRequest> findByRequestNo(String requestNo) {
        return examinationRequestRepository.findByRequestNo(requestNo);
    }

    @Override
    public ExaminationRequest getRequestDetail(String id) {
        return findById(id).orElseThrow(() -> new BusinessException("检查检验申请不存在"));
    }

    @Override
    public PageResult<ExaminationRequest> listRequests(String patientId, String doctorId, String requestType, String status, String payStatus, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<ExaminationRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            if (requestType != null && !requestType.isEmpty()) {
                predicates.add(cb.equal(root.get("requestType"), requestType));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (payStatus != null && !payStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("payStatus"), payStatus));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("requestDate"), endDate));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<ExaminationRequest> page = examinationRequestRepository.findAll(spec, pageable);
        return PageResult.of(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<ExaminationRequest> listRequestsByRegistration(String registrationId) {
        return examinationRequestRepository.findByRegistrationId(registrationId);
    }

    @Override
    public List<ExaminationRequest> listPatientRequests(String patientId) {
        return examinationRequestRepository.findByPatientIdOrderByRequestDateDesc(patientId);
    }

    @Override
    @Transactional
    public void cancelRequest(String id, String reason) {
        ExaminationRequest request = getRequestDetail(id);

        if (request.getPayStatus().equals("已收费")) {
            throw new BusinessException("已收费申请无法取消，请先退费");
        }

        request.setStatus("已取消");
        request.setRemark(reason);
        examinationRequestRepository.save(request);

        log.info("取消检查检验申请成功: requestId={}", id);
    }

    @Override
    @Transactional
    public ExaminationRequest completeRequest(String id) {
        ExaminationRequest request = getRequestDetail(id);

        if (!request.getPayStatus().equals("已收费")) {
            throw new BusinessException("未收费申请无法完成");
        }

        request.setStatus("已完成");
        request.setCompleteTime(LocalDateTime.now());
        ExaminationRequest saved = examinationRequestRepository.save(request);

        log.info("完成检查检验申请成功: requestId={}", id);
        return saved;
    }

    @Override
    @Transactional
    public ExaminationRequest updatePayStatus(String id, String payStatus) {
        ExaminationRequest request = getRequestDetail(id);
        request.setPayStatus(payStatus);
        ExaminationRequest saved = examinationRequestRepository.save(request);
        log.info("更新检查检验申请收费状态成功: requestId={}, payStatus={}", id, payStatus);
        return saved;
    }

    @Override
    @Transactional
    public ExaminationRequest updateRequest(String id, ExaminationRequestDto dto) {
        ExaminationRequest request = getRequestDetail(id);

        if (request.getStatus().equals("已取消") || request.getPayStatus().equals("已收费")) {
            throw new BusinessException("申请状态不允许修改");
        }

        request.setRequestType(dto.getRequestType());
        request.setDiagnosisCode(dto.getDiagnosisCode());
        request.setDiagnosisName(dto.getDiagnosisName());
        request.setExamItems(dto.getExamItems().stream().collect(Collectors.joining(",")));
        request.setClinicalSummary(dto.getClinicalSummary());
        request.setIsEmergency(dto.getIsEmergency() != null ? dto.getIsEmergency() : false);
        request.setRemark(dto.getRemark());

        ExaminationRequest saved = examinationRequestRepository.save(request);
        log.info("更新检查检验申请成功: requestId={}", id);
        return saved;
    }

    @Override
    public List<ExaminationRequest> listPendingRequests(String patientId) {
        return examinationRequestRepository.findByPatientIdOrderByRequestDateDesc(patientId).stream()
                .filter(r -> r.getStatus().equals("待检查"))
                .collect(Collectors.toList());
    }
}