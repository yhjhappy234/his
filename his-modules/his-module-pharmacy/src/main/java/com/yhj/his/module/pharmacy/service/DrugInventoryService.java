package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.InventoryInDTO;
import com.yhj.his.module.pharmacy.dto.InventoryQueryDTO;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import com.yhj.his.module.pharmacy.vo.ExpiryAlertVO;
import com.yhj.his.module.pharmacy.vo.InventoryVO;
import com.yhj.his.module.pharmacy.vo.StockAlertVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 药品库存服务接口
 */
public interface DrugInventoryService {

    /**
     * 库存入库
     */
    Result<InventoryVO> inbound(InventoryInDTO dto);

    /**
     * 库存出库
     */
    Result<InventoryVO> outbound(String inventoryId, BigDecimal quantity, String reason, String operatorId);

    /**
     * 根据ID查询库存
     */
    Result<InventoryVO> getInventoryById(String inventoryId);

    /**
     * 分页查询库存列表
     */
    Result<PageResult<InventoryVO>> queryInventory(InventoryQueryDTO query);

    /**
     * 查询药品在各药房的库存
     */
    Result<List<InventoryVO>> getDrugInventory(String drugId);

    /**
     * 查询药房库存
     */
    Result<PageResult<InventoryVO>> getPharmacyInventory(String pharmacyId, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 锁定库存
     */
    Result<Void> lockInventory(String inventoryId, BigDecimal quantity);

    /**
     * 解锁库存
     */
    Result<Void> unlockInventory(String inventoryId, BigDecimal quantity);

    /**
     * 查询效期预警库存
     */
    Result<List<ExpiryAlertVO>> getExpiryAlerts(Integer alertDays);

    /**
     * 查询库存不足预警
     */
    Result<List<StockAlertVO>> getLowStockAlerts();

    /**
     * 查询库存过剩预警
     */
    Result<List<StockAlertVO>> getOverStockAlerts();

    /**
     * 更新库存状态
     */
    Result<Void> updateInventoryStatus(String inventoryId, InventoryStatus status);

    /**
     * 库存盘点调整
     */
    Result<InventoryVO> adjustInventory(String inventoryId, BigDecimal actualQuantity, String reason, String operatorId);
}