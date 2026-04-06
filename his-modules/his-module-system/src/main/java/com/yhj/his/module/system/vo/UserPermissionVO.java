package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户权限信息VO
 */
@Data
@Schema(description = "用户权限信息")
public class UserPermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "角色列表")
    private List<RoleVO> roles;

    @Schema(description = "权限编码列表")
    private List<String> permissions;

    @Schema(description = "菜单权限列表")
    private List<PermissionVO> menus;

    @Schema(description = "数据权限级别")
    private String dataScope;
}