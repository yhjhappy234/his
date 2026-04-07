package com.yhj.his.module.pharmacy.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.PurchaseApplyDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseAuditDTO;
import com.yhj.his.module.pharmacy.dto.PurchaseQueryDTO;
import com.yhj.his.module.pharmacy.dto.ReceiveConfirmDTO;
import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import com.yhj.his.module.pharmacy.service.PurchaseOrderService;
import com.yhj.his.module.pharmacy.vo.PurchaseOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购订单控制器
 */
@Tag(name = "采购管理", description = "药品采购订单管理接口")
@RestController
@RequestMapping("/api/pharmacy/v1/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "创建采购订单", description = "创建新的采购申请")
    @PostMapping
    public Result<PurchaseOrderVO> createPurchaseOrder(@Valid @RequestBody PurchaseApplyDTO dto) {
        return purchaseOrderService.createPurchaseOrder(dto);
    }

    @Operation(summary = "更新采购订单", description = "更新采购订单信息(仅草稿状态)")
    @PutMapping("/{orderId}")
    public Result<PurchaseOrderVO> updatePurchaseOrder(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Valid @RequestBody PurchaseApplyDTO dto) {
        return purchaseOrderService.updatePurchaseOrder(orderId, dto);
    }

    @Operation(summary = "删除采购订单", description = "删除采购订单(仅草稿或已取消状态)")
    @DeleteMapping("/{orderId}")
    public Result<Void> deletePurchaseOrder(@Parameter(description = "订单ID") @PathVariable String orderId) {
        return purchaseOrderService.deletePurchaseOrder(orderId);
    }

    @Operation(summary = "获取订单详情", description = "根据ID查询采购订单详情")
    @GetMapping("/{orderId}")
    public Result<PurchaseOrderVO> getPurchaseOrder(
            @Parameter(description = "订单ID") @PathVariable String orderId) {
        return purchaseOrderService.getPurchaseOrderById(orderId);
    }

    @Operation(summary = "根据订单号查询", description = "根据订单号查询采购订单")
    @GetMapping("/no/{orderNo}")
    public Result<PurchaseOrderVO> getPurchaseOrderByNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        return purchaseOrderService.getPurchaseOrderByNo(orderNo);
    }

    @Operation(summary = "分页查询采购订单", description = "支持多条件分页查询采购订单")
    @PostMapping("/query")
    public Result<PageResult<PurchaseOrderVO>> queryPurchaseOrders(@RequestBody PurchaseQueryDTO dto) {
        return purchaseOrderService.queryPurchaseOrders(dto);
    }

    @Operation(summary = "提交采购申请", description = "提交采购申请进入审核流程")
    @PostMapping("/{orderId}/submit")
    public Result<PurchaseOrderVO> submitPurchaseOrder(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Parameter(description = "申请人ID") @RequestParam String applicantId,
            @Parameter(description = "申请人姓名") @RequestParam String applicantName) {
        return purchaseOrderService.submitPurchaseOrder(orderId, applicantId, applicantName);
    }

    @Operation(summary = "审核采购订单", description = "审核采购申请(通过/驳回)")
    @PostMapping("/{orderId}/audit")
    public Result<PurchaseOrderVO> auditPurchaseOrder(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Valid @RequestBody PurchaseAuditDTO dto) {
        return purchaseOrderService.auditPurchaseOrder(orderId, dto);
    }

    @Operation(summary = "取消采购订单", description = "取消采购订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelPurchaseOrder(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Parameter(description = "取消原因") @RequestParam String reason) {
        return purchaseOrderService.cancelPurchaseOrder(orderId, reason);
    }

    @Operation(summary = "确认收货入库", description = "确认采购药品入库")
    @PostMapping("/{orderId}/receive")
    public Result<PurchaseOrderVO> confirmReceive(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Valid @RequestBody ReceiveConfirmDTO dto) {
        return purchaseOrderService.confirmReceive(orderId, dto);
    }

    @Operation(summary = "查询待审核订单", description = "查询待审核状态的采购订单")
    @GetMapping("/pending-audit")
    public Result<List<PurchaseOrderVO>> getPendingAuditOrders() {
        return purchaseOrderService.getPendingAuditOrders();
    }

    @Operation(summary = "查询供应商订单", description = "查询指定供应商的采购订单")
    @GetMapping("/supplier/{supplierId}")
    public Result<List<PurchaseOrderVO>> getOrdersBySupplier(
            @Parameter(description = "供应商ID") @PathVariable String supplierId) {
        return purchaseOrderService.getOrdersBySupplier(supplierId);
    }

    @Operation(summary = "更新订单状态", description = "手动更新订单状态")
    @PutMapping("/{orderId}/status")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable String orderId,
            @Parameter(description = "订单状态") @RequestParam PurchaseOrderStatus status) {
        return purchaseOrderService.updateOrderStatus(orderId, status);
    }
}