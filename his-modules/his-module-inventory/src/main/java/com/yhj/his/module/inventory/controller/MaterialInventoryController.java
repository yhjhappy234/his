package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.MaterialInventory;
import com.yhj.his.module.inventory.service.MaterialInventoryService;
import com.yhj.his.module.inventory.vo.MaterialInventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物资库存Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/inventory")
@Tag(name = "物资库存管理", description = "物资库存查询和管理接口")
public class MaterialInventoryController {

    @Autowired
    private MaterialInventoryService materialInventoryService;

    @GetMapping("/{id}")
    @Operation(summary = "查询库存详情", description = "根据ID查询库存详情")
    public Result<MaterialInventoryVO> getById(@Parameter(description = "库存ID") @PathVariable String id) {
        return materialInventoryService.findById(id)
                .map(entity -> Result.success(convertToVO(entity)))
                .orElse(Result.error("库存不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询库存", description = "分页查询所有库存")
    public Result<PageResult<MaterialInventoryVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialInventory> page = materialInventoryService.findAll(pageable);
        List<MaterialInventoryVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/material/{materialId}")
    @Operation(summary = "根据物资查询库存", description = "根据物资ID查询该物资的所有库存")
    public Result<List<MaterialInventoryVO>> getByMaterialId(@Parameter(description = "物资ID") @PathVariable String materialId) {
        List<MaterialInventory> inventories = materialInventoryService.findByMaterialId(materialId);
        List<MaterialInventoryVO> voList = inventories.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询库存", description = "根据库房ID分页查询该库房的库存")
    public Result<PageResult<MaterialInventoryVO>> getByWarehouseId(
            @Parameter(description = "库房ID") @PathVariable String warehouseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("expiryDate").ascending());
        Page<MaterialInventory> page = materialInventoryService.findAll(pageable);
        List<MaterialInventory> filtered = materialInventoryService.findByWarehouseId(warehouseId);
        List<MaterialInventoryVO> voList = filtered.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(voList, (long) voList.size(), pageNum, pageSize));
    }

    @GetMapping("/material/{materialId}/warehouse/{warehouseId}")
    @Operation(summary = "查询物资在指定库房的库存", description = "根据物资ID和库房ID查询库存")
    public Result<List<MaterialInventoryVO>> getByMaterialIdAndWarehouseId(
            @Parameter(description = "物资ID") @PathVariable String materialId,
            @Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialInventory> inventories = materialInventoryService.findByMaterialIdAndWarehouseId(materialId, warehouseId);
        List<MaterialInventoryVO> voList = inventories.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/material/{materialId}/total")
    @Operation(summary = "查询物资总库存", description = "查询某物资的总库存数量")
    public Result<BigDecimal> getTotalQuantity(@Parameter(description = "物资ID") @PathVariable String materialId) {
        BigDecimal total = materialInventoryService.sumQuantityByMaterialId(materialId);
        return Result.success(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/material/{materialId}/warehouse/{warehouseId}/total")
    @Operation(summary = "查询物资在指定库房的总库存", description = "查询某物资在某库房的总库存数量")
    public Result<BigDecimal> getTotalQuantityByWarehouse(
            @Parameter(description = "物资ID") @PathVariable String materialId,
            @Parameter(description = "库房ID") @PathVariable String warehouseId) {
        BigDecimal total = materialInventoryService.sumQuantityByMaterialIdAndWarehouseId(materialId, warehouseId);
        return Result.success(total != null ? total : BigDecimal.ZERO);
    }

    @GetMapping("/expiring")
    @Operation(summary = "查询效期库存", description = "查询即将过期的库存")
    public Result<List<MaterialInventoryVO>> getExpiringInventory(
            @Parameter(description = "预警天数") @RequestParam(defaultValue = "30") Integer days) {
        LocalDate expiryDate = LocalDate.now().plusDays(days);
        List<MaterialInventory> inventories = materialInventoryService.findByExpiryDateBefore(expiryDate);
        List<MaterialInventoryVO> voList = inventories.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/available/{materialId}/{warehouseId}")
    @Operation(summary = "查询可用库存(FIFO)", description = "查询可用库存(按先进先出排序)")
    public Result<List<MaterialInventoryVO>> getAvailableInventoryFIFO(
            @Parameter(description = "物资ID") @PathVariable String materialId,
            @Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialInventory> inventories = materialInventoryService.findAvailableInventoryOrderByExpiry(materialId, warehouseId);
        List<MaterialInventoryVO> voList = inventories.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/active")
    @Operation(summary = "查询所有有效库存", description = "查询所有有效库存记录")
    public Result<List<MaterialInventoryVO>> listActiveInventory() {
        List<MaterialInventory> inventories = materialInventoryService.findAllActiveInventory();
        List<MaterialInventoryVO> voList = inventories.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/check-available")
    @Operation(summary = "检查库存是否充足", description = "检查库存是否满足所需数量")
    public Result<Boolean> checkStockAvailable(
            @Parameter(description = "物资ID") @RequestParam String materialId,
            @Parameter(description = "库房ID") @RequestParam String warehouseId,
            @Parameter(description = "所需数量") @RequestParam BigDecimal requiredQuantity) {
        boolean available = materialInventoryService.checkStockAvailable(materialId, warehouseId, requiredQuantity);
        return Result.success(available);
    }

    @GetMapping("/available-quantity")
    @Operation(summary = "获取可用库存数量", description = "获取某物资在某库房的可用库存数量")
    public Result<BigDecimal> getAvailableQuantity(
            @Parameter(description = "物资ID") @RequestParam String materialId,
            @Parameter(description = "库房ID") @RequestParam String warehouseId) {
        BigDecimal available = materialInventoryService.getAvailableQuantity(materialId, warehouseId);
        return Result.success(available != null ? available : BigDecimal.ZERO);
    }

    private MaterialInventoryVO convertToVO(MaterialInventory entity) {
        MaterialInventoryVO vo = new MaterialInventoryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}