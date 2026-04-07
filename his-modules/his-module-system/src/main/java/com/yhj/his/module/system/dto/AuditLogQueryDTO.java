package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计日志查询DTO
 */
@Data
@Schema(description = "审计日志查询请求")
public class AuditLogQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "审计类型: LOGIN/PERMISSION/DATA/SYSTEM/SECURITY")
    private String auditType;

    @Schema(description = "审计级别: NORMAL/WARNING/CRITICAL")
    private String auditLevel;

    @Schema(description = "审计事件")
    private String auditEvent;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码(从1开始)")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}