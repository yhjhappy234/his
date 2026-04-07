package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.outpatient.dto.BillingSettleRequest;
import com.yhj.his.module.outpatient.entity.BillingItem;
import com.yhj.his.module.outpatient.entity.BillingSettlement;
import com.yhj.his.module.outpatient.repository.BillingItemRepository;
import com.yhj.his.module.outpatient.repository.BillingSettlementRepository;
import com.yhj.his.module.outpatient.service.BillingItemService;
import com.yhj.his.module.outpatient.service.BillingSettlementService;
import com.yhj.his.module.outpatient.vo.BillingResultVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收费结算服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingSettlementServiceImpl implements BillingSettlementService {

    private final BillingSettlementRepository billingSettlementRepository;
    private final BillingItemRepository billingItemRepository;
    private final BillingItemService billingItemService;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public BillingResultVO settle(BillingSettleRequest request) {
        // 查询待收费项目
        List<BillingItem> items = billingItemRepository.findAllById(request.getItemIds());

        if (items.isEmpty()) {
            throw new BusinessException("没有收费项目");
        }

        // 检查收费状态
        for (BillingItem item : items) {
            if (!item.getPayStatus().equals("未收费")) {
                throw new BusinessException("部分项目已收费或已退费");
            }
            if (!item.getRegistrationId().equals(request.getRegistrationId())) {
                throw new BusinessException("收费项目挂号信息不一致");
            }
            if (!item.getPatientId().equals(request.getPatientId())) {
                throw new BusinessException("收费项目患者信息不一致");
            }
        }

        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(BillingItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算医保支付和自费支付
        BigDecimal insuranceAmount = BigDecimal.ZERO;
        BigDecimal selfPayAmount = BigDecimal.ZERO;

        if (request.getPayments() != null) {
            for (BillingSettleRequest.PaymentDto payment : request.getPayments()) {
                if (payment.getPayMethod().equals("医保卡")) {
                    insuranceAmount = insuranceAmount.add(payment.getAmount());
                } else {
                    selfPayAmount = selfPayAmount.add(payment.getAmount());
                }
            }
        } else {
            selfPayAmount = totalAmount;
        }

        // 创建结算记录
        BillingSettlement settlement = new BillingSettlement();
        settlement.setSettlementNo(generateSettlementNo());
        settlement.setInvoiceNo(generateInvoiceNo());
        settlement.setRegistrationId(request.getRegistrationId());
        settlement.setPatientId(request.getPatientId());
        settlement.setPatientName(items.get(0).getPatientName());
        settlement.setTotalAmount(totalAmount);
        settlement.setInsuranceAmount(insuranceAmount);
        settlement.setSelfPayAmount(selfPayAmount);
        settlement.setDiscountAmount(BigDecimal.ZERO);
        settlement.setActualAmount(totalAmount);
        settlement.setPayMethod(request.getPayments() != null ?
                request.getPayments().stream().map(BillingSettleRequest.PaymentDto::getPayMethod).collect(Collectors.joining(",")) : "现金");
        settlement.setStatus("已结算");
        settlement.setSettleTime(LocalDateTime.now());
        settlement.setRemark(request.getRemark());

        BillingSettlement saved = billingSettlementRepository.save(settlement);

        // 更新收费项目状态
        billingItemService.batchUpdatePayStatus(request.getItemIds(), "已收费");

        // 更新收费项目的发票号和结算信息
        for (BillingItem item : items) {
            item.setInvoiceNo(saved.getInvoiceNo());
            item.setPayTime(LocalDateTime.now());
            billingItemRepository.save(item);
        }

        log.info("收费结算成功: settlementNo={}, invoiceNo={}", saved.getSettlementNo(), saved.getInvoiceNo());
        return buildBillingResult(saved);
    }

    @Override
    public Optional<BillingSettlement> findById(String id) {
        return billingSettlementRepository.findById(id);
    }

    @Override
    public Optional<BillingSettlement> findBySettlementNo(String settlementNo) {
        return billingSettlementRepository.findBySettlementNo(settlementNo);
    }

    @Override
    public Optional<BillingSettlement> findByInvoiceNo(String invoiceNo) {
        return billingSettlementRepository.findByInvoiceNo(invoiceNo);
    }

    @Override
    public BillingSettlement getSettlementDetail(String id) {
        return findById(id).orElseThrow(() -> new BusinessException("结算记录不存在"));
    }

    @Override
    public PageResult<BillingSettlement> listSettlements(String patientId, String registrationId, String status, Pageable pageable) {
        Specification<BillingSettlement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (registrationId != null && !registrationId.isEmpty()) {
                predicates.add(cb.equal(root.get("registrationId"), registrationId));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<BillingSettlement> page = billingSettlementRepository.findAll(spec, pageable);
        return PageResult.of(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<BillingSettlement> listPatientSettlements(String patientId) {
        Specification<BillingSettlement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("patientId"), patientId));
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return billingSettlementRepository.findAll(spec);
    }

    @Override
    @Transactional
    public BillingResultVO refund(String settlementId, String reason) {
        BillingSettlement settlement = getSettlementDetail(settlementId);

        if (settlement.getStatus().equals("已退费")) {
            throw new BusinessException("结算已退费");
        }

        // 查询关联的收费项目
        List<BillingItem> items = billingItemRepository.findByRegistrationId(settlement.getRegistrationId()).stream()
                .filter(item -> item.getInvoiceNo().equals(settlement.getInvoiceNo()))
                .collect(Collectors.toList());

        // 更新收费项目状态
        for (BillingItem item : items) {
            item.setPayStatus("已退费");
            billingItemRepository.save(item);
        }

        // 更新结算状态
        settlement.setStatus("已退费");
        settlement.setRemark(reason);
        billingSettlementRepository.save(settlement);

        log.info("退费成功: settlementId={}", settlementId);
        return buildBillingResult(settlement);
    }

    @Override
    public String generateInvoiceNo() {
        return sequenceGenerator.generate("INV", 8);
    }

    @Override
    public String generateSettlementNo() {
        return sequenceGenerator.generate("SET", 8);
    }

    private BillingResultVO buildBillingResult(BillingSettlement settlement) {
        BillingResultVO vo = new BillingResultVO();
        vo.setBillId(settlement.getId());
        vo.setInvoiceNo(settlement.getInvoiceNo());
        vo.setTotalAmount(settlement.getTotalAmount());
        vo.setInsuranceAmount(settlement.getInsuranceAmount());
        vo.setSelfPayAmount(settlement.getSelfPayAmount());
        vo.setActualAmount(settlement.getActualAmount());
        return vo;
    }
}