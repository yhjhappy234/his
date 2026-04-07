package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.RoleAuthorizationDTO;
import com.yhj.his.module.system.dto.RoleDTO;
import com.yhj.his.module.system.service.RoleService;
import com.yhj.his.module.system.vo.PermissionVO;
import com.yhj.his.module.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理", description = "角色创建、修改、删除、授权等接口")
@RestController
@RequestMapping("/api/system/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<RoleVO> create(@Valid @RequestBody RoleDTO dto) {
        return roleService.create(dto);
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public Result<RoleVO> update(@Valid @RequestBody RoleDTO dto) {
        return roleService.update(dto);
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable String roleId) {
        return roleService.delete(roleId);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getById(@Parameter(description = "角色ID") @PathVariable String roleId) {
        return roleService.getById(roleId);
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<RoleVO>> page(
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色编码") @RequestParam(required = false) String roleCode,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return roleService.page(roleName, roleCode, status, pageNum, pageSize);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/list")
    public Result<List<RoleVO>> listAll() {
        return roleService.listAll();
    }

    @Operation(summary = "授权角色权限")
    @PostMapping("/authorize")
    public Result<Void> authorizePermissions(@Valid @RequestBody RoleAuthorizationDTO dto) {
        return roleService.authorizePermissions(dto);
    }

    @Operation(summary = "获取角色权限列表")
    @GetMapping("/permission/{roleId}")
    public Result<List<PermissionVO>> getRolePermissions(@Parameter(description = "角色ID") @PathVariable String roleId) {
        return roleService.getRolePermissions(roleId);
    }

    @Operation(summary = "获取用户角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<RoleVO>> getUserRoles(@Parameter(description = "用户ID") @PathVariable String userId) {
        return roleService.getUserRoles(userId);
    }
}