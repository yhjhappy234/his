package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 语音模板创建/更新请求
 */
@Data
@Schema(description = "语音模板请求")
public class VoiceTemplateRequest {

    @Schema(description = "模板编码", required = true, example = "CALL_STANDARD")
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板名称", required = true)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "模板类型", required = true, example = "CALL_STANDARD")
    @NotBlank(message = "模板类型不能为空")
    private String templateType;

    @Schema(description = "内容模板(支持占位符如{patientName})", required = true)
    @NotBlank(message = "内容模板不能为空")
    private String contentTemplate;

    @Schema(description = "参数定义(JSON格式)")
    private List<Map<String, Object>> paramsDefine;

    @Schema(description = "语音引擎")
    private String voiceEngine;

    @Schema(description = "语音名称")
    private String voiceName;

    @Schema(description = "语速(0.5-2.0)", example = "1.0")
    private Double speed = 1.0;

    @Schema(description = "音量(0-100)", example = "80")
    private Integer volume = 80;

    @Schema(description = "音调(0-100)", example = "50")
    private Integer pitch = 50;

    @Schema(description = "前置提示音文件路径")
    private String preAudio;

    @Schema(description = "后置提示音文件路径")
    private String postAudio;

    @Schema(description = "是否启用")
    private Boolean isEnabled = true;

    @Schema(description = "排序号")
    private Integer sortOrder = 0;

    @Schema(description = "备注")
    private String remark;
}