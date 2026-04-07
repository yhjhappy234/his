package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.entity.DailySettlement;
import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.repository.DailySettlementRepository;
import com.yhj.his.module.finance.repository.OutpatientBillingRepository;
import com.yhj.his.module.finance.service.DailySettlementService;
import com.yhj.his.module.finance.vo.DailySettlementVO;
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
 * 日结服务实现
 */
@Service
@RequiredArgsConstructor
public class DailySettlementServiceImpl implements DailySettlementService {

    private final DailySettlementRepository dailySettlementRepository;
    private final OutpatientBillingRepository outpatientBillingRepository;

    @Override
    @Transactional
    public DailySettlementVO performDailySettlement(String operatorId, String operatorName, LocalDate settlementDate) {
        // 检查是否已日结
        if (dailySettlementRepository.isSettled(settlementDate, operatorId)) {
            throw new BusinessException("该日期已确认日结，无法重复日结");
        }

        // 检查是否存在待确认的日结记录
        if (dailySettlementRepository.hasPendingSettlement(settlementDate, operatorId)) {
            throw new BusinessException("该日期存在待确认的日结记录，请先确认");
        }

        // 统计当日收费情况
        List<OutpatientBilling> billings = outpatientBillingRepository.findByOperatorAndDate(operatorId, settlementDate);

        BigDecimal cashAmount = BigDecimal.ZERO;
        BigDecimal cardAmount = BigDecimal.ZERO;
        BigDecimal wechatAmount = BigDecimal.ZERO;
        BigDecimal alipayAmount = BigDecimal.ZERO;
        BigDecimal insuranceAmount = BigDecimal.ZERO;
        BigDecimal prepaidAmount = BigDecimal.ZERO;
        int billingCount = 0;

        for (OutpatientBilling billing : billings) {
            billingCount++;
            insuranceAmount = insuranceAmount.add(billing.getInsuranceAmount() != null ? billing.getInsuranceAmount() : BigDecimal.ZERO);
            // TODO: 解析payments字段统计各支付方式金额
        }

        BigDecimal totalIncome = cashAmount.add(cardAmount).add(wechatAmount).add(alipayAmount).add(insuranceAmount).add(prepaidAmount);

        // 统计当日退费情况
        BigDecimal totalRefund = outpatientBillingRepository.sumRefundByOperatorAndDate(operatorId, settlementDate);
        if (totalRefund == null) {
            totalRefund = BigDecimal.ZERO;
        }
        Long refundCount = outpatientBillingRepository.countRefundByOperatorAndDate(operatorId, settlementDate);
        if (refundCount == null) {
            refundCount = 0L;
        }

        BigDecimal netIncome = totalIncome.subtract(totalRefund);

        // 创建日结记录
        DailySettlement settlement = new DailySettlement();
        settlement.setSettlementNo(generateSettlementNo());
        settlement.setSettlementDate(settlementDate);
        settlement.setOperatorId(operatorId);
        settlement.setOperatorName(operatorName);
        settlement.setCashAmount(cashAmount);
        settlement.setCardAmount(cardAmount);
        settlement.setWechatAmount(wechatAmount);
        settlement.setAlipayAmount(alipayAmount);
        settlement.setInsuranceAmount(insuranceAmount);
        settlement.setPrepaidAmount(prepaidAmount);
        settlement.setTotalIncome(totalIncome);
        settlement.setTotalRefund(totalRefund);
        settlement.setNetIncome(netIncome);
        settlement.setBillingCount(billingCount);
        settlement.setRefundCount(refundCount.intValue());
        settlement.setSettlementTime(LocalDateTime.now());
        settlement.setStatus(DailySettlement.DailySettlementStatus.PENDING);

        dailySettlementRepository.save(settlement);
        return toVO(settlement);
    }

    @Override
    @Transactional
    public DailySettlementVO confirmSettlement(String settlementNo, String confirmerId, String confirmerName) {
        DailySettlement settlement = dailySettlementRepository.findBySettlementNo(settlementNo)
                .orElseThrow(() -> new BusinessException("日结记录不存在: " + settlementNo));

        if (settlement.getStatus() == DailySettlement.DailySettlementStatus.CONFIRMED) {
            throw new BusinessException("日结记录已确认");
        }

        settlement.setStatus(DailySettlement.DailySettlementStatus.CONFIRMED);
        settlement.setConfirmTime(LocalDateTime.now());
        settlement.setConfirmerId(confirmerId);
        settlement.setConfirmerName(confirmerName);

        dailySettlementRepository.save(settlement);
        return toVO(settlement);
    }

    @Override
    public DailySettlementVO getBySettlementNo(String settlementNo) {
        DailySettlement settlement = dailySettlementRepository.findBySettlementNo(settlementNo)
                .orElseThrow(() -> new BusinessException("日结记录不存在: " + settlementNo));
        return toVO(settlement);
    }

    @Override
    public DailySettlementVO getByDateAndOperator(LocalDate settlementDate, String operatorId) {
        DailySettlement settlement = dailySettlementRepository.findBySettlementDateAndOperatorId(settlementDate, operatorId)
                .orElseThrow(() -> new BusinessException("未找到指定日期和操作员的日结记录"));
        return toVO(settlement);
    }

    @Override
    public List<DailySettlementVO> listByDate(LocalDate settlementDate) {
        List<DailySettlement> settlements = dailySettlementRepository.findBySettlementDate(settlementDate);
        return settlements.stream().filter(s -> !Boolean.TRUE.equals(s.getDeleted())).map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<DailySettlementVO> pageList(String operatorId, LocalDate startDate, LocalDate endDate, String status, int pageNum, int pageSize) {
        Specification<DailySettlement> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (operatorId != null && !operatorId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("operatorId"), operatorId));
            }
            if (startDate != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("settlementDate"), startDate));
            }
            if (endDate != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("settlementDate"), endDate));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), DailySettlement.DailySettlementStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<DailySettlement> page = dailySettlementRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("settlementDate").descending()));
        List<DailySettlementVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public boolean isSettled(LocalDate settlementDate, String operatorId) {
        return dailySettlementRepository.isSettled(settlementDate, operatorId);
    }

    @Override
    public DailySettlementVO getDailyReport(LocalDate settlementDate, String operatorId) {
        // 检查是否已有日结记录
        var existing = dailySettlementRepository.findBySettlementDateAndOperatorId(settlementDate, operatorId);
        if (existing.isPresent()) {
            return toVO(existing.get());
        }

        // 生成报表数据但不保存
        List<OutpatientBilling> billings = outpatientBillingRepository.findByOperatorAndDate(operatorId, settlementDate);

        DailySettlementVO vo = new DailySettlementVO();
        vo.setSettlementDate(settlementDate);
        vo.setOperatorId(operatorId);
        vo.setBillingCount(billings.size());

        BigDecimal totalIncome = BigDecimal.ZERO;
        for (OutpatientBilling billing : billings) {
            totalIncome = totalIncome.add(billing.getTotalAmount() != null ? billing.getTotalAmount() : BigDecimal.ZERO);
        }
        vo.setTotalIncome(totalIncome);

        BigDecimal totalRefund = outpatientBillingRepository.sumRefundByOperatorAndDate(operatorId, settlementDate);
        vo.setTotalRefund(totalRefund != null ? totalRefund : BigDecimal.ZERO);
        vo.setNetIncome(vo.getTotalIncome().subtract(vo.getTotalRefund()));

        Long refundCount = outpatientBillingRepository.countRefundByOperatorAndDate(operatorId, settlementDate);
        vo.setRefundCount(refundCount != null ? refundCount.intValue() : 0);

        vo.setStatus("PENDING");
        vo.setStatusDesc("待确认");

        return vo;
    }

    /**
     * 生成日结单号
     */
    private String generateSettlementNo() {
        return "DS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }

    /**
     * 实体转VO
     */
    private DailySettlementVO toVO(DailySettlement settlement) {
        DailySettlementVO vo = new DailySettlementVO();
        vo.setId(settlement.getId());
        vo.setSettlementNo(settlement.getSettlementNo());
        vo.setSettlementDate(settlement.getSettlementDate());
        vo.setOperatorId(settlement.getOperatorId());
        vo.setOperatorName(settlement.getOperatorName());
        vo.setCashAmount(settlement.getCashAmount());
        vo.setCardAmount(settlement.getCardAmount());
        vo.setWechatAmount(settlement.getWechatAmount());
        vo.setAlipayAmount(settlement.getAlipayAmount());
        vo.setInsuranceAmount(settlement.getInsuranceAmount());
        vo.setPrepaidAmount(settlement.getPrepaidAmount());
        vo.setTotalIncome(settlement.getTotalIncome());
        vo.setTotalRefund(settlement.getTotalRefund());
        vo.setNetIncome(settlement.getNetIncome());
        vo.setBillingCount(settlement.getBillingCount());
        vo.setRefundCount(settlement.getRefundCount());
        vo.setSettlementTime(settlement.getSettlementTime());
        vo.setConfirmTime(settlement.getConfirmTime());
        vo.setConfirmerId(settlement.getConfirmerId());
        vo.setConfirmerName(settlement.getConfirmerName());
        vo.setStatus(settlement.getStatus() != null ? settlement.getStatus().name() : null);
        vo.setStatusDesc(settlement.getStatus() != null ? settlement.getStatus().getDescription() : null);
        return vo;
    }
}