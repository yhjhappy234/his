package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 语音任务创建请求
 */
@Data
@Schema(description = "语音任务创建请求")
public class VoiceTaskCreateRequest {

    @Schema(description = "任务类型", required = true, example = "CALL_NUMBER")
    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @Schema(description = "模板编码", example = "CALL_STANDARD")
    private String templateCode;

    @Schema(description = "播报内容(不使用模板时必填)")
    private String content;

    @Schema(description = "模板参数")
    private Map<String, Object> params;

    @Schema(description = "目标设备ID列表")
    private List<String> targetDevices;

    @Schema(description = "目标设备分组编码列表")
    private List<String> targetGroups;

    @Schema(description = "优先级(1-10, 数字越小优先级越高)", example = "5")
    private Integer priority = 5;

    @Schema(description = "最大播放次数", example = "1")
    private Integer maxPlayCount = 1;

    @Schema(description = "计划播放时间")
    private LocalDateTime scheduledTime;

    @Schema(description = "语速(0.5-2.0)", example = "1.0")
    private Double speed;

    @Schema(description = "音量(0-100)", example = "80")
    private Integer volume;

    @Schema(description = "语音引擎", example = "SAPI")
    private String voiceEngine;

    @Schema(description = "语音名称", example = "Microsoft Huihui")
    private String voiceName;

    @Schema(description = "关联业务ID")
    private String bizId;

    @Schema(description = "关联业务类型")
    private String bizType;

    @Schema(description = "创建人ID")
    private String creatorId;

    @Schema(description = "创建人姓名")
    private String creatorName;
}