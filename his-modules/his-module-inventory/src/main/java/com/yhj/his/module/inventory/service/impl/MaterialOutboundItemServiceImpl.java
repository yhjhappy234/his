package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import com.yhj.his.module.inventory.repository.MaterialOutboundItemRepository;
import com.yhj.his.module.inventory.service.MaterialOutboundItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 出库明细Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialOutboundItemServiceImpl implements MaterialOutboundItemService {

    private final MaterialOutboundItemRepository materialOutboundItemRepository;

    @Override
    public Optional<MaterialOutboundItem> findById(String id) {
        return materialOutboundItemRepository.findById(id);
    }

    @Override
    public List<MaterialOutboundItem> findAll() {
        return materialOutboundItemRepository.findAll();
    }

    @Override
    public List<MaterialOutboundItem> findByOutboundId(String outboundId) {
        return materialOutboundItemRepository.findByOutboundId(outboundId);
    }

    @Override
    public List<MaterialOutboundItem> findByMaterialId(String materialId) {
        return materialOutboundItemRepository.findByMaterialId(materialId);
    }

    @Override
    @Transactional
    public MaterialOutboundItem create(MaterialOutboundItem item) {
        item.setAmount(calculateAmount(item));
        return materialOutboundItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialOutboundItem update(String id, MaterialOutboundItem item) {
        MaterialOutboundItem existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("出库明细不存在: " + id));

        existing.setMaterialId(item.getMaterialId());
        existing.setMaterialCode(item.getMaterialCode());
        existing.setMaterialName(item.getMaterialName());
        existing.setMaterialSpec(item.getMaterialSpec());
        existing.setMaterialUnit(item.getMaterialUnit());
        existing.setBatchNo(item.getBatchNo());
        existing.setInventoryId(item.getInventoryId());
        existing.setApplyQuantity(item.getApplyQuantity());
        existing.setQuantity(item.getQuantity());
        existing.setPurchasePrice(item.getPurchasePrice());
        existing.setRetailPrice(item.getRetailPrice());
        existing.setRemark(item.getRemark());
        existing.setAmount(calculateAmount(existing));

        return materialOutboundItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        materialOutboundItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteByOutboundId(String outboundId) {
        List<MaterialOutboundItem> items = findByOutboundId(outboundId);
        items.forEach(item -> materialOutboundItemRepository.delete(item));
    }

    @Override
    public BigDecimal calculateAmount(MaterialOutboundItem item) {
        if (item.getQuantity() != null && item.getRetailPrice() != null) {
            return item.getQuantity().multiply(item.getRetailPrice());
        }
        return BigDecimal.ZERO;
    }
}