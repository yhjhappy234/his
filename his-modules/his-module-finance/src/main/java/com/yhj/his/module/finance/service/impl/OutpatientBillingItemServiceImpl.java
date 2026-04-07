package com.yhj.his.module.finance.service.impl;

import com.yhj.his.module.finance.entity.OutpatientBillingItem;
import com.yhj.his.module.finance.entity.OutpatientBillingItem.BillingItemStatus;
import com.yhj.his.module.finance.entity.OutpatientBilling.RefundStatus;
import com.yhj.his.module.finance.repository.OutpatientBillingItemRepository;
import com.yhj.his.module.finance.service.OutpatientBillingItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 门诊收费明细服务实现
 */
@Service
@Transactional
public class OutpatientBillingItemServiceImpl implements OutpatientBillingItemService {

    @Autowired
    private OutpatientBillingItemRepository outpatientBillingItemRepository;

    @Override
    public OutpatientBillingItem save(OutpatientBillingItem outpatientBillingItem) {
        return outpatientBillingItemRepository.save(outpatientBillingItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OutpatientBillingItem> findById(String id) {
        return outpatientBillingItemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findByBillingId(String billingId) {
        return outpatientBillingItemRepository.findByBillingId(billingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findByBillingIdAndStatus(String billingId, BillingItemStatus status) {
        return outpatientBillingItemRepository.findByBillingIdAndStatus(billingId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findByItemId(String itemId) {
        return outpatientBillingItemRepository.findByItemId(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findByPrescriptionId(String prescriptionId) {
        return outpatientBillingItemRepository.findByPrescriptionId(prescriptionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findByRequestId(String requestId) {
        return outpatientBillingItemRepository.findByRequestId(requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByBillingId(String billingId) {
        return outpatientBillingItemRepository.countByBillingId(billingId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByBillingId(String billingId) {
        BigDecimal amount = outpatientBillingItemRepository.sumAmountByBillingId(billingId);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumRefundAmountByBillingId(String billingId) {
        BigDecimal amount = outpatientBillingItemRepository.sumRefundAmountByBillingId(billingId);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutpatientBillingItem> findAll() {
        return outpatientBillingItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OutpatientBillingItem> findAll(Pageable pageable) {
        return outpatientBillingItemRepository.findAll(pageable);
    }

    @Override
    public void deleteById(String id) {
        OutpatientBillingItem item = outpatientBillingItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("收费明细不存在: " + id));
        item.setDeleted(true);
        outpatientBillingItemRepository.save(item);
    }

    @Override
    public void deleteByBillingId(String billingId) {
        outpatientBillingItemRepository.deleteByBillingId(billingId);
    }

    @Override
    public List<OutpatientBillingItem> saveAll(List<OutpatientBillingItem> items) {
        return outpatientBillingItemRepository.saveAll(items);
    }

    @Override
    public OutpatientBillingItem refundItem(String id, BigDecimal refundAmount) {
        OutpatientBillingItem item = outpatientBillingItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("收费明细不存在: " + id));

        if (item.getStatus() == BillingItemStatus.REFUNDED) {
            throw new RuntimeException("该明细已退费");
        }

        // 检查退费金额是否有效
        BigDecimal availableAmount = item.getAmount().subtract(item.getRefundAmount() != null ? item.getRefundAmount() : BigDecimal.ZERO);
        if (refundAmount.compareTo(availableAmount) > 0) {
            throw new RuntimeException("退费金额超过可退金额");
        }

        // 更新退费金额
        BigDecimal currentRefundAmount = item.getRefundAmount() != null ? item.getRefundAmount() : BigDecimal.ZERO;
        item.setRefundAmount(currentRefundAmount.add(refundAmount));
        item.setRefundStatus(RefundStatus.COMPLETED);

        // 如果全部退费，则更新状态为已退费
        if (item.getRefundAmount().compareTo(item.getAmount()) >= 0) {
            item.setStatus(BillingItemStatus.REFUNDED);
        }

        return outpatientBillingItemRepository.save(item);
    }
}