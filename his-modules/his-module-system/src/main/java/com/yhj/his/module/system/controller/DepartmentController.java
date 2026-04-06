package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.DepartmentDTO;
import com.yhj.his.module.system.service.DepartmentService;
import com.yhj.his.module.system.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室管理控制器
 */
@Tag(name = "科室管理", description = "科室创建、修改、删除、层级管理等接口")
@RestController
@RequestMapping("/api/system/v1/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "创建科室")
    @PostMapping
    public Result<DepartmentVO> create(@Valid @RequestBody DepartmentDTO dto) {
        return departmentService.create(dto);
    }

    @Operation(summary = "更新科室")
    @PutMapping
    public Result<DepartmentVO> update(@Valid @RequestBody DepartmentDTO dto) {
        return departmentService.update(dto);
    }

    @Operation(summary = "删除科室")
    @DeleteMapping("/{deptId}")
    public Result<Void> delete(@Parameter(description = "科室ID") @PathVariable String deptId) {
        return departmentService.delete(deptId);
    }

    @Operation(summary = "获取科室详情")
    @GetMapping("/{deptId}")
    public Result<DepartmentVO> getById(@Parameter(description = "科室ID") @PathVariable String deptId) {
        return departmentService.getById(deptId);
    }

    @Operation(summary = "分页查询科室")
    @GetMapping("/page")
    public Result<PageResult<DepartmentVO>> page(
            @Parameter(description = "科室名称") @RequestParam(required = false) String deptName,
            @Parameter(description = "科室编码") @RequestParam(required = false) String deptCode,
            @Parameter(description = "科室类型") @RequestParam(required = false) String deptType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "父级ID") @RequestParam(required = false) String parentId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return departmentService.page(deptName, deptCode, deptType, status, parentId, pageNum, pageSize);
    }

    @Operation(summary = "获取所有科室列表")
    @GetMapping("/list")
    public Result<List<DepartmentVO>> listAll() {
        return departmentService.listAll();
    }

    @Operation(summary = "获取科室树")
    @GetMapping("/tree")
    public Result<List<DepartmentVO>> getTree() {
        return departmentService.getTree();
    }

    @Operation(summary = "获取子科室列表")
    @GetMapping("/children/{parentId}")
    public Result<List<DepartmentVO>> listByParentId(@Parameter(description = "父级ID") @PathVariable String parentId) {
        return departmentService.listByParentId(parentId);
    }

    @Operation(summary = "根据科室类型查询")
    @GetMapping("/type/{deptType}")
    public Result<List<DepartmentVO>> listByType(@Parameter(description = "科室类型") @PathVariable String deptType) {
        return departmentService.listByType(deptType);
    }
}