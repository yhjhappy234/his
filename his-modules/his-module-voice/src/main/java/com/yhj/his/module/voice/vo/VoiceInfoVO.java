package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 语音信息VO
 */
@Data
@Schema(description = "语音信息响应")
public class VoiceInfoVO {

    @Schema(description = "语音名称")
    private String voiceName;

    @Schema(description = "语音语言")
    private String language;

    @Schema(description = "语音性别: Male/Female")
    private String gender;

    @Schema(description = "语音描述")
    private String description;

    @Schema(description = "是否可用")
    private Boolean available;

    @Schema(description = "语速范围: min-max")
    private String speedRange;

    @Schema(description = "音量范围: min-max")
    private String volumeRange;
}