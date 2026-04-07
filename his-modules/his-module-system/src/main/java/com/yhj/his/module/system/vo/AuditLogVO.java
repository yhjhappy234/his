package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计日志VO
 */
@Data
@Schema(description = "审计日志信息")
public class AuditLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审计ID")
    private String id;

    @Schema(description = "审计类型")
    private String auditType;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "审计事件")
    private String auditEvent;

    @Schema(description = "审计描述")
    private String auditDesc;

    @Schema(description = "审计级别")
    private String auditLevel;

    @Schema(description = "变更前数据")
    private String beforeData;

    @Schema(description = "变更后数据")
    private String afterData;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "审计时间")
    private LocalDateTime auditTime;

    @Schema(description = "是否已告警")
    private Boolean isAlerted;

    @Schema(description = "告警时间")
    private LocalDateTime alertTime;

    @Schema(description = "告警方式")
    private String alertWay;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}