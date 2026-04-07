package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.MaterialDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.service.MaterialService;
import com.yhj.his.module.inventory.vo.MaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物资信息控制器
 */
@Tag(name = "物资信息管理", description = "物资CRUD接口")
@RestController
@RequestMapping("/api/inventory/v1/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService service;

    @Operation(summary = "创建物资")
    @PostMapping
    public Result<MaterialVO> create(@Valid @RequestBody MaterialDTO dto) {
        return Result.success(service.create(dto));
    }

    @Operation(summary = "更新物资")
    @PutMapping("/{id}")
    public Result<MaterialVO> update(
            @Parameter(description = "物资ID") @PathVariable String id,
            @Valid @RequestBody MaterialDTO dto) {
        return Result.success(service.update(id, dto));
    }

    @Operation(summary = "删除物资")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "物资ID") @PathVariable String id) {
        service.delete(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询物资")
    @GetMapping("/{id}")
    public Result<MaterialVO> getById(@Parameter(description = "物资ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据编码查询物资")
    @GetMapping("/code/{code}")
    public Result<MaterialVO> getByCode(@Parameter(description = "物资编码") @PathVariable String code) {
        return Result.success(service.getByCode(code));
    }

    @Operation(summary = "分页查询物资")
    @GetMapping("/list")
    public Result<PageResult<MaterialVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询物资")
    @PostMapping("/query")
    public Result<PageResult<MaterialVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "根据分类查询物资")
    @GetMapping("/category/{categoryId}")
    public Result<List<MaterialVO>> listByCategory(
            @Parameter(description = "分类ID") @PathVariable String categoryId) {
        return Result.success(service.listByCategory(categoryId));
    }

    @Operation(summary = "搜索物资")
    @GetMapping("/search")
    public Result<PageResult<MaterialVO>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.search(keyword, pageNum, pageSize));
    }
}