package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialInventoryVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存查询服务接口
 */
public interface InventoryQueryService {

    /**
     * 根据物资ID查询库存
     */
    List<MaterialInventoryVO> getByMaterialId(String materialId);

    /**
     * 根据物资ID和库房ID查询库存
     */
    List<MaterialInventoryVO> getByMaterialIdAndWarehouseId(String materialId, String warehouseId);

    /**
     * 根据库房ID查询库存
     */
    PageResult<MaterialInventoryVO> getByWarehouseId(String warehouseId, Integer pageNum, Integer pageSize);

    /**
     * 查询某物资总库存
     */
    BigDecimal getTotalQuantity(String materialId);

    /**
     * 查询某物资在某库房的总库存
     */
    BigDecimal getTotalQuantityByWarehouse(String materialId, String warehouseId);

    /**
     * 条件查询库存
     */
    PageResult<MaterialInventoryVO> query(QueryDTO query);

    /**
     * 查询效期库存
     */
    List<MaterialInventoryVO> getExpiringInventory(int days);
}