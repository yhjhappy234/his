package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志VO
 */
@Data
@Schema(description = "操作日志信息")
public class OperationLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID")
    private String id;

    @Schema(description = "日志编号")
    private String logNo;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "登录账号")
    private String loginName;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作模块")
    private String operationModule;

    @Schema(description = "操作功能")
    private String operationFunc;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParam;

    @Schema(description = "响应数据")
    private String responseData;

    @Schema(description = "操作结果")
    private String operationResult;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "耗时(ms)")
    private Integer duration;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "服务端IP")
    private String serverIp;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}