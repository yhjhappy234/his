package com.yhj.his.module.pharmacy.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.InventoryInDTO;
import com.yhj.his.module.pharmacy.dto.InventoryQueryDTO;
import com.yhj.his.module.pharmacy.enums.InventoryStatus;
import com.yhj.his.module.pharmacy.service.DrugInventoryService;
import com.yhj.his.module.pharmacy.vo.ExpiryAlertVO;
import com.yhj.his.module.pharmacy.vo.InventoryVO;
import com.yhj.his.module.pharmacy.vo.StockAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 药品库存控制器
 */
@Tag(name = "库存管理", description = "药品库存管理接口")
@RestController
@RequestMapping("/api/pharmacy/v1/inventory")
@RequiredArgsConstructor
public class DrugInventoryController {

    private final DrugInventoryService inventoryService;

    @Operation(summary = "库存入库", description = "药品入库操作")
    @PostMapping("/inbound")
    public Result<InventoryVO> inbound(@Valid @RequestBody InventoryInDTO dto) {
        return inventoryService.inbound(dto);
    }

    @Operation(summary = "库存出库", description = "药品出库操作")
    @PostMapping("/{inventoryId}/outbound")
    public Result<InventoryVO> outbound(
            @Parameter(description = "库存ID") @PathVariable String inventoryId,
            @Parameter(description = "出库数量") @RequestParam BigDecimal quantity,
            @Parameter(description = "出库原因") @RequestParam String reason,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return inventoryService.outbound(inventoryId, quantity, reason, operatorId);
    }

    @Operation(summary = "获取库存详情", description = "根据ID查询库存信息")
    @GetMapping("/{inventoryId}")
    public Result<InventoryVO> getInventory(
            @Parameter(description = "库存ID") @PathVariable String inventoryId) {
        return inventoryService.getInventoryById(inventoryId);
    }

    @Operation(summary = "分页查询库存列表", description = "支持多条件分页查询库存")
    @PostMapping("/query")
    public Result<PageResult<InventoryVO>> queryInventory(@RequestBody InventoryQueryDTO dto) {
        return inventoryService.queryInventory(dto);
    }

    @Operation(summary = "查询药品在各药房库存", description = "查询指定药品在各药房的库存情况")
    @GetMapping("/drug/{drugId}")
    public Result<List<InventoryVO>> getDrugInventory(
            @Parameter(description = "药品ID") @PathVariable String drugId) {
        return inventoryService.getDrugInventory(drugId);
    }

    @Operation(summary = "查询药房库存", description = "分页查询指定药房的库存列表")
    @GetMapping("/pharmacy/{pharmacyId}")
    public Result<PageResult<InventoryVO>> getPharmacyInventory(
            @Parameter(description = "药房ID") @PathVariable String pharmacyId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return inventoryService.getPharmacyInventory(pharmacyId, keyword, pageNum, pageSize);
    }

    @Operation(summary = "锁定库存", description = "锁定指定数量的库存")
    @PostMapping("/{inventoryId}/lock")
    public Result<Void> lockInventory(
            @Parameter(description = "库存ID") @PathVariable String inventoryId,
            @Parameter(description = "锁定数量") @RequestParam BigDecimal quantity) {
        return inventoryService.lockInventory(inventoryId, quantity);
    }

    @Operation(summary = "解锁库存", description = "解锁指定数量的库存")
    @PostMapping("/{inventoryId}/unlock")
    public Result<Void> unlockInventory(
            @Parameter(description = "库存ID") @PathVariable String inventoryId,
            @Parameter(description = "解锁数量") @RequestParam BigDecimal quantity) {
        return inventoryService.unlockInventory(inventoryId, quantity);
    }

    @Operation(summary = "效期预警查询", description = "查询即将过期或已过期的药品库存")
    @GetMapping("/expiry-alerts")
    public Result<List<ExpiryAlertVO>> getExpiryAlerts(
            @Parameter(description = "预警天数") @RequestParam(defaultValue = "180") Integer alertDays) {
        return inventoryService.getExpiryAlerts(alertDays);
    }

    @Operation(summary = "库存不足预警", description = "查询库存低于下限的药品")
    @GetMapping("/low-stock-alerts")
    public Result<List<StockAlertVO>> getLowStockAlerts() {
        return inventoryService.getLowStockAlerts();
    }

    @Operation(summary = "库存过剩预警", description = "查询库存超过上限的药品")
    @GetMapping("/over-stock-alerts")
    public Result<List<StockAlertVO>> getOverStockAlerts() {
        return inventoryService.getOverStockAlerts();
    }

    @Operation(summary = "更新库存状态", description = "更新库存状态(正常/冻结/过期等)")
    @PutMapping("/{inventoryId}/status")
    public Result<Void> updateInventoryStatus(
            @Parameter(description = "库存ID") @PathVariable String inventoryId,
            @Parameter(description = "库存状态") @RequestParam InventoryStatus status) {
        return inventoryService.updateInventoryStatus(inventoryId, status);
    }

    @Operation(summary = "库存盘点调整", description = "库存盘点后的数量调整")
    @PostMapping("/{inventoryId}/adjust")
    public Result<InventoryVO> adjustInventory(
            @Parameter(description = "库存ID") @PathVariable String inventoryId,
            @Parameter(description = "实际数量") @RequestParam BigDecimal actualQuantity,
            @Parameter(description = "调整原因") @RequestParam String reason,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return inventoryService.adjustInventory(inventoryId, actualQuantity, reason, operatorId);
    }
}