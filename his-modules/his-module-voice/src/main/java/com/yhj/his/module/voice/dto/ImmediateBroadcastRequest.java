package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 立即播报请求
 */
@Data
@Schema(description = "立即播报请求")
public class ImmediateBroadcastRequest {

    @Schema(description = "播报内容", required = true)
    @NotBlank(message = "播报内容不能为空")
    private String content;

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "优先级(1-10, 数字越小优先级越高)", example = "1")
    private Integer priority = 1;

    @Schema(description = "语速(0.5-2.0)", example = "1.0")
    private Double speed = 1.0;

    @Schema(description = "音量(0-100)", example = "80")
    private Integer volume = 80;

    @Schema(description = "语音引擎", example = "SAPI")
    private String voiceEngine;

    @Schema(description = "语音名称", example = "Microsoft Huihui")
    private String voiceName;

    @Schema(description = "是否重复播报")
    private Boolean repeat = false;

    @Schema(description = "重复次数")
    private Integer repeatCount = 1;

    @Schema(description = "重复间隔(秒)")
    private Integer repeatInterval = 5;
}