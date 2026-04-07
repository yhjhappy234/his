package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * TTS引擎配置VO
 */
@Data
@Schema(description = "TTS引擎配置响应")
public class TtsEngineVO {

    @Schema(description = "引擎名称")
    private String engineName;

    @Schema(description = "引擎类型: LOCAL-本地引擎, CLOUD-云端引擎")
    private String engineType;

    @Schema(description = "引擎描述")
    private String description;

    @Schema(description = "是否可用")
    private Boolean available;

    @Schema(description = "支持的语音列表")
    private java.util.List<VoiceInfoVO> voices;

    @Schema(description = "引擎优先级")
    private Integer priority;

    @Schema(description = "配置参数")
    private java.util.Map<String, Object> config;
}