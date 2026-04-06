package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import com.yhj.his.module.inventory.repository.MaterialInboundItemRepository;
import com.yhj.his.module.inventory.service.MaterialInboundItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 入库明细Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialInboundItemServiceImpl implements MaterialInboundItemService {

    private final MaterialInboundItemRepository materialInboundItemRepository;

    @Override
    public Optional<MaterialInboundItem> findById(String id) {
        return materialInboundItemRepository.findById(id);
    }

    @Override
    public List<MaterialInboundItem> findAll() {
        return materialInboundItemRepository.findAll();
    }

    @Override
    public List<MaterialInboundItem> findByInboundId(String inboundId) {
        return materialInboundItemRepository.findByInboundId(inboundId);
    }

    @Override
    public List<MaterialInboundItem> findByMaterialId(String materialId) {
        return materialInboundItemRepository.findByMaterialId(materialId);
    }

    @Override
    @Transactional
    public MaterialInboundItem create(MaterialInboundItem item) {
        item.setAmount(calculateAmount(item));
        return materialInboundItemRepository.save(item);
    }

    @Override
    @Transactional
    public MaterialInboundItem update(String id, MaterialInboundItem item) {
        MaterialInboundItem existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("入库明细不存在: " + id));

        existing.setMaterialId(item.getMaterialId());
        existing.setMaterialCode(item.getMaterialCode());
        existing.setMaterialName(item.getMaterialName());
        existing.setMaterialSpec(item.getMaterialSpec());
        existing.setMaterialUnit(item.getMaterialUnit());
        existing.setBatchNo(item.getBatchNo());
        existing.setProductionDate(item.getProductionDate());
        existing.setExpiryDate(item.getExpiryDate());
        existing.setQuantity(item.getQuantity());
        existing.setPurchasePrice(item.getPurchasePrice());
        existing.setRetailPrice(item.getRetailPrice());
        existing.setLocation(item.getLocation());
        existing.setRemark(item.getRemark());
        existing.setAmount(calculateAmount(existing));

        return materialInboundItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        materialInboundItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteByInboundId(String inboundId) {
        List<MaterialInboundItem> items = findByInboundId(inboundId);
        items.forEach(item -> materialInboundItemRepository.delete(item));
    }

    @Override
    public BigDecimal calculateAmount(MaterialInboundItem item) {
        if (item.getQuantity() != null && item.getPurchasePrice() != null) {
            return item.getQuantity().multiply(item.getPurchasePrice());
        }
        return BigDecimal.ZERO;
    }
}