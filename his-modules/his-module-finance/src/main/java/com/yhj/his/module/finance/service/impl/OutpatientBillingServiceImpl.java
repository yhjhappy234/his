package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.OutpatientRefundDTO;
import com.yhj.his.module.finance.dto.OutpatientSettleDTO;
import com.yhj.his.module.finance.vo.InvoiceVO;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.entity.OutpatientBillingItem;
import com.yhj.his.module.finance.entity.PriceItem;
import com.yhj.his.module.finance.repository.OutpatientBillingItemRepository;
import com.yhj.his.module.finance.repository.OutpatientBillingRepository;
import com.yhj.his.module.finance.repository.PriceItemRepository;
import com.yhj.his.module.finance.service.InsurancePolicyService;
import com.yhj.his.module.finance.service.InvoiceService;
import com.yhj.his.module.finance.service.OutpatientBillingService;
import com.yhj.his.module.finance.vo.InvoiceVO;
import com.yhj.his.module.finance.vo.OutpatientBillingItemVO;
import com.yhj.his.module.finance.vo.OutpatientBillingVO;
import com.yhj.his.module.finance.vo.PendingBillingItemVO;
import com.yhj.his.module.finance.vo.PendingBillingVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 门诊收费服务实现
 */
@Service
@RequiredArgsConstructor
public class OutpatientBillingServiceImpl implements OutpatientBillingService {

    private final OutpatientBillingRepository billingRepository;
    private final OutpatientBillingItemRepository billingItemRepository;
    private final PriceItemRepository priceItemRepository;
    private final InsurancePolicyService insurancePolicyService;
    private final InvoiceService invoiceService;

    @Override
    public PendingBillingVO getPendingItems(String visitId) {
        // TODO: 实际实现需要调用门诊管理模块获取待收费项目
        // 这里提供模拟数据结构
        PendingBillingVO vo = new PendingBillingVO();
        vo.setVisitId(visitId);
        vo.setItems(new ArrayList<>());
        vo.setTotalAmount(BigDecimal.ZERO);
        return vo;
    }

    @Override
    @Transactional
    public SettlementResultVO settle(OutpatientSettleDTO dto, String operatorId, String operatorName) {
        // 验证支付金额
        BigDecimal totalPayment = dto.getPayments().stream()
                .map(OutpatientSettleDTO.PaymentDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 创建收费记录
        OutpatientBilling billing = new OutpatientBilling();
        billing.setBillingNo(generateBillingNo());
        billing.setPatientId(dto.getPatientId());
        billing.setPatientName(dto.getPatientName());
        billing.setVisitId(dto.getVisitId());
        billing.setVisitNo(dto.getVisitNo());
        billing.setDeptId(dto.getDeptId());
        billing.setDeptName(dto.getDeptName());
        billing.setBillingDate(LocalDate.now());
        billing.setBillingTime(LocalDateTime.now());

        if (dto.getInsuranceType() != null) {
            billing.setInsuranceType(com.yhj.his.module.finance.entity.InsurancePolicy.InsuranceTypeEnum.valueOf(dto.getInsuranceType()));
            billing.setInsuranceCardNo(dto.getInsuranceCardNo());
        }

        billing.setPayments(JSONUtil.toJsonStr(dto.getPayments()));
        billing.setOperatorId(operatorId);
        billing.setOperatorName(operatorName);
        billing.setStatus(OutpatientBilling.BillingStatus.NORMAL);

        // TODO: 计算费用明细和医保报销
        billing.setTotalAmount(totalPayment);
        billing.setSelfPayAmount(totalPayment);
        billing.setInsuranceAmount(BigDecimal.ZERO);

        billingRepository.save(billing);

        // 创建发票
        InvoiceVO invoice = invoiceService.createInvoice(billing.getId(), Invoice.BillingType.OUTPATIENT.name(), operatorId, operatorName);
        billing.setInvoiceNo(invoice.getInvoiceNo());
        billingRepository.save(billing);

        // 返回结算结果
        SettlementResultVO result = new SettlementResultVO();
        result.setBillingId(billing.getId());
        result.setBillingNo(billing.getBillingNo());
        result.setInvoiceNo(billing.getInvoiceNo());
        result.setTotalAmount(billing.getTotalAmount());
        result.setInsuranceAmount(billing.getInsuranceAmount());
        result.setSelfPayAmount(billing.getSelfPayAmount());
        result.setSettlementTime(LocalDateTime.now());
        result.setItemIds(new ArrayList<>());

        return result;
    }

    @Override
    @Transactional
    public SettlementResultVO refund(OutpatientRefundDTO dto, String operatorId, String operatorName) {
        OutpatientBilling billing = billingRepository.findByBillingNo(dto.getBillingNo())
                .orElseThrow(() -> new BusinessException("收费记录不存在: " + dto.getBillingNo()));

        if (billing.getStatus() == OutpatientBilling.BillingStatus.REFUNDED) {
            throw new BusinessException("该收费记录已全部退费");
        }

        // 计算退费金额
        BigDecimal refundAmount = BigDecimal.ZERO;
        List<OutpatientBillingItem> items;

        if (dto.getItemIds() != null && !dto.getItemIds().isEmpty()) {
            // 部分退费
            items = billingItemRepository.findByBillingId(billing.getId());
            for (String itemId : dto.getItemIds()) {
                for (OutpatientBillingItem item : items) {
                    if (item.getId().equals(itemId) && item.getStatus() == OutpatientBillingItem.BillingItemStatus.NORMAL) {
                        refundAmount = refundAmount.add(item.getAmount());
                        item.setStatus(OutpatientBillingItem.BillingItemStatus.REFUNDED);
                        item.setRefundAmount(item.getAmount());
                        billingItemRepository.save(item);
                    }
                }
            }
            billing.setStatus(OutpatientBilling.BillingStatus.PARTIAL_REFUND);
        } else {
            // 全部退费
            items = billingItemRepository.findByBillingId(billing.getId());
            for (OutpatientBillingItem item : items) {
                if (item.getStatus() == OutpatientBillingItem.BillingItemStatus.NORMAL) {
                    refundAmount = refundAmount.add(item.getAmount());
                    item.setStatus(OutpatientBillingItem.BillingItemStatus.REFUNDED);
                    item.setRefundAmount(item.getAmount());
                    billingItemRepository.save(item);
                }
            }
            billing.setStatus(OutpatientBilling.BillingStatus.REFUNDED);
        }

        billing.setRefundAmount(refundAmount);
        billing.setRefundStatus(OutpatientBilling.RefundStatus.COMPLETED);
        billing.setRefundTime(LocalDateTime.now());
        billing.setRefundOperatorId(operatorId);
        billing.setRefundReason(dto.getRefundReason());
        billingRepository.save(billing);

        // TODO: 作废发票

        SettlementResultVO result = new SettlementResultVO();
        result.setBillingId(billing.getId());
        result.setBillingNo(billing.getBillingNo());
        result.setTotalAmount(billing.getTotalAmount());
        result.setRefundAmount(refundAmount);
        result.setSettlementTime(LocalDateTime.now());
        return result;
    }

    @Override
    public OutpatientBillingVO getByBillingNo(String billingNo) {
        OutpatientBilling billing = billingRepository.findByBillingNo(billingNo)
                .orElseThrow(() -> new BusinessException("收费记录不存在: " + billingNo));
        return toVO(billing);
    }

    @Override
    public OutpatientBillingVO getByInvoiceNo(String invoiceNo) {
        OutpatientBilling billing = billingRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new BusinessException("发票号对应的收费记录不存在: " + invoiceNo));
        return toVO(billing);
    }

    @Override
    public List<OutpatientBillingVO> listByVisitId(String visitId) {
        List<OutpatientBilling> billings = billingRepository.findByVisitId(visitId);
        return billings.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<OutpatientBillingVO> listByPatientId(String patientId) {
        List<OutpatientBilling> billings = billingRepository.findByPatientId(patientId);
        return billings.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<OutpatientBillingVO> pageList(String patientId, String billingDate, String status, int pageNum, int pageSize) {
        Specification<OutpatientBilling> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (patientId != null && !patientId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("patientId"), patientId));
            }
            if (billingDate != null && !billingDate.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("billingDate"), LocalDate.parse(billingDate)));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), OutpatientBilling.BillingStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<OutpatientBilling> page = billingRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<OutpatientBillingVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PendingBillingVO calculateFee(String visitId, String insuranceType) {
        // TODO: 实际实现需要调用门诊管理模块获取待收费项目并计算医保报销
        return getPendingItems(visitId);
    }

    @Override
    @Transactional
    public OutpatientBillingVO printInvoice(String billingNo) {
        OutpatientBilling billing = billingRepository.findByBillingNo(billingNo)
                .orElseThrow(() -> new BusinessException("收费记录不存在: " + billingNo));
        invoiceService.printInvoice(billing.getInvoiceNo());
        return toVO(billing);
    }

    /**
     * 生成收费单号
     */
    private String generateBillingNo() {
        return "BIL" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
    }

    /**
     * 实体转VO
     */
    private OutpatientBillingVO toVO(OutpatientBilling billing) {
        OutpatientBillingVO vo = new OutpatientBillingVO();
        vo.setId(billing.getId());
        vo.setBillingNo(billing.getBillingNo());
        vo.setInvoiceNo(billing.getInvoiceNo());
        vo.setPatientId(billing.getPatientId());
        vo.setPatientName(billing.getPatientName());
        vo.setVisitId(billing.getVisitId());
        vo.setVisitNo(billing.getVisitNo());
        vo.setDeptId(billing.getDeptId());
        vo.setDeptName(billing.getDeptName());
        vo.setBillingDate(billing.getBillingDate());
        vo.setBillingTime(billing.getBillingTime());
        vo.setTotalAmount(billing.getTotalAmount());
        vo.setDiscountAmount(billing.getDiscountAmount());
        vo.setInsuranceAmount(billing.getInsuranceAmount());
        vo.setSelfPayAmount(billing.getSelfPayAmount());
        vo.setRefundAmount(billing.getRefundAmount());
        vo.setInsuranceType(billing.getInsuranceType() != null ? billing.getInsuranceType().name() : null);
        vo.setInsuranceTypeDesc(billing.getInsuranceType() != null ? billing.getInsuranceType().getDescription() : null);
        vo.setInsuranceCardNo(billing.getInsuranceCardNo());
        vo.setOperatorId(billing.getOperatorId());
        vo.setOperatorName(billing.getOperatorName());
        vo.setStatus(billing.getStatus() != null ? billing.getStatus().name() : null);
        vo.setStatusDesc(billing.getStatus() != null ? billing.getStatus().getDescription() : null);
        vo.setRefundStatus(billing.getRefundStatus() != null ? billing.getRefundStatus().name() : null);
        vo.setRefundStatusDesc(billing.getRefundStatus() != null ? billing.getRefundStatus().getDescription() : null);

        // 查询明细
        List<OutpatientBillingItem> items = billingItemRepository.findByBillingId(billing.getId());
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));

        return vo;
    }

    /**
     * 明细实体转VO
     */
    private OutpatientBillingItemVO toItemVO(OutpatientBillingItem item) {
        OutpatientBillingItemVO vo = new OutpatientBillingItemVO();
        vo.setId(item.getId());
        vo.setItemId(item.getItemId());
        vo.setItemCode(item.getItemCode());
        vo.setItemName(item.getItemName());
        vo.setItemCategory(item.getItemCategory() != null ? item.getItemCategory().name() : null);
        vo.setItemCategoryDesc(item.getItemCategory() != null ? item.getItemCategory().getDescription() : null);
        vo.setItemUnit(item.getItemUnit());
        vo.setQuantity(item.getQuantity());
        vo.setUnitPrice(item.getUnitPrice());
        vo.setAmount(item.getAmount());
        vo.setInsuranceType(item.getInsuranceType() != null ? item.getInsuranceType().name() : null);
        vo.setInsuranceAmount(item.getInsuranceAmount());
        vo.setSelfPayAmount(item.getSelfPayAmount());
        vo.setPrescriptionId(item.getPrescriptionId());
        vo.setRequestId(item.getRequestId());
        vo.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
        vo.setRefundAmount(item.getRefundAmount());
        return vo;
    }
}