package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AlertHandleDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.entity.MaterialAlert;
import com.yhj.his.module.inventory.service.MaterialAlertService;
import com.yhj.his.module.inventory.vo.MaterialAlertVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存预警Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/alerts")
@Tag(name = "库存预警管理", description = "库存预警查询和处理接口")
public class MaterialAlertController {

    @Autowired
    private MaterialAlertService materialAlertService;

    @GetMapping("/{id}")
    @Operation(summary = "查询预警详情", description = "根据ID查询预警详情")
    public Result<MaterialAlertVO> getById(@Parameter(description = "预警ID") @PathVariable String id) {
        return materialAlertService.findById(id)
                .map(entity -> Result.success(convertToVO(entity)))
                .orElse(Result.error("预警不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询预警", description = "分页查询所有预警")
    public Result<PageResult<MaterialAlertVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialAlert> page = materialAlertService.findAll(pageable);
        List<MaterialAlertVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/unhandled")
    @Operation(summary = "查询未处理的预警", description = "查询所有未处理的预警")
    public Result<List<MaterialAlertVO>> listUnhandled() {
        List<MaterialAlert> alerts = materialAlertService.findUnhandledAlerts();
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/type/{alertType}")
    @Operation(summary = "根据预警类型查询", description = "根据预警类型查询预警列表")
    public Result<List<MaterialAlertVO>> listByType(@Parameter(description = "预警类型") @PathVariable String alertType) {
        List<MaterialAlert> alerts = materialAlertService.findByAlertType(alertType);
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/material/{materialId}")
    @Operation(summary = "根据物资查询预警", description = "根据物资ID查询该物资的预警")
    public Result<List<MaterialAlertVO>> listByMaterialId(@Parameter(description = "物资ID") @PathVariable String materialId) {
        List<MaterialAlert> alerts = materialAlertService.findByMaterialId(materialId);
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/material/{materialId}/unhandled")
    @Operation(summary = "根据物资查询未处理预警", description = "根据物资ID查询该物资未处理的预警")
    public Result<List<MaterialAlertVO>> listUnhandledByMaterialId(@Parameter(description = "物资ID") @PathVariable String materialId) {
        List<MaterialAlert> alerts = materialAlertService.findUnhandledAlertsByMaterialId(materialId);
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询预警", description = "根据库房ID查询预警列表")
    public Result<List<MaterialAlertVO>> listByWarehouseId(@Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialAlert> alerts = materialAlertService.findByWarehouseId(warehouseId);
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "查询库存下限预警", description = "查询所有库存下限预警")
    public Result<List<MaterialAlertVO>> listLowStockAlerts() {
        List<MaterialAlert> alerts = materialAlertService.findByAlertType("LOW_STOCK");
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/high-stock")
    @Operation(summary = "查询库存上限预警", description = "查询所有库存上限预警")
    public Result<List<MaterialAlertVO>> listHighStockAlerts() {
        List<MaterialAlert> alerts = materialAlertService.findByAlertType("HIGH_STOCK");
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/expiry")
    @Operation(summary = "查询效期预警", description = "查询所有效期预警")
    public Result<List<MaterialAlertVO>> listExpiryAlerts() {
        List<MaterialAlert> alerts = materialAlertService.findByAlertType("EXPIRY");
        List<MaterialAlertVO> voList = alerts.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/handle")
    @Operation(summary = "处理预警", description = "处理单个预警")
    public Result<MaterialAlertVO> handle(@Valid @RequestBody AlertHandleDTO dto) {
        MaterialAlert alert = materialAlertService.handle(dto.getAlertId(), dto.getHandlerId(),
                dto.getHandlerName(), dto.getHandleRemark());
        return Result.success(convertToVO(alert));
    }

    @PostMapping("/batch-handle")
    @Operation(summary = "批量处理预警", description = "批量处理多个预警")
    public Result<Void> batchHandle(
            @RequestBody List<String> alertIds,
            @Parameter(description = "处理人ID") @RequestParam String handlerId,
            @Parameter(description = "处理人姓名") @RequestParam String handlerName,
            @Parameter(description = "处理备注") @RequestParam(required = false) String handleRemark) {
        materialAlertService.handleBatch(alertIds, handlerId, handlerName, handleRemark);
        return Result.success();
    }

    @PostMapping("/check")
    @Operation(summary = "执行预警检查", description = "执行库存预警检查并生成预警记录")
    public Result<Void> checkAndGenerateAlerts() {
        materialAlertService.checkAndGenerateAlerts();
        return Result.success();
    }

    @PostMapping("/check-low-stock")
    @Operation(summary = "检查低库存预警", description = "执行低库存预警检查")
    public Result<Void> checkLowStockAlerts() {
        materialAlertService.checkLowStockAlerts();
        return Result.success();
    }

    @PostMapping("/check-expiry")
    @Operation(summary = "检查效期预警", description = "执行效期预警检查")
    public Result<Void> checkExpiryAlerts() {
        materialAlertService.checkExpiryAlerts();
        return Result.success();
    }

    @GetMapping("/statistics")
    @Operation(summary = "预警统计", description = "获取预警统计数据")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = materialAlertService.getAlertStatistics();
        return Result.success(statistics);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预警记录", description = "删除预警记录")
    public Result<Void> delete(@Parameter(description = "预警ID") @PathVariable String id) {
        materialAlertService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除预警", description = "批量删除预警记录")
    public Result<Void> batchDelete(@RequestBody List<String> ids) {
        materialAlertService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询预警", description = "根据条件查询预警")
    public Result<PageResult<MaterialAlertVO>> search(
            @Parameter(description = "预警类型") @RequestParam(required = false) String alertType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "库房ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "物资ID") @RequestParam(required = false) String materialId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialAlert> page = materialAlertService.search(alertType, status, warehouseId, materialId, pageable);
        List<MaterialAlertVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    private MaterialAlertVO convertToVO(MaterialAlert entity) {
        MaterialAlertVO vo = new MaterialAlertVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}