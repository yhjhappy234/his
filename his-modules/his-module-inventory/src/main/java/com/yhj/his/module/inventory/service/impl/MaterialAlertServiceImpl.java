package com.yhj.his.module.inventory.service.impl;

import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialAlert;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.enums.MaterialStatus;
import com.yhj.his.module.inventory.repository.MaterialAlertRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.service.MaterialAlertService;
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
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 库存预警Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialAlertServiceImpl implements MaterialAlertService {

    private final MaterialAlertRepository materialAlertRepository;
    private final MaterialRepository materialRepository;
    private final MaterialInventoryService materialInventoryService;

    private static final int EXPIRY_ALERT_DAYS = 30; // 效期预警提前天数

    @Override
    public Optional<MaterialAlert> findById(String id) {
        return materialAlertRepository.findById(id);
    }

    @Override
    public List<MaterialAlert> findAll() {
        return materialAlertRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<MaterialAlert> findAll(Pageable pageable) {
        return materialAlertRepository.findByDeletedFalse(pageable);
    }

    @Override
    public List<MaterialAlert> findByAlertType(String alertType) {
        return materialAlertRepository.findByAlertTypeAndDeletedFalse(alertType);
    }

    @Override
    public List<MaterialAlert> findByStatus(Integer status) {
        return materialAlertRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<MaterialAlert> findByMaterialId(String materialId) {
        return materialAlertRepository.findByMaterialIdAndDeletedFalse(materialId);
    }

    @Override
    public List<MaterialAlert> findByWarehouseId(String warehouseId) {
        return materialAlertRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
    }

    @Override
    public Page<MaterialAlert> search(String alertType, Integer status, String warehouseId, String materialId, Pageable pageable) {
        return materialAlertRepository.search(alertType, status, warehouseId, materialId, pageable);
    }

    @Override
    public List<MaterialAlert> findUnhandledAlerts() {
        return materialAlertRepository.findUnhandledAlerts();
    }

    @Override
    public List<MaterialAlert> findUnhandledAlertsByMaterialId(String materialId) {
        return materialAlertRepository.findUnhandledAlertsByMaterialId(materialId);
    }

    @Override
    @Transactional
    public MaterialAlert create(MaterialAlert materialAlert) {
        materialAlert.setStatus(0);
        materialAlert.setCreateTime(LocalDateTime.now());
        return materialAlertRepository.save(materialAlert);
    }

    @Override
    @Transactional
    public MaterialAlert update(String id, MaterialAlert materialAlert) {
        MaterialAlert existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预警记录不存在: " + id));

        existing.setAlertContent(materialAlert.getAlertContent());
        existing.setRemark(materialAlert.getRemark());

        return materialAlertRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        MaterialAlert alert = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预警记录不存在: " + id));
        alert.setDeleted(true);
        materialAlertRepository.save(alert);
    }

    @Override
    @Transactional
    public void deleteBatch(List<String> ids) {
        ids.forEach(this::delete);
    }

    @Override
    @Transactional
    public MaterialAlert handle(String id, String handlerId, String handlerName, String handleRemark) {
        MaterialAlert alert = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("预警记录不存在: " + id));

        alert.setStatus(1);
        alert.setHandlerId(handlerId);
        alert.setHandlerName(handlerName);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleRemark(handleRemark);

        return materialAlertRepository.save(alert);
    }

    @Override
    @Transactional
    public void handleBatch(List<String> ids, String handlerId, String handlerName, String handleRemark) {
        ids.forEach(id -> handle(id, handlerId, handlerName, handleRemark));
    }

    @Override
    @Transactional
    public MaterialAlert createLowStockAlert(String materialId, String materialCode, String materialName,
                                             String materialSpec, String materialUnit, String warehouseId,
                                             String warehouseName, BigDecimal currentQuantity, BigDecimal alertThreshold) {
        MaterialAlert alert = new MaterialAlert();
        alert.setAlertType("LOW_STOCK");
        alert.setMaterialId(materialId);
        alert.setMaterialCode(materialCode);
        alert.setMaterialName(materialName);
        alert.setMaterialSpec(materialSpec);
        alert.setMaterialUnit(materialUnit);
        alert.setWarehouseId(warehouseId);
        alert.setWarehouseName(warehouseName);
        alert.setCurrentQuantity(currentQuantity);
        alert.setAlertThreshold(alertThreshold);
        alert.setAlertContent("库存不足预警: 当前库存" + currentQuantity + ", 预警阈值" + alertThreshold);
        return create(alert);
    }

    @Override
    @Transactional
    public MaterialAlert createHighStockAlert(String materialId, String materialCode, String materialName,
                                              String materialSpec, String materialUnit, String warehouseId,
                                              String warehouseName, BigDecimal currentQuantity, BigDecimal alertThreshold) {
        MaterialAlert alert = new MaterialAlert();
        alert.setAlertType("HIGH_STOCK");
        alert.setMaterialId(materialId);
        alert.setMaterialCode(materialCode);
        alert.setMaterialName(materialName);
        alert.setMaterialSpec(materialSpec);
        alert.setMaterialUnit(materialUnit);
        alert.setWarehouseId(warehouseId);
        alert.setWarehouseName(warehouseName);
        alert.setCurrentQuantity(currentQuantity);
        alert.setAlertThreshold(alertThreshold);
        alert.setAlertContent("库存超限预警: 当前库存" + currentQuantity + ", 预警阈值" + alertThreshold);
        return create(alert);
    }

    @Override
    @Transactional
    public MaterialAlert createExpiryAlert(String materialId, String materialCode, String materialName,
                                           String materialSpec, String materialUnit, String warehouseId,
                                           String warehouseName, String batchNo, LocalDate expiryDate) {
        MaterialAlert alert = new MaterialAlert();
        alert.setAlertType("EXPIRY");
        alert.setMaterialId(materialId);
        alert.setMaterialCode(materialCode);
        alert.setMaterialName(materialName);
        alert.setMaterialSpec(materialSpec);
        alert.setMaterialUnit(materialUnit);
        alert.setWarehouseId(warehouseId);
        alert.setWarehouseName(warehouseName);
        alert.setBatchNo(batchNo);
        alert.setExpiryDate(expiryDate);
        long daysToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        alert.setAlertContent("效期预警: 批号" + batchNo + ", 有效期" + expiryDate + ", 剩余" + daysToExpiry + "天");
        return create(alert);
    }

    @Override
    @Transactional
    public void checkAndGenerateAlerts() {
        checkLowStockAlerts();
        checkExpiryAlerts();
    }

    @Override
    @Transactional
    public void checkLowStockAlerts() {
        List<Material> materials = materialRepository.findMaterialsForLowStockAlert(MaterialStatus.NORMAL);

        for (Material material : materials) {
            List<MaterialInventory> inventories = materialInventoryService.findByMaterialId(material.getId());

            for (MaterialInventory inventory : inventories) {
                BigDecimal totalQuantity = inventory.getQuantity();

                // 检查低库存
                if (material.getMinStock() != null && totalQuantity.compareTo(material.getMinStock()) < 0) {
                    // 检查是否已存在未处理的预警
                    List<MaterialAlert> existingAlerts = findUnhandledAlertsByMaterialId(material.getId());
                    boolean hasLowStockAlert = existingAlerts.stream()
                            .anyMatch(a -> "LOW_STOCK".equals(a.getAlertType()) &&
                                    a.getWarehouseId().equals(inventory.getWarehouseId()));

                    if (!hasLowStockAlert) {
                        createLowStockAlert(
                                material.getId(),
                                material.getMaterialCode(),
                                material.getMaterialName(),
                                material.getMaterialSpec(),
                                material.getMaterialUnit(),
                                inventory.getWarehouseId(),
                                inventory.getWarehouseName(),
                                totalQuantity,
                                material.getMinStock()
                        );
                    }
                }

                // 检查高库存
                if (material.getMaxStock() != null && totalQuantity.compareTo(material.getMaxStock()) > 0) {
                    List<MaterialAlert> existingAlerts = findUnhandledAlertsByMaterialId(material.getId());
                    boolean hasHighStockAlert = existingAlerts.stream()
                            .anyMatch(a -> "HIGH_STOCK".equals(a.getAlertType()) &&
                                    a.getWarehouseId().equals(inventory.getWarehouseId()));

                    if (!hasHighStockAlert) {
                        createHighStockAlert(
                                material.getId(),
                                material.getMaterialCode(),
                                material.getMaterialName(),
                                material.getMaterialSpec(),
                                material.getMaterialUnit(),
                                inventory.getWarehouseId(),
                                inventory.getWarehouseName(),
                                totalQuantity,
                                material.getMaxStock()
                        );
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void checkExpiryAlerts() {
        LocalDate expiryThreshold = LocalDate.now().plusDays(EXPIRY_ALERT_DAYS);
        List<MaterialInventory> inventories = materialInventoryService.findByExpiryDateBefore(expiryThreshold);

        for (MaterialInventory inventory : inventories) {
            // 检查是否已存在未处理的预警
            List<MaterialAlert> existingAlerts = findUnhandledAlertsByMaterialId(inventory.getMaterialId());
            boolean hasExpiryAlert = existingAlerts.stream()
                    .anyMatch(a -> "EXPIRY".equals(a.getAlertType()) &&
                            a.getWarehouseId().equals(inventory.getWarehouseId()) &&
                            a.getBatchNo().equals(inventory.getBatchNo()));

            if (!hasExpiryAlert) {
                createExpiryAlert(
                        inventory.getMaterialId(),
                        inventory.getMaterialCode(),
                        inventory.getMaterialName(),
                        inventory.getMaterialSpec(),
                        inventory.getMaterialUnit(),
                        inventory.getWarehouseId(),
                        inventory.getWarehouseName(),
                        inventory.getBatchNo(),
                        inventory.getExpiryDate()
                );
            }
        }
    }

    @Override
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        List<MaterialAlert> unhandledAlerts = findUnhandledAlerts();
        statistics.put("totalUnhandled", unhandledAlerts.size());

        long lowStockCount = unhandledAlerts.stream().filter(a -> "LOW_STOCK".equals(a.getAlertType())).count();
        long highStockCount = unhandledAlerts.stream().filter(a -> "HIGH_STOCK".equals(a.getAlertType())).count();
        long expiryCount = unhandledAlerts.stream().filter(a -> "EXPIRY".equals(a.getAlertType())).count();

        statistics.put("lowStockCount", lowStockCount);
        statistics.put("highStockCount", highStockCount);
        statistics.put("expiryCount", expiryCount);

        return statistics;
    }
}