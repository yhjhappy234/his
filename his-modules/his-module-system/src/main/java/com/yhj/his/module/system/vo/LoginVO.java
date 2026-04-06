package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录响应VO
 */
@Data
@Schema(description = "登录响应")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "Token过期时间")
    private LocalDateTime tokenExpiry;

    @Schema(description = "角色列表")
    private java.util.List<RoleVO> roles;

    @Schema(description = "权限编码列表")
    private java.util.List<String> permissions;

    @Schema(description = "数据权限级别")
    private String dataScope;
}