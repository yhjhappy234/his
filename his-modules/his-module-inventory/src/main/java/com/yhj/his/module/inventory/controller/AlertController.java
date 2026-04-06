package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.service.AlertService;
import com.yhj.his.module.inventory.service.InventoryQueryService;
import com.yhj.his.module.inventory.vo.MaterialAlertVO;
import com.yhj.his.module.inventory.vo.MaterialInventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存预警控制器
 */
@Tag(name = "库存预警", description = "库存下限预警、效期预警接口")
@RestController
@RequestMapping("/api/inventory/v1/alert")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final InventoryQueryService inventoryQueryService;

    @Operation(summary = "查询库存下限预警")
    @GetMapping("/low-stock")
    public Result<List<MaterialAlertVO>> getLowStockAlerts() {
        return Result.success(alertService.getLowStockAlerts());
    }

    @Operation(summary = "查询效期预警")
    @GetMapping("/expiry")
    public Result<List<MaterialAlertVO>> getExpiryAlerts(
            @Parameter(description = "预警天数") @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(alertService.getExpiryAlerts(days));
    }

    @Operation(summary = "分页查询预警")
    @GetMapping("/list")
    public Result<PageResult<MaterialAlertVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(alertService.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询预警")
    @PostMapping("/query")
    public Result<PageResult<MaterialAlertVO>> query(@RequestBody QueryDTO query) {
        return Result.success(alertService.query(query));
    }

    @Operation(summary = "处理预警")
    @PostMapping("/handle/{alertId}")
    public Result<MaterialAlertVO> handle(
            @Parameter(description = "预警ID") @PathVariable String alertId,
            @Parameter(description = "处理人ID") @RequestParam String handlerId,
            @Parameter(description = "处理人姓名") @RequestParam String handlerName,
            @Parameter(description = "处理备注") @RequestParam(required = false) String remark) {
        return Result.success(alertService.handle(alertId, handlerId, handlerName, remark));
    }

    @Operation(summary = "批量处理预警")
    @PostMapping("/batch-handle")
    public Result<Void> batchHandle(
            @RequestBody List<String> alertIds,
            @Parameter(description = "处理人ID") @RequestParam String handlerId,
            @Parameter(description = "处理人姓名") @RequestParam String handlerName,
            @Parameter(description = "处理备注") @RequestParam(required = false) String remark) {
        alertService.batchHandle(alertIds, handlerId, handlerName, remark);
        return Result.success();
    }

    @Operation(summary = "执行预警检查")
    @PostMapping("/check")
    public Result<Void> checkAndGenerateAlerts() {
        alertService.checkAndGenerateAlerts();
        return Result.success();
    }

    @Operation(summary = "根据物资ID查询预警")
    @GetMapping("/material/{materialId}")
    public Result<List<MaterialAlertVO>> getByMaterialId(
            @Parameter(description = "物资ID") @PathVariable String materialId) {
        return Result.success(alertService.getByMaterialId(materialId));
    }

    // 库存查询接口
    @Operation(summary = "根据物资ID查询库存")
    @GetMapping("/inventory/material/{materialId}")
    public Result<List<MaterialInventoryVO>> getInventoryByMaterialId(
            @Parameter(description = "物资ID") @PathVariable String materialId) {
        return Result.success(inventoryQueryService.getByMaterialId(materialId));
    }

    @Operation(summary = "根据物资ID和库房ID查询库存")
    @GetMapping("/inventory/material/{materialId}/warehouse/{warehouseId}")
    public Result<List<MaterialInventoryVO>> getInventoryByMaterialIdAndWarehouseId(
            @Parameter(description = "物资ID") @PathVariable String materialId,
            @Parameter(description = "库房ID") @PathVariable String warehouseId) {
        return Result.success(inventoryQueryService.getByMaterialIdAndWarehouseId(materialId, warehouseId));
    }

    @Operation(summary = "根据库房ID查询库存")
    @GetMapping("/inventory/warehouse/{warehouseId}")
    public Result<PageResult<MaterialInventoryVO>> getInventoryByWarehouseId(
            @Parameter(description = "库房ID") @PathVariable String warehouseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(inventoryQueryService.getByWarehouseId(warehouseId, pageNum, pageSize));
    }

    @Operation(summary = "查询某物资总库存")
    @GetMapping("/inventory/total/{materialId}")
    public Result<BigDecimal> getTotalQuantity(
            @Parameter(description = "物资ID") @PathVariable String materialId) {
        return Result.success(inventoryQueryService.getTotalQuantity(materialId));
    }

    @Operation(summary = "查询某物资在某库房的总库存")
    @GetMapping("/inventory/total/{materialId}/warehouse/{warehouseId}")
    public Result<BigDecimal> getTotalQuantityByWarehouse(
            @Parameter(description = "物资ID") @PathVariable String materialId,
            @Parameter(description = "库房ID") @PathVariable String warehouseId) {
        return Result.success(inventoryQueryService.getTotalQuantityByWarehouse(materialId, warehouseId));
    }

    @Operation(summary = "条件查询库存")
    @PostMapping("/inventory/query")
    public Result<PageResult<MaterialInventoryVO>> queryInventory(@RequestBody QueryDTO query) {
        return Result.success(inventoryQueryService.query(query));
    }

    @Operation(summary = "查询效期库存")
    @GetMapping("/inventory/expiring")
    public Result<List<MaterialInventoryVO>> getExpiringInventory(
            @Parameter(description = "预警天数") @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(inventoryQueryService.getExpiringInventory(days));
    }
}