package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
@Schema(description = "用户信息")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "关联员工ID")
    private String employeeId;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "用户类型")
    private String userType;

    @Schema(description = "登录方式")
    private String loginType;

    @Schema(description = "密码有效期")
    private LocalDate passwordExpiry;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "登录失败次数")
    private Integer loginFailCount;

    @Schema(description = "会话超时(分钟)")
    private Integer sessionTimeout;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "数据权限级别")
    private String dataScope;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "角色列表")
    private java.util.List<RoleVO> roles;
}