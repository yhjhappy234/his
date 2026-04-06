package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.*;
import com.yhj.his.module.system.service.UserService;
import com.yhj.his.module.system.vo.LoginVO;
import com.yhj.his.module.system.vo.UserPermissionVO;
import com.yhj.his.module.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户登录、注册、信息管理等接口")
@RestController
@RequestMapping("/api/system/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return userService.login(dto, clientIp);
    }

    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public Result<Void> logout(@Parameter(description = "用户ID") @RequestParam String userId) {
        return userService.logout(userId);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserDTO dto) {
        return userService.create(dto);
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<UserVO> update(@Valid @RequestBody UserDTO dto) {
        return userService.update(dto);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable String userId) {
        return userService.delete(userId);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> getById(@Parameter(description = "用户ID") @PathVariable String userId) {
        return userService.getById(userId);
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(
            @Parameter(description = "用户名") @RequestParam(required = false) String userName,
            @Parameter(description = "登录账号") @RequestParam(required = false) String loginName,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "电话") @RequestParam(required = false) String phone,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.page(userName, loginName, deptId, status, phone, pageNum, pageSize);
    }

    @Operation(summary = "获取用户权限")
    @GetMapping("/permission/{userId}")
    public Result<UserPermissionVO> getUserPermission(@Parameter(description = "用户ID") @PathVariable String userId) {
        return userService.getUserPermission(userId);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password/change")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Valid @RequestBody PasswordChangeDTO dto) {
        return userService.changePassword(userId, dto);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        return userService.resetPassword(userId, newPassword);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/status/{userId}")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable String userId,
            @Parameter(description = "状态") @RequestParam String status) {
        return userService.updateStatus(userId, status);
    }

    @Operation(summary = "授权用户角色")
    @PostMapping("/authorize")
    public Result<Void> authorizeRoles(@Valid @RequestBody UserAuthorizationDTO dto) {
        return userService.authorizeRoles(dto);
    }

    @Operation(summary = "获取科室用户列表")
    @GetMapping("/dept/{deptId}")
    public Result<List<UserVO>> listByDeptId(@Parameter(description = "科室ID") @PathVariable String deptId) {
        return userService.listByDeptId(deptId);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}