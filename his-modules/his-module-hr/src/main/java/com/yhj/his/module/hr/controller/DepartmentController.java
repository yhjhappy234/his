package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.DepartmentType;
import com.yhj.his.module.hr.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室管理Controller
 */
@RestController("hrDepartmentController")
@RequestMapping("/api/hr/v1/department")
@RequiredArgsConstructor
@Tag(name = "科室管理", description = "科室组织架构管理接口")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "创建科室", description = "创建新科室")
    public Result<DepartmentVO> createDepartment(@Valid @RequestBody DepartmentCreateDTO dto) {
        DepartmentVO vo = departmentService.createDepartment(dto);
        return Result.success(vo);
    }

    @PutMapping("/{deptId}")
    @Operation(summary = "更新科室", description = "更新科室信息")
    public Result<DepartmentVO> updateDepartment(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Valid @RequestBody DepartmentCreateDTO dto) {
        DepartmentVO vo = departmentService.updateDepartment(deptId, dto);
        return Result.success(vo);
    }

    @DeleteMapping("/{deptId}")
    @Operation(summary = "删除科室", description = "删除科室")
    public Result<Void> deleteDepartment(@Parameter(description = "科室ID") @PathVariable String deptId) {
        departmentService.deleteDepartment(deptId);
        return Result.success();
    }

    @GetMapping("/{deptId}")
    @Operation(summary = "获取科室详情", description = "根据ID获取科室详情")
    public Result<DepartmentVO> getDepartment(@Parameter(description = "科室ID") @PathVariable String deptId) {
        DepartmentVO vo = departmentService.getDepartmentById(deptId);
        return Result.success(vo);
    }

    @GetMapping("/code/{deptCode}")
    @Operation(summary = "根据编码获取科室", description = "根据编码获取科室信息")
    public Result<DepartmentVO> getDepartmentByCode(@Parameter(description = "科室编码") @PathVariable String deptCode) {
        DepartmentVO vo = departmentService.getDepartmentByCode(deptCode);
        return Result.success(vo);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询科室", description = "分页查询科室列表")
    public Result<PageResult<DepartmentVO>> listDepartments(
            @Parameter(description = "科室类型") @RequestParam(required = false) String deptType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        DepartmentType type = deptType != null ? DepartmentType.valueOf(deptType) : null;
        PageResult<DepartmentVO> result = departmentService.listDepartments(type, status, keyword, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取科室树", description = "获取科室树形结构")
    public Result<List<DepartmentVO>> getDepartmentTree() {
        List<DepartmentVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }

    @GetMapping("/{deptId}/children")
    @Operation(summary = "获取子科室", description = "获取指定科室的子科室列表")
    public Result<List<DepartmentVO>> listChildDepartments(@Parameter(description = "科室ID") @PathVariable String deptId) {
        List<DepartmentVO> list = departmentService.listChildDepartments(deptId);
        return Result.success(list);
    }

    @GetMapping("/type/{deptType}")
    @Operation(summary = "按类型查询科室", description = "按类型查询科室列表")
    public Result<List<DepartmentVO>> listDepartmentsByType(@Parameter(description = "科室类型") @PathVariable String deptType) {
        DepartmentType type = DepartmentType.valueOf(deptType);
        List<DepartmentVO> list = departmentService.listDepartmentsByType(type);
        return Result.success(list);
    }
}