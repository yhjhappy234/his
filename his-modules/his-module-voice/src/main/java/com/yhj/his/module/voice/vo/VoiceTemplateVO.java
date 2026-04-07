package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 语音模板VO
 */
@Data
@Schema(description = "语音模板响应")
public class VoiceTemplateVO {

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "模板类型描述")
    private String templateTypeDesc;

    @Schema(description = "内容模板")
    private String contentTemplate;

    @Schema(description = "参数定义")
    private List<Map<String, Object>> paramsDefine;

    @Schema(description = "语音引擎")
    private String voiceEngine;

    @Schema(description = "语音名称")
    private String voiceName;

    @Schema(description = "语速")
    private Double speed;

    @Schema(description = "音量")
    private Integer volume;

    @Schema(description = "音调")
    private Integer pitch;

    @Schema(description = "前置提示音")
    private String preAudio;

    @Schema(description = "后置提示音")
    private String postAudio;

    @Schema(description = "是否系统模板")
    private Boolean isSystem;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}