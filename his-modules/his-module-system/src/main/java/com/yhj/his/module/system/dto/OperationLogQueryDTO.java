package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 */
@Data
@Schema(description = "操作日志查询请求")
public class OperationLogQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作模块")
    private String operationModule;

    @Schema(description = "操作结果: SUCCESS/FAILURE")
    private String operationResult;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码(从1开始)")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;
}