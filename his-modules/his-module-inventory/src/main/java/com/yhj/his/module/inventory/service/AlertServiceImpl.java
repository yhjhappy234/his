package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.Material;
import com.yhj.his.module.inventory.entity.MaterialAlert;
import com.yhj.his.module.inventory.enums.MaterialStatus;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.repository.MaterialAlertRepository;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.repository.MaterialRepository;
import com.yhj.his.module.inventory.vo.MaterialAlertVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存预警服务实现
 */
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final MaterialAlertRepository alertRepository;
    private final MaterialRepository materialRepository;
    private final MaterialInventoryRepository inventoryRepository;

    @Override
    public List<MaterialAlertVO> getLowStockAlerts() {
        List<MaterialAlert> alerts = alertRepository.findByAlertTypeAndDeletedFalse("LOW_STOCK");
        return alerts.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<MaterialAlertVO> getExpiryAlerts(int days) {
        LocalDate expiryDate = LocalDate.now().plusDays(days);
        List<MaterialInventory> inventories = inventoryRepository.findByExpiryDateBefore(expiryDate);
        List<MaterialAlertVO> alerts = new ArrayList<>();

        for (MaterialInventory inventory : inventories) {
            MaterialAlertVO vo = new MaterialAlertVO();
            vo.setAlertType("EXPIRY");
            vo.setAlertTypeName("效期预警");
            vo.setMaterialId(inventory.getMaterialId());
            vo.setMaterialCode(inventory.getMaterialCode());
            vo.setMaterialName(inventory.getMaterialName());
            vo.setMaterialSpec(inventory.getMaterialSpec());
            vo.setMaterialUnit(inventory.getMaterialUnit());
            vo.setWarehouseId(inventory.getWarehouseId());
            vo.setWarehouseName(inventory.getWarehouseName());
            vo.setBatchNo(inventory.getBatchNo());
            vo.setCurrentQuantity(inventory.getQuantity());
            vo.setExpiryDate(inventory.getExpiryDate());

            long daysToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), inventory.getExpiryDate());
            vo.setAlertContent("物资即将过期，剩余" + daysToExpiry + "天，请及时处理");

            alerts.add(vo);
        }

        return alerts;
    }

    @Override
    public PageResult<MaterialAlertVO> list(Integer pageNum, Integer pageSize) {
        Page<MaterialAlert> page = alertRepository.findByDeletedFalse(PageRequest.of(pageNum - 1, pageSize));
        List<MaterialAlertVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public PageResult<MaterialAlertVO> query(QueryDTO query) {
        Page<MaterialAlert> page = alertRepository.search(
                query.getAlertType(),
                query.getStatus() != null ? Integer.parseInt(query.getStatus()) : null,
                query.getWarehouseId(),
                query.getMaterialId(),
                PageRequest.of(query.getPageNum() - 1, query.getPageSize())
        );
        List<MaterialAlertVO> list = page.getContent().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), query.getPageNum(), query.getPageSize());
    }

    @Override
    @Transactional
    public MaterialAlertVO handle(String alertId, String handlerId, String handlerName, String remark) {
        MaterialAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("预警记录不存在"));

        alert.setStatus(1);
        alert.setHandlerId(handlerId);
        alert.setHandlerName(handlerName);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleRemark(remark);

        alert = alertRepository.save(alert);
        return toVO(alert);
    }

    @Override
    @Transactional
    public void batchHandle(List<String> alertIds, String handlerId, String handlerName, String remark) {
        for (String alertId : alertIds) {
            handle(alertId, handlerId, handlerName, remark);
        }
    }

    @Override
    @Transactional
    public void checkAndGenerateAlerts() {
        // 检查库存下限预警
        List<Material> materials = materialRepository.findMaterialsForLowStockAlert(MaterialStatus.NORMAL);

        for (Material material : materials) {
            BigDecimal totalStock = inventoryRepository.sumQuantityByMaterialId(material.getId());
            if (totalStock != null && material.getMinStock() != null &&
                    totalStock.compareTo(material.getMinStock()) < 0) {

                // 检查是否已存在未处理的预警
                List<MaterialAlert> existing = alertRepository.findUnhandledAlertsByMaterialId(material.getId());
                if (existing.isEmpty()) {
                    MaterialAlert alert = new MaterialAlert();
                    alert.setAlertType("LOW_STOCK");
                    alert.setMaterialId(material.getId());
                    alert.setMaterialCode(material.getMaterialCode());
                    alert.setMaterialName(material.getMaterialName());
                    alert.setMaterialSpec(material.getMaterialSpec());
                    alert.setMaterialUnit(material.getMaterialUnit());
                    alert.setCurrentQuantity(totalStock);
                    alert.setAlertThreshold(material.getMinStock());
                    alert.setAlertContent("物资库存低于下限，当前库存: " + totalStock + ", 下限: " + material.getMinStock());
                    alert.setStatus(0);
                    alertRepository.save(alert);
                }
            }
        }

        // 检查效期预警（30天内）
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        List<MaterialInventory> inventories = inventoryRepository.findByExpiryDateBefore(expiryDate);

        for (MaterialInventory inventory : inventories) {
            if (inventory.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // 检查是否已存在未处理的预警
            List<MaterialAlert> existing = alertRepository.findUnhandledAlertsByMaterialId(inventory.getMaterialId());
            boolean hasExpiryAlert = existing.stream()
                    .anyMatch(a -> "EXPIRY".equals(a.getAlertType()) &&
                            inventory.getBatchNo().equals(a.getBatchNo()));

            if (!hasExpiryAlert) {
                long daysToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), inventory.getExpiryDate());

                MaterialAlert alert = new MaterialAlert();
                alert.setAlertType("EXPIRY");
                alert.setMaterialId(inventory.getMaterialId());
                alert.setMaterialCode(inventory.getMaterialCode());
                alert.setMaterialName(inventory.getMaterialName());
                alert.setMaterialSpec(inventory.getMaterialSpec());
                alert.setMaterialUnit(inventory.getMaterialUnit());
                alert.setWarehouseId(inventory.getWarehouseId());
                alert.setWarehouseName(inventory.getWarehouseName());
                alert.setBatchNo(inventory.getBatchNo());
                alert.setCurrentQuantity(inventory.getQuantity());
                alert.setExpiryDate(inventory.getExpiryDate());
                alert.setAlertContent("物资即将过期，剩余" + daysToExpiry + "天，请及时处理");
                alert.setStatus(0);
                alertRepository.save(alert);
            }
        }
    }

    @Override
    public List<MaterialAlertVO> getByMaterialId(String materialId) {
        List<MaterialAlert> alerts = alertRepository.findByMaterialIdAndDeletedFalse(materialId);
        return alerts.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialAlertVO toVO(MaterialAlert entity) {
        MaterialAlertVO vo = new MaterialAlertVO();
        vo.setId(entity.getId());
        vo.setAlertType(entity.getAlertType());
        vo.setAlertTypeName(getAlertTypeName(entity.getAlertType()));
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setBatchNo(entity.getBatchNo());
        vo.setCurrentQuantity(entity.getCurrentQuantity());
        vo.setAlertThreshold(entity.getAlertThreshold());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setAlertContent(entity.getAlertContent());
        vo.setStatus(entity.getStatus());
        vo.setStatusName(entity.getStatus() == 0 ? "未处理" : "已处理");
        vo.setHandlerId(entity.getHandlerId());
        vo.setHandlerName(entity.getHandlerName());
        vo.setHandleTime(entity.getHandleTime());
        vo.setHandleRemark(entity.getHandleRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private String getAlertTypeName(String alertType) {
        switch (alertType) {
            case "LOW_STOCK":
                return "库存下限预警";
            case "HIGH_STOCK":
                return "库存上限预警";
            case "EXPIRY":
                return "效期预警";
            default:
                return alertType;
        }
    }
}