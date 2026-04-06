package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.repository.MaterialInventoryRepository;
import com.yhj.his.module.inventory.vo.MaterialInventoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存查询服务实现
 */
@Service
@RequiredArgsConstructor
public class InventoryQueryServiceImpl implements InventoryQueryService {

    private final MaterialInventoryRepository inventoryRepository;

    @Override
    public List<MaterialInventoryVO> getByMaterialId(String materialId) {
        List<MaterialInventory> list = inventoryRepository.findByMaterialIdAndDeletedFalse(materialId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<MaterialInventoryVO> getByMaterialIdAndWarehouseId(String materialId, String warehouseId) {
        List<MaterialInventory> list = inventoryRepository.findByMaterialIdAndWarehouseIdAndDeletedFalse(materialId, warehouseId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<MaterialInventoryVO> getByWarehouseId(String warehouseId, Integer pageNum, Integer pageSize) {
        List<MaterialInventory> all = inventoryRepository.findByWarehouseIdAndDeletedFalse(warehouseId);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, all.size());
        List<MaterialInventory> pageList = all.subList(start, end);
        List<MaterialInventoryVO> list = pageList.stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(list, (long) all.size(), pageNum, pageSize);
    }

    @Override
    public BigDecimal getTotalQuantity(String materialId) {
        BigDecimal total = inventoryRepository.sumQuantityByMaterialId(materialId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalQuantityByWarehouse(String materialId, String warehouseId) {
        BigDecimal total = inventoryRepository.sumQuantityByMaterialIdAndWarehouseId(materialId, warehouseId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public PageResult<MaterialInventoryVO> query(QueryDTO query) {
        List<MaterialInventory> all = inventoryRepository.findAllActiveInventory();

        // 根据条件过滤
        if (query.getWarehouseId() != null) {
            all = all.stream()
                    .filter(i -> query.getWarehouseId().equals(i.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        if (query.getKeyword() != null) {
            String keyword = query.getKeyword().toLowerCase();
            all = all.stream()
                    .filter(i -> i.getMaterialCode().toLowerCase().contains(keyword) ||
                            i.getMaterialName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        // 分页
        int start = (query.getPageNum() - 1) * query.getPageSize();
        int end = Math.min(start + query.getPageSize(), all.size());
        List<MaterialInventory> pageList = all.subList(start, end);
        List<MaterialInventoryVO> list = pageList.stream().map(this::toVO).collect(Collectors.toList());

        return PageResult.of(list, (long) all.size(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public List<MaterialInventoryVO> getExpiringInventory(int days) {
        LocalDate expiryDate = LocalDate.now().plusDays(days);
        List<MaterialInventory> list = inventoryRepository.findByExpiryDateBefore(expiryDate);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    private MaterialInventoryVO toVO(MaterialInventory entity) {
        MaterialInventoryVO vo = new MaterialInventoryVO();
        vo.setId(entity.getId());
        vo.setMaterialId(entity.getMaterialId());
        vo.setMaterialCode(entity.getMaterialCode());
        vo.setMaterialName(entity.getMaterialName());
        vo.setMaterialSpec(entity.getMaterialSpec());
        vo.setMaterialUnit(entity.getMaterialUnit());
        vo.setWarehouseId(entity.getWarehouseId());
        vo.setWarehouseName(entity.getWarehouseName());
        vo.setBatchNo(entity.getBatchNo());
        vo.setExpiryDate(entity.getExpiryDate());
        vo.setQuantity(entity.getQuantity());
        vo.setLockedQuantity(entity.getLockedQuantity());
        vo.setAvailableQuantity(entity.getAvailableQuantity());
        vo.setLocation(entity.getLocation());
        vo.setPurchasePrice(entity.getPurchasePrice());
        vo.setRetailPrice(entity.getRetailPrice());
        vo.setSupplierId(entity.getSupplierId());
        vo.setSupplierName(entity.getSupplierName());
        vo.setInboundTime(entity.getInboundTime());
        vo.setStatus(entity.getStatus());

        // 计算距过期天数
        if (entity.getExpiryDate() != null) {
            long daysToExpiry = ChronoUnit.DAYS.between(LocalDate.now(), entity.getExpiryDate());
            if (daysToExpiry < 0) {
                vo.setStatus(0); // 已过期
            }
        }

        return vo;
    }
}