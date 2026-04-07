package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.PrepaymentDTO;
import com.yhj.his.module.finance.vo.InvoiceVO;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.Prepayment;
import com.yhj.his.module.finance.repository.PrepaymentRepository;
import com.yhj.his.module.finance.service.InvoiceService;
import com.yhj.his.module.finance.service.PrepaymentService;
import com.yhj.his.module.finance.vo.PrepaymentBalanceVO;
import com.yhj.his.module.finance.vo.PrepaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预交金服务实现
 */
@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final PrepaymentRepository prepaymentRepository;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public PrepaymentVO deposit(PrepaymentDTO dto, String operatorId, String operatorName) {
        // 计算当前余额
        BigDecimal currentBalance = calculateTotalDeposit(dto.getAdmissionId());
        BigDecimal balanceAfter = currentBalance.add(dto.getDepositAmount());

        Prepayment prepayment = new Prepayment();
        prepayment.setPrepaymentNo(generatePrepaymentNo());
        prepayment.setAdmissionId(dto.getAdmissionId());
        prepayment.setPatientId(dto.getPatientId());
        prepayment.setPatientName(dto.getPatientName());
        prepayment.setDepositType(Prepayment.DepositType.DEPOSIT);
        prepayment.setDepositAmount(dto.getDepositAmount());
        prepayment.setPaymentMethod(Prepayment.PaymentMethod.valueOf(dto.getPaymentMethod()));
        prepayment.setBalanceBefore(currentBalance);
        prepayment.setBalanceAfter(balanceAfter);
        prepayment.setOperatorId(operatorId);
        prepayment.setOperatorName(operatorName);
        prepayment.setOperateTime(LocalDateTime.now());
        prepayment.setRemark(dto.getRemark());
        prepayment.setStatus(Prepayment.PrepaymentStatus.NORMAL);

        prepaymentRepository.save(prepayment);

        // 创建收据
        InvoiceVO invoice = invoiceService.createInvoice(prepayment.getId(), Invoice.BillingType.PREPAYMENT.name(), operatorId, operatorName);
        prepayment.setReceiptNo(invoice.getInvoiceNo());
        prepaymentRepository.save(prepayment);

        return toVO(prepayment);
    }

    @Override
    @Transactional
    public PrepaymentVO refund(String admissionId, BigDecimal refundAmount, String refundMethod, String reason,
                                String operatorId, String operatorName) {
        // 计算当前余额
        BigDecimal currentBalance = calculateTotalDeposit(admissionId);
        if (currentBalance.compareTo(refundAmount) < 0) {
            throw new BusinessException("预交金余额不足，无法退还");
        }

        BigDecimal balanceAfter = currentBalance.subtract(refundAmount);

        Prepayment prepayment = new Prepayment();
        prepayment.setPrepaymentNo(generatePrepaymentNo());
        prepayment.setAdmissionId(admissionId);
        prepayment.setDepositType(Prepayment.DepositType.REFUND);
        prepayment.setDepositAmount(refundAmount);
        prepayment.setPaymentMethod(Prepayment.PaymentMethod.valueOf(refundMethod));
        prepayment.setBalanceBefore(currentBalance);
        prepayment.setBalanceAfter(balanceAfter);
        prepayment.setOperatorId(operatorId);
        prepayment.setOperatorName(operatorName);
        prepayment.setOperateTime(LocalDateTime.now());
        prepayment.setRemark(reason);
        prepayment.setStatus(Prepayment.PrepaymentStatus.NORMAL);

        prepaymentRepository.save(prepayment);

        return toVO(prepayment);
    }

    @Override
    public PrepaymentBalanceVO getBalance(String admissionId) {
        BigDecimal totalDeposit = calculateTotalDeposit(admissionId);

        PrepaymentBalanceVO vo = new PrepaymentBalanceVO();
        vo.setAdmissionId(admissionId);
        vo.setTotalDeposit(totalDeposit);
        vo.setCurrentBalance(totalDeposit);
        vo.setUsedAmount(BigDecimal.ZERO);
        vo.setEstimatedCost(BigDecimal.ZERO);

        // 设置余额状态
        BigDecimal warningThreshold = BigDecimal.valueOf(1000);
        if (totalDeposit.compareTo(warningThreshold) < 0) {
            vo.setBalanceStatus("INSUFFICIENT");
        } else if (totalDeposit.compareTo(warningThreshold.multiply(BigDecimal.valueOf(2))) < 0) {
            vo.setBalanceStatus("WARNING");
        } else {
            vo.setBalanceStatus("SUFFICIENT");
        }
        vo.setWarningThreshold(warningThreshold);

        return vo;
    }

    @Override
    public List<PrepaymentVO> listByAdmissionId(String admissionId) {
        List<Prepayment> prepayments = prepaymentRepository.findByAdmissionIdAndStatus(admissionId, Prepayment.PrepaymentStatus.NORMAL);
        return prepayments.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<PrepaymentVO> listByPatientId(String patientId) {
        List<Prepayment> prepayments = prepaymentRepository.findByPatientId(patientId);
        return prepayments.stream().filter(p -> !Boolean.TRUE.equals(p.getDeleted())).map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PrepaymentVO getByPrepaymentNo(String prepaymentNo) {
        Prepayment prepayment = prepaymentRepository.findByPrepaymentNo(prepaymentNo)
                .orElseThrow(() -> new BusinessException("预交金记录不存在: " + prepaymentNo));
        return toVO(prepayment);
    }

    @Override
    public PageResult<PrepaymentVO> pageList(String patientId, String admissionId, String depositType, int pageNum, int pageSize) {
        Specification<Prepayment> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (patientId != null && !patientId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("patientId"), patientId));
            }
            if (admissionId != null && !admissionId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("admissionId"), admissionId));
            }
            if (depositType != null && !depositType.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("depositType"), Prepayment.DepositType.valueOf(depositType)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<Prepayment> page = prepaymentRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<PrepaymentVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public BigDecimal calculateTotalDeposit(String admissionId) {
        BigDecimal deposit = prepaymentRepository.sumDepositByAdmissionId(admissionId);
        return deposit != null ? deposit : BigDecimal.ZERO;
    }

    @Override
    public boolean checkDepositWarning(String admissionId, BigDecimal estimatedCost) {
        BigDecimal currentBalance = calculateTotalDeposit(admissionId);
        BigDecimal warningThreshold = BigDecimal.valueOf(1000);
        return currentBalance.compareTo(estimatedCost.subtract(warningThreshold)) < 0;
    }

    /**
     * 生成预交金单号
     */
    private String generatePrepaymentNo() {
        return "PRE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * 实体转VO
     */
    private PrepaymentVO toVO(Prepayment prepayment) {
        PrepaymentVO vo = new PrepaymentVO();
        vo.setId(prepayment.getId());
        vo.setPrepaymentNo(prepayment.getPrepaymentNo());
        vo.setReceiptNo(prepayment.getReceiptNo());
        vo.setAdmissionId(prepayment.getAdmissionId());
        vo.setPatientId(prepayment.getPatientId());
        vo.setPatientName(prepayment.getPatientName());
        vo.setDepositType(prepayment.getDepositType() != null ? prepayment.getDepositType().name() : null);
        vo.setDepositTypeDesc(prepayment.getDepositType() != null ? prepayment.getDepositType().getDescription() : null);
        vo.setDepositAmount(prepayment.getDepositAmount());
        vo.setPaymentMethod(prepayment.getPaymentMethod() != null ? prepayment.getPaymentMethod().name() : null);
        vo.setPaymentMethodDesc(prepayment.getPaymentMethod() != null ? prepayment.getPaymentMethod().getDescription() : null);
        vo.setBalanceBefore(prepayment.getBalanceBefore());
        vo.setBalanceAfter(prepayment.getBalanceAfter());
        vo.setOperatorId(prepayment.getOperatorId());
        vo.setOperatorName(prepayment.getOperatorName());
        vo.setOperateTime(prepayment.getOperateTime());
        vo.setRemark(prepayment.getRemark());
        vo.setStatus(prepayment.getStatus() != null ? prepayment.getStatus().name() : null);
        return vo;
    }
}