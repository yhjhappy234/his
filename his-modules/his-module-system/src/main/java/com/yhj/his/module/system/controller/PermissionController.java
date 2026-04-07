package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.PermissionDTO;
import com.yhj.his.module.system.service.PermissionService;
import com.yhj.his.module.system.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@Tag(name = "权限管理", description = "权限创建、修改、删除等接口")
@RestController
@RequestMapping("/api/system/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "创建权限")
    @PostMapping
    public Result<PermissionVO> create(@Valid @RequestBody PermissionDTO dto) {
        return permissionService.create(dto);
    }

    @Operation(summary = "更新权限")
    @PutMapping
    public Result<PermissionVO> update(@Valid @RequestBody PermissionDTO dto) {
        return permissionService.update(dto);
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{permId}")
    public Result<Void> delete(@Parameter(description = "权限ID") @PathVariable String permId) {
        return permissionService.delete(permId);
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{permId}")
    public Result<PermissionVO> getById(@Parameter(description = "权限ID") @PathVariable String permId) {
        return permissionService.getById(permId);
    }

    @Operation(summary = "分页查询权限")
    @GetMapping("/page")
    public Result<PageResult<PermissionVO>> page(
            @Parameter(description = "权限名称") @RequestParam(required = false) String permName,
            @Parameter(description = "权限编码") @RequestParam(required = false) String permCode,
            @Parameter(description = "权限类型") @RequestParam(required = false) String permType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return permissionService.page(permName, permCode, permType, status, pageNum, pageSize);
    }

    @Operation(summary = "获取所有权限列表")
    @GetMapping("/list")
    public Result<List<PermissionVO>> listAll() {
        return permissionService.listAll();
    }

    @Operation(summary = "获取菜单权限树")
    @GetMapping("/menu/tree")
    public Result<List<PermissionVO>> getMenuTree() {
        return permissionService.getMenuTree();
    }

    @Operation(summary = "获取用户菜单权限")
    @GetMapping("/menu/user/{userId}")
    public Result<List<PermissionVO>> getUserMenus(@Parameter(description = "用户ID") @PathVariable String userId) {
        return permissionService.getUserMenus(userId);
    }

    @Operation(summary = "获取子权限列表")
    @GetMapping("/children/{parentId}")
    public Result<List<PermissionVO>> listByParentId(@Parameter(description = "父级ID") @PathVariable String parentId) {
        return permissionService.listByParentId(parentId);
    }
}