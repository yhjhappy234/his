package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.InpatientSettleDTO;
import com.yhj.his.module.finance.entity.InpatientSettlement;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.repository.InpatientSettlementRepository;
import com.yhj.his.module.finance.repository.PrepaymentRepository;
import com.yhj.his.module.finance.service.InpatientSettlementService;
import com.yhj.his.module.finance.service.InsurancePolicyService;
import com.yhj.his.module.finance.service.InvoiceService;
import com.yhj.his.module.finance.service.PrepaymentService;
import com.yhj.his.module.finance.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.finance.vo.InpatientSettlementVO;
import com.yhj.his.module.finance.vo.InvoiceVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 住院结算服务实现
 */
@Service
@RequiredArgsConstructor
public class InpatientSettlementServiceImpl implements InpatientSettlementService {

    private final InpatientSettlementRepository settlementRepository;
    private final PrepaymentRepository prepaymentRepository;
    private final PrepaymentService prepaymentService;
    private final InsurancePolicyService insurancePolicyService;
    private final InvoiceService invoiceService;

    @Override
    public InpatientFeeSummaryVO getFeeSummary(String admissionId) {
        // TODO: 实际实现需要调用住院管理模块获取费用明细
        BigDecimal totalDeposit = prepaymentService.calculateTotalDeposit(admissionId);

        InpatientFeeSummaryVO vo = new InpatientFeeSummaryVO();
        vo.setAdmissionId(admissionId);
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setTotalDeposit(totalDeposit);
        vo.setDepositBalance(totalDeposit);

        return vo;
    }

    @Override
    @Transactional
    public SettlementResultVO settle(InpatientSettleDTO dto, String operatorId, String operatorName) {
        // 检查是否已结算
        if (settlementRepository.isAdmissionSettled(dto.getAdmissionId())) {
            throw new BusinessException("该住院记录已结算");
        }

        // 计算预交金总额
        BigDecimal totalDeposit = prepaymentService.calculateTotalDeposit(dto.getAdmissionId());

        // TODO: 实际实现需要从住院管理模块获取费用明细
        // 这里使用模拟数据
        BigDecimal totalAmount = BigDecimal.valueOf(5000);
        BigDecimal bedFee = BigDecimal.valueOf(500);
        BigDecimal drugFee = BigDecimal.valueOf(1500);
        BigDecimal examFee = BigDecimal.valueOf(800);
        BigDecimal testFee = BigDecimal.valueOf(600);
        BigDecimal treatmentFee = BigDecimal.valueOf(1000);
        BigDecimal materialFee = BigDecimal.valueOf(200);
        BigDecimal nursingFee = BigDecimal.valueOf(300);
        BigDecimal otherFee = BigDecimal.valueOf(100);

        // 计算医保报销
        BigDecimal insuranceAmount = BigDecimal.ZERO;
        BigDecimal selfPayAmount = totalAmount;

        if (dto.getInsuranceType() != null && !"SELF".equals(dto.getInsuranceType())) {
            // 使用默认比例计算医保报销(甲类80%)
            BigDecimal classAAmount = drugFee.add(examFee).add(testFee).add(treatmentFee);
            InsurancePolicyService.InsuranceSettlementResult result = insurancePolicyService.calculateSettlement(
                    dto.getInsuranceType(), totalAmount, classAAmount, BigDecimal.ZERO, BigDecimal.ZERO);
            insuranceAmount = result.totalInsuranceAmount();
            selfPayAmount = result.selfPayAmount();
        }

        // 计算补交/退还金额
        BigDecimal netAmount = selfPayAmount.subtract(totalDeposit);
        BigDecimal supplementAmount = BigDecimal.ZERO;
        BigDecimal refundAmount = BigDecimal.ZERO;

        if (netAmount.compareTo(BigDecimal.ZERO) > 0) {
            supplementAmount = netAmount;
        } else {
            refundAmount = netAmount.abs();
        }

        // 创建结算记录
        InpatientSettlement settlement = new InpatientSettlement();
        settlement.setSettlementNo(generateSettlementNo());
        settlement.setAdmissionId(dto.getAdmissionId());
        settlement.setPatientId(dto.getPatientId());
        settlement.setPatientName(dto.getPatientName());
        settlement.setTotalAmount(totalAmount);
        settlement.setBedFee(bedFee);
        settlement.setDrugFee(drugFee);
        settlement.setExamFee(examFee);
        settlement.setTestFee(testFee);
        settlement.setTreatmentFee(treatmentFee);
        settlement.setMaterialFee(materialFee);
        settlement.setNursingFee(nursingFee);
        settlement.setOtherFee(otherFee);
        settlement.setTotalDeposit(totalDeposit);
        settlement.setInsuranceAmount(insuranceAmount);
        settlement.setSelfPayAmount(selfPayAmount);
        settlement.setRefundAmount(refundAmount);
        settlement.setSupplementAmount(supplementAmount);

        if (dto.getInsuranceType() != null) {
            settlement.setInsuranceType(com.yhj.his.module.finance.entity.InsurancePolicy.InsuranceTypeEnum.valueOf(dto.getInsuranceType()));
            settlement.setInsuranceCardNo(dto.getInsuranceCardNo());
        }

        settlement.setSettlementTime(LocalDateTime.now());
        settlement.setOperatorId(operatorId);
        settlement.setOperatorName(operatorName);
        settlement.setStatus(InpatientSettlement.SettlementStatus.NORMAL);

        settlementRepository.save(settlement);

        // 创建发票
        InvoiceVO invoice = invoiceService.createInvoice(settlement.getId(), Invoice.BillingType.INPATIENT.name(), operatorId, operatorName);
        settlement.setInvoiceNo(invoice.getInvoiceNo());
        settlementRepository.save(settlement);

        // 如果有退还金额，退还预交金
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            prepaymentService.refund(dto.getAdmissionId(), refundAmount, "CASH", "出院结算退还", operatorId, operatorName);
        }

        // 返回结算结果
        SettlementResultVO result = new SettlementResultVO();
        result.setBillingId(settlement.getId());
        result.setBillingNo(settlement.getSettlementNo());
        result.setInvoiceNo(settlement.getInvoiceNo());
        result.setTotalAmount(totalAmount);
        result.setInsuranceAmount(insuranceAmount);
        result.setSelfPayAmount(selfPayAmount);
        result.setPrepaidUsed(totalDeposit);
        result.setRefundAmount(refundAmount);
        result.setSupplementAmount(supplementAmount);
        result.setSettlementTime(LocalDateTime.now());

        return result;
    }

    @Override
    public InpatientSettlementVO getBySettlementNo(String settlementNo) {
        InpatientSettlement settlement = settlementRepository.findBySettlementNo(settlementNo)
                .orElseThrow(() -> new BusinessException("结算记录不存在: " + settlementNo));
        return toVO(settlement);
    }

    @Override
    public InpatientSettlementVO getByInvoiceNo(String invoiceNo) {
        InpatientSettlement settlement = settlementRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new BusinessException("发票号对应的结算记录不存在: " + invoiceNo));
        return toVO(settlement);
    }

    @Override
    public InpatientSettlementVO getByAdmissionId(String admissionId) {
        InpatientSettlement settlement = settlementRepository.findByAdmissionId(admissionId)
                .orElseThrow(() -> new BusinessException("住院记录未结算: " + admissionId));
        return toVO(settlement);
    }

    @Override
    public List<InpatientSettlementVO> listByPatientId(String patientId) {
        List<InpatientSettlement> settlements = settlementRepository.findByPatientId(patientId);
        return settlements.stream().filter(s -> !Boolean.TRUE.equals(s.getDeleted())).map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<InpatientSettlementVO> pageList(String patientId, String settlementDate, String status, int pageNum, int pageSize) {
        Specification<InpatientSettlement> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (patientId != null && !patientId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("patientId"), patientId));
            }
            if (settlementDate != null && !settlementDate.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("settlementTime"), LocalDate.parse(settlementDate)));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), InpatientSettlement.SettlementStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<InpatientSettlement> page = settlementRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<InpatientSettlementVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public InpatientFeeSummaryVO calculateSettlement(String admissionId, String insuranceType) {
        return getFeeSummary(admissionId);
    }

    @Override
    public boolean isSettled(String admissionId) {
        return settlementRepository.isAdmissionSettled(admissionId);
    }

    @Override
    @Transactional
    public void voidSettlement(String settlementNo, String reason, String operatorId) {
        InpatientSettlement settlement = settlementRepository.findBySettlementNo(settlementNo)
                .orElseThrow(() -> new BusinessException("结算记录不存在: " + settlementNo));

        settlement.setStatus(InpatientSettlement.SettlementStatus.CANCELLED);
        settlement.setRemark("作废原因: " + reason);
        settlementRepository.save(settlement);

        // TODO: 作废发票，恢复预交金等后续处理
    }

    /**
     * 生成结算单号
     */
    private String generateSettlementNo() {
        return "SET" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
    }

    /**
     * 实体转VO
     */
    private InpatientSettlementVO toVO(InpatientSettlement settlement) {
        InpatientSettlementVO vo = new InpatientSettlementVO();
        vo.setId(settlement.getId());
        vo.setSettlementNo(settlement.getSettlementNo());
        vo.setInvoiceNo(settlement.getInvoiceNo());
        vo.setAdmissionId(settlement.getAdmissionId());
        vo.setPatientId(settlement.getPatientId());
        vo.setPatientName(settlement.getPatientName());
        vo.setAdmissionDate(settlement.getAdmissionDate());
        vo.setDischargeDate(settlement.getDischargeDate());
        vo.setHospitalDays(settlement.getHospitalDays());
        vo.setTotalAmount(settlement.getTotalAmount());
        vo.setBedFee(settlement.getBedFee());
        vo.setDrugFee(settlement.getDrugFee());
        vo.setExamFee(settlement.getExamFee());
        vo.setTestFee(settlement.getTestFee());
        vo.setTreatmentFee(settlement.getTreatmentFee());
        vo.setMaterialFee(settlement.getMaterialFee());
        vo.setNursingFee(settlement.getNursingFee());
        vo.setOtherFee(settlement.getOtherFee());
        vo.setTotalDeposit(settlement.getTotalDeposit());
        vo.setInsuranceAmount(settlement.getInsuranceAmount());
        vo.setSelfPayAmount(settlement.getSelfPayAmount());
        vo.setRefundAmount(settlement.getRefundAmount());
        vo.setSupplementAmount(settlement.getSupplementAmount());
        vo.setInsuranceType(settlement.getInsuranceType() != null ? settlement.getInsuranceType().name() : null);
        vo.setInsuranceTypeDesc(settlement.getInsuranceType() != null ? settlement.getInsuranceType().getDescription() : null);
        vo.setInsuranceCardNo(settlement.getInsuranceCardNo());
        vo.setInsuranceClaimNo(settlement.getInsuranceClaimNo());
        vo.setSettlementTime(settlement.getSettlementTime());
        vo.setOperatorId(settlement.getOperatorId());
        vo.setOperatorName(settlement.getOperatorName());
        vo.setStatus(settlement.getStatus() != null ? settlement.getStatus().name() : null);
        return vo;
    }
}