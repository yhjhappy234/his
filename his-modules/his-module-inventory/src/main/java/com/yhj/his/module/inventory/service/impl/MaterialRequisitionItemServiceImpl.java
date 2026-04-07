package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import com.yhj.his.module.inventory.repository.MaterialRequisitionItemRepository;
import com.yhj.his.module.inventory.service.MaterialRequisitionItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 物资申领明细Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialRequisitionItemServiceImpl implements MaterialRequisitionItemService {

    private final MaterialRequisitionItemRepository materialRequisitionItemRepository;

    @Override
    public Optional<MaterialRequisitionItem> findById(String id) {
        return materialRequisitionItemRepository.findById(id);
    }

    @Override
    public List<MaterialRequisitionItem> findAll() {
        return materialRequisitionItemRepository.findAll();
    }

    @Override
    public List<MaterialRequisitionItem> findByRequisitionId(String requisitionId) {
        return materialRequisitionItemRepository.findByRequisitionId(requisitionId);
    }

    @Override
    public List<MaterialRequisitionItem> findByMaterialId(String materialId) {
        return materialRequisitionItemRepository.findByMaterialId(materialId);
    }

    @Override
    @Transactional
    public MaterialRequisitionItem create(MaterialRequisitionItem item) {
        item.setAmount(calculateAmount(item));
        return materialRequisitionItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialRequisitionItem update(String id, MaterialRequisitionItem item) {
        MaterialRequisitionItem existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领明细不存在: " + id));

        existing.setMaterialId(item.getMaterialId());
        existing.setMaterialCode(item.getMaterialCode());
        existing.setMaterialName(item.getMaterialName());
        existing.setMaterialSpec(item.getMaterialSpec());
        existing.setMaterialUnit(item.getMaterialUnit());
        existing.setApplyQuantity(item.getApplyQuantity());
        existing.setIssueQuantity(item.getIssueQuantity());
        existing.setPurchasePrice(item.getPurchasePrice());
        existing.setRetailPrice(item.getRetailPrice());
        existing.setRemark(item.getRemark());
        existing.setAmount(calculateAmount(existing));

        return materialRequisitionItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        materialRequisitionItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteByRequisitionId(String requisitionId) {
        List<MaterialRequisitionItem> items = findByRequisitionId(requisitionId);
        items.forEach(item -> materialRequisitionItemRepository.delete(item));
    }

    @Override
    public BigDecimal calculateAmount(MaterialRequisitionItem item) {
        if (item.getApplyQuantity() != null && item.getRetailPrice() != null) {
            return item.getApplyQuantity().multiply(item.getRetailPrice());
        }
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public MaterialRequisitionItem updateIssueQuantity(String id, BigDecimal issueQuantity) {
        MaterialRequisitionItem item = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("申领明细不存在: " + id));

        item.setIssueQuantity(issueQuantity);
        if (issueQuantity != null && item.getRetailPrice() != null) {
            item.setAmount(issueQuantity.multiply(item.getRetailPrice()));
        }

        return materialRequisitionItemRepository.save(item);
    }
}