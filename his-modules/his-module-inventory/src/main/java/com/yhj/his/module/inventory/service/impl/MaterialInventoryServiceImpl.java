package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 物资库存Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialInventoryServiceImpl implements MaterialInventoryService {

    private final MaterialInventoryRepository materialInventoryRepository;

    @Override
    public Optional<MaterialInventory> findById(String id) {
        return materialInventoryRepository.findById(id);
    }

    @Override
    public List<MaterialInventory> findAll() {
        return materialInventoryRepository.findAllActiveInventory();
    }

    @Override
    public Page<MaterialInventory> findAll(Pageable pageable) {
        return materialInventoryRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialInventory> findByMaterialId(String materialId) {
        return materialInventoryRepository.findByMaterialIdAndDeletedFalse(materialId);
    }

    @Override
    public List<MaterialInventory> findByWarehouseId(String warehouseId) {
        return materialInventoryRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public List<MaterialInventory> findByMaterialIdAndWarehouseId(String materialId, String warehouseId) {
        return materialInventoryRepository.findByMaterialIdAndWarehouseIdAndDeletedFalse(materialId, warehouseId);
    }

    @Override
    public Optional<MaterialInventory> findByMaterialIdAndBatchNoAndWarehouseId(String materialId, String batchNo, String warehouseId) {
        return materialInventoryRepository.findByMaterialIdAndBatchNoAndWarehouseIdAndDeletedFalse(materialId, batchNo, warehouseId);
    }

    @Override
    public BigDecimal sumQuantityByMaterialId(String materialId) {
        BigDecimal sum = materialInventoryRepository.sumQuantityByMaterialId(materialId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumQuantityByMaterialIdAndWarehouseId(String materialId, String warehouseId) {
        BigDecimal sum = materialInventoryRepository.sumQuantityByMaterialIdAndWarehouseId(materialId, warehouseId);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<MaterialInventory> findByExpiryDateBefore(LocalDate expiryDate) {
        return materialInventoryRepository.findByExpiryDateBefore(expiryDate);
    }

    @Override
    public List<MaterialInventory> findAvailableInventoryOrderByExpiry(String materialId, String warehouseId) {
        return materialInventoryRepository.findAvailableInventoryOrderByExpiry(materialId, warehouseId, LocalDate.now());
    }

    @Override
    public List<MaterialInventory> findAllActiveInventory() {
        return materialInventoryRepository.findAllActiveInventory();
    }

    @Override
    @Transactional
    public MaterialInventory create(MaterialInventory materialInventory) {
        materialInventory.setAvailableQuantity(materialInventory.getQuantity().subtract(materialInventory.getLockedQuantity()));
        materialInventory.setInboundTime(LocalDateTime.now());
        return materialInventoryRepository.save(materialInventory);
    }

    @Override
    @Transactional
    public MaterialInventory update(String id, MaterialInventory materialInventory) {
        MaterialInventory existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + id));

        existing.setLocation(materialInventory.getLocation());
        existing.setStatus(materialInventory.getStatus());

        return materialInventoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialInventory inventory = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + id));
        if (inventory.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("库存数量不为零，不能删除");
        }
        inventory.setDeleted(true);
        materialInventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public MaterialInventory inboundStock(String materialId, String warehouseId, String batchNo,
                                          BigDecimal quantity, BigDecimal purchasePrice, BigDecimal retailPrice,
                                          LocalDate expiryDate, String supplierId, String supplierName) {
        // 查找是否存在相同批号的库存
        Optional<MaterialInventory> existingOpt = findByMaterialIdAndBatchNoAndWarehouseId(materialId, batchNo, warehouseId);

        if (existingOpt.isPresent()) {
            // 增加库存
            MaterialInventory existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity().add(quantity));
            existing.setAvailableQuantity(existing.getAvailableQuantity().add(quantity));
            existing.setPurchasePrice(purchasePrice);
            existing.setRetailPrice(retailPrice);
            return materialInventoryRepository.save(existing);
        } else {
            // 创建新的库存记录
            MaterialInventory inventory = new MaterialInventory();
            inventory.setMaterialId(materialId);
            inventory.setWarehouseId(warehouseId);
            inventory.setBatchNo(batchNo);
            inventory.setQuantity(quantity);
            inventory.setLockedQuantity(BigDecimal.ZERO);
            inventory.setAvailableQuantity(quantity);
            inventory.setPurchasePrice(purchasePrice);
            inventory.setRetailPrice(retailPrice);
            inventory.setExpiryDate(expiryDate);
            inventory.setSupplierId(supplierId);
            inventory.setSupplierName(supplierName);
            inventory.setInboundTime(LocalDateTime.now());
            inventory.setStatus(1);
            return materialInventoryRepository.save(inventory);
        }
    }

    @Override
    @Transactional
    public void outboundStock(String inventoryId, BigDecimal quantity) {
        MaterialInventory inventory = findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + inventoryId));

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("可用库存不足");
        }

        inventory.setQuantity(inventory.getQuantity().subtract(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        materialInventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void lockStock(String inventoryId, BigDecimal quantity) {
        MaterialInventory inventory = findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + inventoryId));

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("可用库存不足");
        }

        inventory.setLockedQuantity(inventory.getLockedQuantity().add(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        materialInventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void unlockStock(String inventoryId, BigDecimal quantity) {
        MaterialInventory inventory = findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + inventoryId));

        if (inventory.getLockedQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("锁定库存数量不足");
        }

        inventory.setLockedQuantity(inventory.getLockedQuantity().subtract(quantity));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(quantity));
        materialInventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void adjustStock(String inventoryId, BigDecimal actualQuantity) {
        MaterialInventory inventory = findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("库存记录不存在: " + inventoryId));

        BigDecimal diff = actualQuantity.subtract(inventory.getQuantity());
        inventory.setQuantity(actualQuantity);
        inventory.setAvailableQuantity(actualQuantity.subtract(inventory.getLockedQuantity()));
        materialInventoryRepository.save(inventory);
    }

    @Override
    public boolean checkStockAvailable(String materialId, String warehouseId, BigDecimal requiredQuantity) {
        BigDecimal available = getAvailableQuantity(materialId, warehouseId);
        return available.compareTo(requiredQuantity) >= 0;
    }

    @Override
    public BigDecimal getAvailableQuantity(String materialId, String warehouseId) {
        List<MaterialInventory> inventories = findAvailableInventoryOrderByExpiry(materialId, warehouseId);
        return inventories.stream()
                .map(MaterialInventory::getAvailableQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}