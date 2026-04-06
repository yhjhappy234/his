package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.dto.WarehouseDTO;
import com.yhj.his.module.inventory.service.WarehouseService;
import com.yhj.his.module.inventory.vo.WarehouseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库房信息控制器
 */
@Tag(name = "库房管理", description = "库房信息CRUD接口")
@RestController
@RequestMapping("/api/inventory/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @Operation(summary = "创建库房")
    @PostMapping
    public Result<WarehouseVO> create(@Valid @RequestBody WarehouseDTO dto) {
        return Result.success(service.create(dto));
    }

    @Operation(summary = "更新库房")
    @PutMapping("/{id}")
    public Result<WarehouseVO> update(
            @Parameter(description = "库房ID") @PathVariable String id,
            @Valid @RequestBody WarehouseDTO dto) {
        return Result.success(service.update(id, dto));
    }

    @Operation(summary = "删除库房")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "库房ID") @PathVariable String id) {
        service.delete(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询库房")
    @GetMapping("/{id}")
    public Result<WarehouseVO> getById(@Parameter(description = "库房ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据编码查询库房")
    @GetMapping("/code/{code}")
    public Result<WarehouseVO> getByCode(@Parameter(description = "库房编码") @PathVariable String code) {
        return Result.success(service.getByCode(code));
    }

    @Operation(summary = "分页查询库房")
    @GetMapping("/list")
    public Result<PageResult<WarehouseVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询库房")
    @PostMapping("/query")
    public Result<PageResult<WarehouseVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "根据类型查询库房")
    @GetMapping("/type/{warehouseType}")
    public Result<List<WarehouseVO>> listByType(
            @Parameter(description = "库房类型") @PathVariable String warehouseType) {
        return Result.success(service.listByType(warehouseType));
    }

    @Operation(summary = "根据科室查询库房")
    @GetMapping("/dept/{deptId}")
    public Result<List<WarehouseVO>> listByDept(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        return Result.success(service.listByDept(deptId));
    }

    @Operation(summary = "查询所有启用的库房")
    @GetMapping("/active")
    public Result<List<WarehouseVO>> listActive() {
        return Result.success(service.listActive());
    }
}