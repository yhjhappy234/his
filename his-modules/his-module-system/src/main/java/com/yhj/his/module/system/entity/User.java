package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.system.enums.DataScopeLevel;
import com.yhj.his.module.system.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_login_name", columnList = "login_name"),
        @Index(name = "idx_employee_id", columnList = "employee_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_dept_id", columnList = "dept_id")
})
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Column(name = "user_name", length = 50, nullable = false)
    private String userName;

    /**
     * 登录账号
     */
    @Column(name = "login_name", length = 30, nullable = false, unique = true)
    private String loginName;

    /**
     * 密码(加密)
     */
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    /**
     * 关联员工ID
     */
    @Column(name = "employee_id", length = 20)
    private String employeeId;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 所属科室ID
     */
    @Column(name = "dept_id", length = 36)
    private String deptId;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 50)
    private String email;

    /**
     * 身份证号
     */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /**
     * 用户类型
     */
    @Column(name = "user_type", length = 20)
    private String userType;

    /**
     * 登录方式
     */
    @Column(name = "login_type", length = 20)
    private String loginType;

    /**
     * 密码有效期
     */
    @Column(name = "password_expiry")
    private LocalDate passwordExpiry;

    /**
     * 密码修改时间
     */
    @Column(name = "password_update_time")
    private LocalDateTime passwordUpdateTime;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 登录失败次数
     */
    @Column(name = "login_fail_count", nullable = false)
    private Integer loginFailCount = 0;

    /**
     * 锁定时间
     */
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    /**
     * 会话超时(分钟)
     */
    @Column(name = "session_timeout", nullable = false)
    private Integer sessionTimeout = 30;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.NORMAL;

    /**
     * 数据权限级别
     */
    @Column(name = "data_scope", length = 20)
    @Enumerated(EnumType.STRING)
    private DataScopeLevel dataScope;

    /**
     * 头像
     */
    @Column(name = "avatar", length = 200)
    private String avatar;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}