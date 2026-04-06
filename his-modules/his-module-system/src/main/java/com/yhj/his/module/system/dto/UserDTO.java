package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建/更新DTO
 */
@Data
@Schema(description = "用户创建/更新请求")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID(更新时必填)")
    private String id;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50")
    @Schema(description = "用户名", required = true)
    private String userName;

    @Schema(description = "登录账号(创建时必填)")
    @Size(max = 30, message = "登录账号长度不能超过30")
    private String loginName;

    @Schema(description = "密码(创建时必填，更新时可选)")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50之间")
    private String password;

    @Schema(description = "关联员工ID")
    @Size(max = 20, message = "员工ID长度不能超过20")
    private String employeeId;

    @Schema(description = "真实姓名")
    @Size(max = 50, message = "真实姓名长度不能超过50")
    private String realName;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "联系电话")
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50")
    private String email;

    @Schema(description = "身份证号")
    @Pattern(regexp = "^$|^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    private String idCard;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "登录方式")
    private String loginType;

    @Schema(description = "会话超时时间(分钟)")
    private Integer sessionTimeout;

    @Schema(description = "数据权限级别: ALL/DEPARTMENT/PERSONAL")
    private String dataScope;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    @Schema(description = "角色ID列表")
    private java.util.List<String> roleIds;
}