package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.MaterialCategoryDTO;
import com.yhj.his.module.inventory.service.MaterialCategoryService;
import com.yhj.his.module.inventory.vo.MaterialCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物资分类控制器
 */
@Tag(name = "物资分类管理", description = "物资分类CRUD接口")
@RestController
@RequestMapping("/api/inventory/v1/category")
@RequiredArgsConstructor
public class MaterialCategoryController {

    private final MaterialCategoryService service;

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<MaterialCategoryVO> create(@Valid @RequestBody MaterialCategoryDTO dto) {
        return Result.success(service.create(dto));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<MaterialCategoryVO> update(
            @Parameter(description = "分类ID") @PathVariable String id,
            @Valid @RequestBody MaterialCategoryDTO dto) {
        return Result.success(service.update(id, dto));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable String id) {
        service.delete(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public Result<MaterialCategoryVO> getById(@Parameter(description = "分类ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据编码查询分类")
    @GetMapping("/code/{code}")
    public Result<MaterialCategoryVO> getByCode(@Parameter(description = "分类编码") @PathVariable String code) {
        return Result.success(service.getByCode(code));
    }

    @Operation(summary = "分页查询分类")
    @GetMapping("/list")
    public Result<PageResult<MaterialCategoryVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "查询分类树")
    @GetMapping("/tree")
    public Result<List<MaterialCategoryVO>> tree() {
        return Result.success(service.tree());
    }

    @Operation(summary = "查询子分类")
    @GetMapping("/children/{parentId}")
    public Result<List<MaterialCategoryVO>> getChildren(
            @Parameter(description = "父分类ID") @PathVariable String parentId) {
        return Result.success(service.getChildren(parentId));
    }
}