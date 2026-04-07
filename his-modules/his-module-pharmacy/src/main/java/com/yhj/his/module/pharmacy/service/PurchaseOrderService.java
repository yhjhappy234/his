package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.PurchaseApplyDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseAuditDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseQueryDTO;
import com.yhj.his.module.pharmacy.dto.ReceiveConfirmDTO;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import com.yhj.his.module.pharmacy.vo.PurchaseOrderVO;

import java.util.List;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService {

    /**
     * 创建采购申请
     */
    Result<PurchaseOrderVO> createPurchaseOrder(PurchaseApplyDTO dto);

    /**
     * 更新采购订单
     */
    Result<PurchaseOrderVO> updatePurchaseOrder(String orderId, PurchaseApplyDTO dto);

    /**
     * 删除采购订单
     */
    Result<Void> deletePurchaseOrder(String orderId);

    /**
     * 根据ID查询采购订单
     */
    Result<PurchaseOrderVO> getPurchaseOrderById(String orderId);

    /**
     * 根据订单号查询
     */
    Result<PurchaseOrderVO> getPurchaseOrderByNo(String orderNo);

    /**
     * 分页查询采购订单列表
     */
    Result<PageResult<PurchaseOrderVO>> queryPurchaseOrders(PurchaseQueryDTO query);

    /**
     * 提交采购申请
     */
    Result<PurchaseOrderVO> submitPurchaseOrder(String orderId, String applicantId, String applicantName);

    /**
     * 审核采购订单
     */
    Result<PurchaseOrderVO> auditPurchaseOrder(String orderId, PurchaseAuditDTO dto);

    /**
     * 取消采购订单
     */
    Result<Void> cancelPurchaseOrder(String orderId, String reason);

    /**
     * 确认收货入库
     */
    Result<PurchaseOrderVO> confirmReceive(String orderId, ReceiveConfirmDTO dto);

    /**
     * 查询待审核采购订单
     */
    Result<List<PurchaseOrderVO>> getPendingAuditOrders();

    /**
     * 查询指定供应商的采购订单
     */
    Result<List<PurchaseOrderVO>> getOrdersBySupplier(String supplierId);

    /**
     * 更新订单状态
     */
    Result<Void> updateOrderStatus(String orderId, PurchaseOrderStatus status);
}