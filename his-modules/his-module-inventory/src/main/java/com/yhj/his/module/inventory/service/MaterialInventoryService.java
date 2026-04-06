package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 物资库存Service接口
 */
public interface MaterialInventoryService {

    /**
     * 根据ID查询库存
     */
    Optional<MaterialInventory> findById(String id);

    /**
     * 查询所有库存
     */
    List<MaterialInventory> findAll();

    /**
     * 分页查询库存
     */
    Page<MaterialInventory> findAll(Pageable pageable);

    /**
     * 根据物资ID查询库存
     */
    List<MaterialInventory> findByMaterialId(String materialId);

    /**
     * 根据库房ID查询库存
     */
    List<MaterialInventory> findByWarehouseId(String warehouseId);

    /**
     * 根据物资ID和库房ID查询库存
     */
    List<MaterialInventory> findByMaterialIdAndWarehouseId(String materialId, String warehouseId);

    /**
     * 根据物资ID、批号、库房ID查询库存
     */
    Optional<MaterialInventory> findByMaterialIdAndBatchNoAndWarehouseId(String materialId, String batchNo, String warehouseId);

    /**
     * 查询某物资总库存数量
     */
    BigDecimal sumQuantityByMaterialId(String materialId);

    /**
     * 查询某物资在某库房的总库存数量
     */
    BigDecimal sumQuantityByMaterialIdAndWarehouseId(String materialId, String warehouseId);

    /**
     * 查询效期预警库存
     */
    List<MaterialInventory> findByExpiryDateBefore(LocalDate expiryDate);

    /**
     * 查询可用库存（按先进先出）
     */
    List<MaterialInventory> findAvailableInventoryOrderByExpiry(String materialId, String warehouseId);

    /**
     * 查询所有有效库存
     */
    List<MaterialInventory> findAllActiveInventory();

    /**
     * 创建库存记录
     */
    MaterialInventory create(MaterialInventory materialInventory);

    /**
     * 更新库存记录
     */
    MaterialInventory update(String id, MaterialInventory materialInventory);

    /**
     * 删除库存记录
     */
    void delete(String id);

    /**
     * 入库增加库存
     */
    MaterialInventory inboundStock(String materialId, String warehouseId, String batchNo,
                                   BigDecimal quantity, BigDecimal purchasePrice, BigDecimal retailPrice,
                                   LocalDate expiryDate, String supplierId, String supplierName);

    /**
     * 出库减少库存
     */
    void outboundStock(String inventoryId, BigDecimal quantity);

    /**
     * 锁定库存
     */
    void lockStock(String inventoryId, BigDecimal quantity);

    /**
     * 解锁库存
     */
    void unlockStock(String inventoryId, BigDecimal quantity);

    /**
     * 盘点调整库存
     */
    void adjustStock(String inventoryId, BigDecimal actualQuantity);

    /**
     * 检查库存是否充足
     */
    boolean checkStockAvailable(String materialId, String warehouseId, BigDecimal requiredQuantity);

    /**
     * 获取可用库存数量
     */
    BigDecimal getAvailableQuantity(String materialId, String warehouseId);
}