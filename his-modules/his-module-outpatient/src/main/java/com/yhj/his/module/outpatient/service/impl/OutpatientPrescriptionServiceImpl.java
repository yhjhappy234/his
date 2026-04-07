package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.PrescriptionCreateRequest;
import com.yhj.his.module.outpatient.entity.OutpatientPrescription;
import com.yhj.his.module.outpatient.entity.PrescriptionDetail;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.repository.OutpatientPrescriptionRepository;
import com.yhj.his.module.outpatient.repository.PrescriptionDetailRepository;
import com.yhj.his.module.outpatient.repository.RegistrationRepository;
import com.yhj.his.module.outpatient.service.OutpatientPrescriptionService;
import com.yhj.his.module.outpatient.vo.PrescriptionResultVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 门诊处方服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutpatientPrescriptionServiceImpl implements OutpatientPrescriptionService {

    private final OutpatientPrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository detailRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional
    public PrescriptionResultVO createPrescription(PrescriptionCreateRequest request) {
        log.info("开立处方: registrationId={}", request.getRegistrationId());

        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("挂号记录不存在"));

        // 创建处方
        OutpatientPrescription prescription = new OutpatientPrescription();
        prescription.setPrescriptionNo(generatePrescriptionNo());
        prescription.setRegistrationId(request.getRegistrationId());
        prescription.setPatientId(request.getPatientId());
        prescription.setPatientName(registration.getPatientName());
        prescription.setGender(registration.getGender());
        prescription.setAge(registration.getAge());
        prescription.setDeptId(registration.getDeptId());
        prescription.setDeptName(registration.getDeptName());
        prescription.setDoctorId(registration.getDoctorId());
        prescription.setDoctorName(registration.getDoctorName());
        prescription.setPrescriptionType(request.getPrescriptionType());
        prescription.setPrescriptionDate(LocalDate.now());
        prescription.setDiagnosisCode(request.getDiagnosisCode());
        prescription.setDiagnosisName(request.getDiagnosisName());
        prescription.setPayStatus("未收费");
        prescription.setStatus("有效");
        prescription.setAuditStatus("待审核");
        prescription.setRemark(request.getRemark());

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PrescriptionDetail> details = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (PrescriptionCreateRequest.PrescriptionDetailRequest detailReq : request.getDetails()) {
            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setDrugId(detailReq.getDrugId());
            detail.setDrugName(detailReq.getDrugName());
            detail.setDrugSpec(detailReq.getDrugSpec());
            detail.setDrugUnit(detailReq.getDrugUnit());
            detail.setDrugForm(detailReq.getDrugForm());
            detail.setQuantity(detailReq.getQuantity());
            detail.setDosage(detailReq.getDosage());
            detail.setFrequency(detailReq.getFrequency());
            detail.setDays(detailReq.getDays());
            detail.setRoute(detailReq.getRoute());
            detail.setUnitPrice(detailReq.getUnitPrice() != null ? detailReq.getUnitPrice() : BigDecimal.ZERO);
            detail.setGroupNo(detailReq.getGroupNo());
            detail.setSkinTest(detailReq.getSkinTest());
            detail.setIsEssential(detailReq.getIsEssential());
            detail.setIsMedicalInsurance(detailReq.getIsMedicalInsurance());
            detail.setRemark(detailReq.getRemark());

            // 计算金额
            BigDecimal amount = detail.getUnitPrice().multiply(detail.getQuantity());
            detail.setAmount(amount);
            totalAmount = totalAmount.add(amount);

            // 皮试警告
            if (detailReq.getSkinTest() != null && detailReq.getSkinTest().equals("需要")) {
                warnings.add("药品[" + detailReq.getDrugName() + "]需要皮试");
            }

            details.add(detail);
        }

        prescription.setTotalAmount(totalAmount);
        prescription = prescriptionRepository.save(prescription);

        // 保存明细
        for (PrescriptionDetail detail : details) {
            detail.setPrescriptionId(prescription.getId());
        }
        detailRepository.saveAll(details);

        log.info("处方开立成功: prescriptionId={}, prescriptionNo={}",
                prescription.getId(), prescription.getPrescriptionNo());

        return buildPrescriptionResult(prescription, details, warnings);
    }

    @Override
    public Optional<OutpatientPrescription> findById(String id) {
        return prescriptionRepository.findById(id);
    }

    @Override
    public Optional<OutpatientPrescription> findByPrescriptionNo(String prescriptionNo) {
        return prescriptionRepository.findByPrescriptionNo(prescriptionNo);
    }

    @Override
    public PrescriptionResultVO getPrescriptionDetail(String id) {
        OutpatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("处方不存在"));

        List<PrescriptionDetail> details = detailRepository.findByPrescriptionId(id);
        return buildPrescriptionResult(prescription, details, new ArrayList<>());
    }

    @Override
    public PageResult<OutpatientPrescription> listPrescriptions(String patientId, String doctorId,
            String payStatus, String status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<OutpatientPrescription> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (doctorId != null && !doctorId.isEmpty()) {
                predicates.add(cb.equal(root.get("doctorId"), doctorId));
            }
            if (payStatus != null && !payStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("payStatus"), payStatus));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("prescriptionDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("prescriptionDate"), endDate));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<OutpatientPrescription> page = prescriptionRepository.findAll(spec, pageable);
        return PageResult.of(page.getContent(), page.getTotalElements(),
                pageable.getPageNumber() + 1, pageable.getPageSize());
    }

    @Override
    public List<OutpatientPrescription> listPrescriptionsByRegistration(String registrationId) {
        return prescriptionRepository.findByRegistrationId(registrationId);
    }

    @Override
    public List<OutpatientPrescription> listPatientPrescriptions(String patientId) {
        return prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId);
    }

    @Override
    @Transactional
    public void voidPrescription(String id, String reason) {
        log.info("作废处方: id={}, reason={}", id, reason);

        OutpatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("处方不存在"));

        if (prescription.getPayStatus().equals("已收费")) {
            throw new IllegalArgumentException("已收费处方无法作废，请先退费");
        }

        prescription.setStatus("已作废");
        prescription.setRemark(reason);
        prescriptionRepository.save(prescription);

        // 删除处方明细
        detailRepository.deleteByPrescriptionId(id);

        log.info("处方作废成功: id={}", id);
    }

    @Override
    @Transactional
    public OutpatientPrescription auditPrescription(String id, boolean approved,
            String auditorId, String auditorName, String remark) {
        log.info("审核处方: id={}, approved={}", id, approved);

        OutpatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("处方不存在"));

        prescription.setAuditStatus(approved ? "审核通过" : "审核不通过");
        prescription.setAuditorId(auditorId);
        prescription.setAuditorName(auditorName);
        prescription.setAuditTime(LocalDateTime.now());
        prescription.setAuditRemark(remark);

        if (!approved) {
            prescription.setStatus("已作废");
        }

        prescription = prescriptionRepository.save(prescription);
        log.info("处方审核完成: id={}", id);

        return prescription;
    }

    @Override
    public PrescriptionResultVO calculateAmount(String prescriptionId) {
        OutpatientPrescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("处方不存在"));

        List<PrescriptionDetail> details = detailRepository.findByPrescriptionId(prescriptionId);
        BigDecimal totalAmount = details.stream()
                .map(PrescriptionDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        prescription.setTotalAmount(totalAmount);
        prescriptionRepository.save(prescription);

        return buildPrescriptionResult(prescription, details, new ArrayList<>());
    }

    @Override
    @Transactional
    public PrescriptionResultVO updatePrescription(String id, PrescriptionCreateRequest request) {
        log.info("更新处方: id={}", id);

        OutpatientPrescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("处方不存在"));

        if (!prescription.getStatus().equals("有效")) {
            throw new IllegalArgumentException("只能修改有效状态的处方");
        }

        if (prescription.getPayStatus().equals("已收费")) {
            throw new IllegalArgumentException("已收费处方无法修改");
        }

        // 删除旧明细
        detailRepository.deleteByPrescriptionId(id);

        // 更新处方信息
        prescription.setPrescriptionType(request.getPrescriptionType());
        prescription.setDiagnosisCode(request.getDiagnosisCode());
        prescription.setDiagnosisName(request.getDiagnosisName());
        prescription.setRemark(request.getRemark());

        // 创建新明细并计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PrescriptionDetail> details = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (PrescriptionCreateRequest.PrescriptionDetailRequest detailReq : request.getDetails()) {
            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setPrescriptionId(id);
            detail.setDrugId(detailReq.getDrugId());
            detail.setDrugName(detailReq.getDrugName());
            detail.setDrugSpec(detailReq.getDrugSpec());
            detail.setDrugUnit(detailReq.getDrugUnit());
            detail.setDrugForm(detailReq.getDrugForm());
            detail.setQuantity(detailReq.getQuantity());
            detail.setDosage(detailReq.getDosage());
            detail.setFrequency(detailReq.getFrequency());
            detail.setDays(detailReq.getDays());
            detail.setRoute(detailReq.getRoute());
            detail.setUnitPrice(detailReq.getUnitPrice() != null ? detailReq.getUnitPrice() : BigDecimal.ZERO);
            detail.setGroupNo(detailReq.getGroupNo());
            detail.setSkinTest(detailReq.getSkinTest());
            detail.setIsEssential(detailReq.getIsEssential());
            detail.setIsMedicalInsurance(detailReq.getIsMedicalInsurance());
            detail.setRemark(detailReq.getRemark());

            BigDecimal amount = detail.getUnitPrice().multiply(detail.getQuantity());
            detail.setAmount(amount);
            totalAmount = totalAmount.add(amount);

            if (detailReq.getSkinTest() != null && detailReq.getSkinTest().equals("需要")) {
                warnings.add("药品[" + detailReq.getDrugName() + "]需要皮试");
            }

            details.add(detail);
        }

        prescription.setTotalAmount(totalAmount);
        prescription = prescriptionRepository.save(prescription);
        detailRepository.saveAll(details);

        log.info("处方更新成功: id={}", id);

        return buildPrescriptionResult(prescription, details, warnings);
    }

    /**
     * 生成处方号
     */
    private String generatePrescriptionNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "RX" + LocalDate.now().format(formatter) +
                String.format("%04d", System.currentTimeMillis() % 10000);
    }

    /**
     * 构建处方结果
     */
    private PrescriptionResultVO buildPrescriptionResult(OutpatientPrescription prescription,
            List<PrescriptionDetail> details, List<String> warnings) {
        PrescriptionResultVO result = new PrescriptionResultVO();
        result.setPrescriptionId(prescription.getId());
        result.setPrescriptionNo(prescription.getPrescriptionNo());
        result.setTotalAmount(prescription.getTotalAmount());
        result.setWarnings(warnings);

        List<PrescriptionResultVO.PrescriptionDetailVO> detailVOs = details.stream()
                .map(d -> {
                    PrescriptionResultVO.PrescriptionDetailVO vo = new PrescriptionResultVO.PrescriptionDetailVO();
                    vo.setDrugId(d.getDrugId());
                    vo.setDrugName(d.getDrugName());
                    vo.setDrugSpec(d.getDrugSpec());
                    vo.setQuantity(d.getQuantity());
                    vo.setDosage(d.getDosage());
                    vo.setFrequency(d.getFrequency());
                    vo.setDays(d.getDays());
                    vo.setRoute(d.getRoute());
                    vo.setUnitPrice(d.getUnitPrice());
                    vo.setAmount(d.getAmount());
                    vo.setSkinTest(d.getSkinTest());
                    return vo;
                })
                .collect(Collectors.toList());

        result.setDetails(detailVOs);
        return result;
    }
}