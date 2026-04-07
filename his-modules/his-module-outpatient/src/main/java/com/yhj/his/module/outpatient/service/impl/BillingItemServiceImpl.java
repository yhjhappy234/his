package com.yhj.his.module.outpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.outpatient.entity.BillingItem;
import com.yhj.his.module.outpatient.repository.BillingItemRepository;
import com.yhj.his.module.outpatient.service.BillingItemService;
import com.yhj.his.module.outpatient.vo.PendingBillingVO;

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
 * 收费项目服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillingItemServiceImpl implements BillingItemService {

    private final BillingItemRepository billingItemRepository;

    @Override
    @Transactional
    public BillingItem createBillingItem(BillingItem item) {
        item.setCreateTime(LocalDateTime.now());
        item.setPayStatus("未收费");
        BillingItem saved = billingItemRepository.save(item);
        log.info("创建收费项目成功: itemId={}", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public List<BillingItem> createBillingItems(List<BillingItem> items) {
        LocalDateTime now = LocalDateTime.now();
        for (BillingItem item : items) {
            item.setCreateTime(now);
            item.setPayStatus("未收费");
        }
        List<BillingItem> saved = billingItemRepository.saveAll(items);
        log.info("批量创建收费项目成功: count={}", saved.size());
        return saved;
    }

    @Override
    public Optional<BillingItem> findById(String id) {
        return billingItemRepository.findById(id);
    }

    @Override
    public BillingItem getBillingItemDetail(String id) {
        return findById(id).orElseThrow(() -> new BusinessException("收费项目不存在"));
    }

    @Override
    public PageResult<BillingItem> listBillingItems(String patientId, String registrationId, String payStatus, Pageable pageable) {
        Specification<BillingItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (patientId != null && !patientId.isEmpty()) {
                predicates.add(cb.equal(root.get("patientId"), patientId));
            }
            if (registrationId != null && !registrationId.isEmpty()) {
                predicates.add(cb.equal(root.get("registrationId"), registrationId));
            }
            if (payStatus != null && !payStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("payStatus"), payStatus));
            }
            predicates.add(cb.equal(root.get("deleted"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<BillingItem> page = billingItemRepository.findAll(spec, pageable);
        return PageResult.of(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public List<BillingItem> listBillingItemsByRegistration(String registrationId) {
        return billingItemRepository.findByRegistrationId(registrationId);
    }

    @Override
    public List<BillingItem> listUnpaidItems(String registrationId) {
        return billingItemRepository.findByRegistrationIdAndPayStatus(registrationId, "未收费");
    }

    @Override
    public PendingBillingVO getPendingBilling(String registrationId) {
        List<BillingItem> items = listUnpaidItems(registrationId);

        if (items.isEmpty()) {
            throw new BusinessException("没有待收费项目");
        }

        PendingBillingVO vo = new PendingBillingVO();

        // 患者信息
        BillingItem firstItem = items.get(0);
        PendingBillingVO.PatientBriefVO patientInfo = new PendingBillingVO.PatientBriefVO();
        patientInfo.setPatientId(firstItem.getPatientId());
        patientInfo.setPatientName(firstItem.getPatientName());
        vo.setPatientInfo(patientInfo);

        // 项目列表
        List<PendingBillingVO.BillingItemVO> itemVOs = items.stream()
                .map(this::convertToItemVO)
                .collect(Collectors.toList());
        vo.setItems(itemVOs);

        // 总金额
        BigDecimal totalAmount = items.stream()
                .map(BillingItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        return vo;
    }

    @Override
    public BigDecimal calculateTotalAmount(String registrationId) {
        List<BillingItem> items = listUnpaidItems(registrationId);
        return items.stream()
                .map(BillingItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public BillingItem updatePayStatus(String id, String payStatus) {
        BillingItem item = getBillingItemDetail(id);
        item.setPayStatus(payStatus);
        if (payStatus.equals("已收费")) {
            item.setPayTime(LocalDateTime.now());
        }
        BillingItem saved = billingItemRepository.save(item);
        log.info("更新收费状态成功: itemId={}, payStatus={}", id, payStatus);
        return saved;
    }

    @Override
    @Transactional
    public void batchUpdatePayStatus(List<String> ids, String payStatus) {
        billingItemRepository.updatePayStatusByIds(ids, payStatus);
        log.info("批量更新收费状态成功: count={}, payStatus={}", ids.size(), payStatus);
    }

    @Override
    @Transactional
    public void deleteBillingItem(String id) {
        BillingItem item = getBillingItemDetail(id);
        if (item.getPayStatus().equals("已收费")) {
            throw new BusinessException("已收费项目无法删除");
        }
        item.setDeleted(true);
        billingItemRepository.save(item);
        log.info("删除收费项目成功: itemId={}", id);
    }

    @Override
    @Transactional
    public BillingItem updateBillingItem(String id, BillingItem item) {
        BillingItem existing = getBillingItemDetail(id);

        if (existing.getPayStatus().equals("已收费")) {
            throw new BusinessException("已收费项目无法修改");
        }

        existing.setItemNo(item.getItemNo());
        existing.setItemType(item.getItemType());
        existing.setRefId(item.getRefId());
        existing.setItemName(item.getItemName());
        existing.setDescription(item.getDescription());
        existing.setAmount(item.getAmount());
        existing.setInsuranceAmount(item.getInsuranceAmount());
        existing.setSelfPayAmount(item.getSelfPayAmount());
        existing.setRemark(item.getRemark());

        BillingItem saved = billingItemRepository.save(existing);
        log.info("更新收费项目成功: itemId={}", id);
        return saved;
    }

    private PendingBillingVO.BillingItemVO convertToItemVO(BillingItem item) {
        PendingBillingVO.BillingItemVO vo = new PendingBillingVO.BillingItemVO();
        vo.setItemId(item.getId());
        vo.setItemType(item.getItemType());
        vo.setItemNo(item.getItemNo());
        vo.setItemName(item.getItemName());
        vo.setDescription(item.getDescription());
        vo.setAmount(item.getAmount());
        vo.setPayStatus(item.getPayStatus());
        return vo;
    }
}