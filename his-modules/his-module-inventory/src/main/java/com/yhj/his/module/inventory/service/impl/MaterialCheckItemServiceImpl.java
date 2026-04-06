package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import com.yhj.his.module.inventory.repository.MaterialCheckItemRepository;
import com.yhj.his.module.inventory.service.MaterialCheckItemService;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 库存盘点明细Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialCheckItemServiceImpl implements MaterialCheckItemService {

    private final MaterialCheckItemRepository materialCheckItemRepository;
    private final MaterialInventoryService materialInventoryService;

    @Override
    public Optional<MaterialCheckItem> findById(String id) {
        return materialCheckItemRepository.findById(id);
    }

    @Override
    public List<MaterialCheckItem> findAll() {
        return materialCheckItemRepository.findAll();
    }

    @Override
    public List<MaterialCheckItem> findByCheckId(String checkId) {
        return materialCheckItemRepository.findByCheckId(checkId);
    }

    @Override
    public List<MaterialCheckItem> findByMaterialId(String materialId) {
        return materialCheckItemRepository.findByMaterialId(materialId);
    }

    @Override
    public List<MaterialCheckItem> findDiffItems(String checkId) {
        return materialCheckItemRepository.findDiffItems(checkId);
    }

    @Override
    public List<MaterialCheckItem> findUnadjustedDiffItems(String checkId) {
        return materialCheckItemRepository.findUnadjustedDiffItems(checkId);
    }

    @Override
    @Transactional
    public MaterialCheckItem create(MaterialCheckItem item) {
        calculateDiff(item);
        return materialCheckItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialCheckItem update(String id, MaterialCheckItem item) {
        MaterialCheckItem existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点明细不存在: " + id));

        existing.setMaterialId(item.getMaterialId());
        existing.setMaterialCode(item.getMaterialCode());
        existing.setMaterialName(item.getMaterialName());
        existing.setMaterialSpec(item.getMaterialSpec());
        existing.setMaterialUnit(item.getMaterialUnit());
        existing.setBatchNo(item.getBatchNo());
        existing.setInventoryId(item.getInventoryId());
        existing.setBookQuantity(item.getBookQuantity());
        existing.setActualQuantity(item.getActualQuantity());
        existing.setPurchasePrice(item.getPurchasePrice());
        existing.setRetailPrice(item.getRetailPrice());
        existing.setRemark(item.getRemark());
        calculateDiff(existing);

        return materialCheckItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        materialCheckItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteByCheckId(String checkId) {
        List<MaterialCheckItem> items = findByCheckId(checkId);
        items.forEach(item -> materialCheckItemRepository.delete(item));
    }

    @Override
    public BigDecimal calculateDiffAmount(MaterialCheckItem item) {
        if (item.getDiffQuantity() != null && item.getRetailPrice() != null) {
            return item.getDiffQuantity().abs().multiply(item.getRetailPrice());
        }
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public MaterialCheckItem inputActualQuantity(String id, BigDecimal actualQuantity, String remark) {
        MaterialCheckItem item = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点明细不存在: " + id));

        item.setActualQuantity(actualQuantity);
        item.setRemark(remark);
        calculateDiff(item);

        return materialCheckItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialCheckItem adjust(String id) {
        MaterialCheckItem item = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点明细不存在: " + id));

        item.setAdjusted(true);
        return materialCheckItemRepository.save(item);
    }

    @Override
    public void calculateDiff(MaterialCheckItem item) {
        if (item.getActualQuantity() == null) {
            item.setDiffQuantity(null);
            item.setDiffAmount(null);
            item.setDiffType(null);
            return;
        }

        BigDecimal diff = item.getActualQuantity().subtract(item.getBookQuantity());
        item.setDiffQuantity(diff);

        BigDecimal diffAmount = calculateDiffAmount(item);
        item.setDiffAmount(diffAmount);

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            item.setDiffType("PROFIT");
        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
            item.setDiffType("LOSS");
        } else {
            item.setDiffType("NONE");
        }
    }
}