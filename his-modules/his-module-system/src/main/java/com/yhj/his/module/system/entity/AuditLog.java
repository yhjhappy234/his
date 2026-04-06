package com.yhj.his.module.system.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.system.enums.AuditLevel;
import com.yhj.his.module.system.enums.AuditType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_audit_log", indexes = {
        @Index(name = "idx_audit_user_time", columnList = "user_id, audit_time"),
        @Index(name = "idx_audit_type_time", columnList = "audit_type, audit_time"),
        @Index(name = "idx_audit_level", columnList = "audit_level")
})
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 审计类型
     */
    @Column(name = "audit_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditType auditType;

    /**
     * 用户ID
     */
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * 登录账号
     */
    @Column(name = "login_name", length = 30)
    private String loginName;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 审计事件
     */
    @Column(name = "audit_event", length = 50)
    private String auditEvent;

    /**
     * 审计描述
     */
    @Column(name = "audit_desc", length = 200)
    private String auditDesc;

    /**
     * 审计级别
     */
    @Column(name = "audit_level", length = 20)
    @Enumerated(EnumType.STRING)
    private AuditLevel auditLevel;

    /**
     * 变更前数据
     */
    @Column(name = "before_data", columnDefinition = "TEXT")
    private String beforeData;

    /**
     * 变更后数据
     */
    @Column(name = "after_data", columnDefinition = "TEXT")
    private String afterData;

    /**
     * 客户端IP
     */
    @Column(name = "client_ip", length = 50)
    private String clientIp;

    /**
     * 审计时间
     */
    @Column(name = "audit_time", nullable = false)
    private LocalDateTime auditTime;

    /**
     * 是否已告警
     */
    @Column(name = "is_alerted")
    private Boolean isAlerted = false;

    /**
     * 告警时间
     */
    @Column(name = "alert_time")
    private LocalDateTime alertTime;

    /**
     * 告警方式
     */
    @Column(name = "alert_way", length = 20)
    private String alertWay;
}