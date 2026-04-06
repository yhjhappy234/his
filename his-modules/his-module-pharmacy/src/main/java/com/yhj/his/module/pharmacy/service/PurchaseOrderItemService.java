package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.entity.PurchaseOrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单明细服务接口
 */
public interface PurchaseOrderItemService {

    /**
     * 创建采购订单明细
     */
    Result<PurchaseOrderItem> createPurchaseOrderItem(PurchaseOrderItem item);

    /**
     * 更新采购订单明细
     */
    Result<PurchaseOrderItem> updatePurchaseOrderItem(PurchaseOrderItem item);

    /**
     * 根据ID删除采购订单明细
     */
    Result<Void> deletePurchaseOrderItem(String itemId);

    /**
     * 根据ID查询采购订单明细
     */
    Result<PurchaseOrderItem> getPurchaseOrderItemById(String itemId);

    /**
     * 根据订单ID查询明细列表
     */
    Result<List<PurchaseOrderItem>> getItemsByOrderId(String orderId);

    /**
     * 根据药品ID查询明细列表
     */
    Result<List<PurchaseOrderItem>> getItemsByDrugId(String drugId);

    /**
     * 更新已入库数量
     */
    Result<Void> updateReceivedQuantity(String itemId, BigDecimal receivedQuantity);

    /**
     * 增加已入库数量
     */
    Result<Void> addReceivedQuantity(String itemId, BigDecimal quantity);

    /**
     * 批量创建采购订单明细
     */
    Result<List<PurchaseOrderItem>> batchCreateItems(List<PurchaseOrderItem> items);

    /**
     * 删除订单的所有明细
     */
    Result<Void> deleteByOrderId(String orderId);

    /**
     * 批量查询采购订单明细
     */
    Result<List<PurchaseOrderItem>> getItemsByIds(List<String> itemIds);

    /**
     * 计算订单明细总金额
     */
    BigDecimal calculateTotalAmount(List<PurchaseOrderItem> items);

    /**
     * 计算订单明细总数量
     */
    BigDecimal calculateTotalQuantity(List<PurchaseOrderItem> items);
}