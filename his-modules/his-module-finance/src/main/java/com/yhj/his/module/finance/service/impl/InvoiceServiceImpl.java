package com.yhj.his.module.finance.service.impl;

import cn.hutool.core.util.IdUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.finance.dto.InvoiceVoidDTO;
import com.yhj.his.module.finance.entity.Invoice;
import com.yhj.his.module.finance.entity.OutpatientBilling;
import com.yhj.his.module.finance.entity.InpatientSettlement;
import com.yhj.his.module.finance.repository.InvoiceRepository;
import com.yhj.his.module.finance.repository.OutpatientBillingRepository;
import com.yhj.his.module.finance.repository.InpatientSettlementRepository;
import com.yhj.his.module.finance.service.InvoiceService;
import com.yhj.his.module.finance.vo.InvoiceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发票服务实现
 */
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OutpatientBillingRepository outpatientBillingRepository;
    private final InpatientSettlementRepository inpatientSettlementRepository;

    @Override
    @Transactional
    public InvoiceVO createInvoice(String billingId, String billingType, String operatorId, String operatorName) {
        String patientId = null;
        String patientName = null;
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal insuranceAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal selfPayAmount = java.math.BigDecimal.ZERO;

        // 根据收费类型获取信息
        if ("OUTPATIENT".equals(billingType)) {
            OutpatientBilling billing = outpatientBillingRepository.findById(billingId)
                    .orElseThrow(() -> new BusinessException("门诊收费记录不存在: " + billingId));
            patientId = billing.getPatientId();
            patientName = billing.getPatientName();
            totalAmount = billing.getTotalAmount();
            insuranceAmount = billing.getInsuranceAmount();
            selfPayAmount = billing.getSelfPayAmount();
        } else if ("INPATIENT".equals(billingType)) {
            InpatientSettlement settlement = inpatientSettlementRepository.findById(billingId)
                    .orElseThrow(() -> new BusinessException("住院结算记录不存在: " + billingId));
            patientId = settlement.getPatientId();
            patientName = settlement.getPatientName();
            totalAmount = settlement.getTotalAmount();
            insuranceAmount = settlement.getInsuranceAmount();
            selfPayAmount = settlement.getSelfPayAmount();
        } else if ("PREPAYMENT".equals(billingType)) {
            // 预交金收据
            // TODO: 从预交金记录获取信息
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(getNextInvoiceNo());
        invoice.setInvoiceCode("MED001");
        invoice.setBillingId(billingId);
        invoice.setBillingType(Invoice.BillingType.valueOf(billingType));
        invoice.setPatientId(patientId);
        invoice.setPatientName(patientName);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setInvoiceTime(LocalDateTime.now());
        invoice.setTotalAmount(totalAmount);
        invoice.setInsuranceAmount(insuranceAmount != null ? insuranceAmount : java.math.BigDecimal.ZERO);
        invoice.setSelfPayAmount(selfPayAmount != null ? selfPayAmount : totalAmount);
        invoice.setInvoiceType(Invoice.InvoiceType.MEDICAL);
        invoice.setPrintCount(0);
        invoice.setOperatorId(operatorId);
        invoice.setOperatorName(operatorName);
        invoice.setStatus(Invoice.InvoiceStatus.VALID);

        invoiceRepository.save(invoice);
        return toVO(invoice);
    }

    @Override
    @Transactional
    public InvoiceVO voidInvoice(InvoiceVoidDTO dto, String operatorId) {
        Invoice invoice = invoiceRepository.findByInvoiceNo(dto.getInvoiceNo())
                .orElseThrow(() -> new BusinessException("发票不存在: " + dto.getInvoiceNo()));

        if (invoice.getStatus() == Invoice.InvoiceStatus.VOID) {
            throw new BusinessException("发票已作废");
        }

        invoice.setStatus(Invoice.InvoiceStatus.VOID);
        invoice.setVoidTime(LocalDateTime.now());
        invoice.setVoidOperatorId(operatorId);
        invoice.setVoidReason(dto.getVoidReason());

        invoiceRepository.save(invoice);
        return toVO(invoice);
    }

    @Override
    @Transactional
    public InvoiceVO printInvoice(String invoiceNo) {
        Invoice invoice = invoiceRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new BusinessException("发票不存在: " + invoiceNo));

        if (invoice.getStatus() == Invoice.InvoiceStatus.VOID) {
            throw new BusinessException("发票已作废，无法打印");
        }

        invoice.setPrintCount(invoice.getPrintCount() + 1);
        invoice.setLastPrintTime(LocalDateTime.now());

        invoiceRepository.save(invoice);
        return toVO(invoice);
    }

    @Override
    @Transactional
    public InvoiceVO reprintInvoice(String invoiceNo, String operatorId) {
        Invoice invoice = invoiceRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new BusinessException("发票不存在: " + invoiceNo));

        if (invoice.getStatus() == Invoice.InvoiceStatus.VOID) {
            throw new BusinessException("发票已作废，无法重打");
        }

        // 记录重打操作
        invoice.setPrintCount(invoice.getPrintCount() + 1);
        invoice.setLastPrintTime(LocalDateTime.now());
        invoice.setRemark("重打操作，操作员: " + operatorId);

        invoiceRepository.save(invoice);
        return toVO(invoice);
    }

    @Override
    public InvoiceVO getByInvoiceNo(String invoiceNo) {
        Invoice invoice = invoiceRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new BusinessException("发票不存在: " + invoiceNo));
        return toVO(invoice);
    }

    @Override
    public InvoiceVO getByBillingId(String billingId) {
        Invoice invoice = invoiceRepository.findByBillingId(billingId)
                .orElseThrow(() -> new BusinessException("收费记录对应的发票不存在: " + billingId));
        return toVO(invoice);
    }

    @Override
    public List<InvoiceVO> listByPatientId(String patientId) {
        List<Invoice> invoices = invoiceRepository.findByPatientId(patientId);
        return invoices.stream().filter(i -> !Boolean.TRUE.equals(i.getDeleted())).map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<InvoiceVO> pageList(String patientId, String invoiceDate, String status, int pageNum, int pageSize) {
        Specification<Invoice> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (patientId != null && !patientId.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("patientId"), patientId));
            }
            if (invoiceDate != null && !invoiceDate.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("invoiceDate"), LocalDate.parse(invoiceDate)));
            }
            if (status != null && !status.isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), Invoice.InvoiceStatus.valueOf(status)));
            }
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            return predicates;
        };

        Page<Invoice> page = invoiceRepository.findAll(spec, PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending()));
        List<InvoiceVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public String getNextInvoiceNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxNo = invoiceRepository.findMaxInvoiceNoByDate(LocalDate.now()).orElse(null);

        if (maxNo == null) {
            return "INV" + dateStr + "0001";
        }

        // 解析当前最大号并递增
        String suffix = maxNo.substring(maxNo.length() - 4);
        int nextNum = Integer.parseInt(suffix) + 1;
        return "INV" + dateStr + String.format("%04d", nextNum);
    }

    @Override
    @Transactional
    public InvoiceVO generateElectronicInvoice(String billingId, String billingType) {
        InvoiceVO invoiceVO = createInvoice(billingId, billingType, "SYSTEM", "系统自动生成");

        Invoice invoice = invoiceRepository.findByInvoiceNo(invoiceVO.getInvoiceNo())
                .orElseThrow(() -> new BusinessException("发票不存在"));

        invoice.setInvoiceType(Invoice.InvoiceType.ELECTRONIC);
        invoiceRepository.save(invoice);

        return toVO(invoice);
    }

    /**
     * 实体转VO
     */
    private InvoiceVO toVO(Invoice invoice) {
        InvoiceVO vo = new InvoiceVO();
        vo.setId(invoice.getId());
        vo.setInvoiceNo(invoice.getInvoiceNo());
        vo.setInvoiceCode(invoice.getInvoiceCode());
        vo.setBillingId(invoice.getBillingId());
        vo.setBillingType(invoice.getBillingType() != null ? invoice.getBillingType().name() : null);
        vo.setBillingTypeDesc(invoice.getBillingType() != null ? invoice.getBillingType().getDescription() : null);
        vo.setPatientId(invoice.getPatientId());
        vo.setPatientName(invoice.getPatientName());
        vo.setInvoiceDate(invoice.getInvoiceDate());
        vo.setInvoiceTime(invoice.getInvoiceTime());
        vo.setTotalAmount(invoice.getTotalAmount());
        vo.setInsuranceAmount(invoice.getInsuranceAmount());
        vo.setSelfPayAmount(invoice.getSelfPayAmount());
        vo.setInvoiceType(invoice.getInvoiceType() != null ? invoice.getInvoiceType().name() : null);
        vo.setInvoiceTypeDesc(invoice.getInvoiceType() != null ? invoice.getInvoiceType().getDescription() : null);
        vo.setPrintCount(invoice.getPrintCount());
        vo.setLastPrintTime(invoice.getLastPrintTime());
        vo.setOperatorId(invoice.getOperatorId());
        vo.setOperatorName(invoice.getOperatorName());
        vo.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
        vo.setStatusDesc(invoice.getStatus() != null ? invoice.getStatus().getDescription() : null);
        vo.setVoidTime(invoice.getVoidTime());
        vo.setVoidReason(invoice.getVoidReason());
        return vo;
    }
}