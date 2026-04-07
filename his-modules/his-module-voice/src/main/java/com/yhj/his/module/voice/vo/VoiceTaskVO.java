package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 语音任务响应VO
 */
@Data
@Schema(description = "语音任务响应")
public class VoiceTaskVO {

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "任务编号")
    private String taskNo;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务类型描述")
    private String taskTypeDesc;

    @Schema(description = "播报内容")
    private String content;

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "任务状态描述")
    private String statusDesc;

    @Schema(description = "已播放次数")
    private Integer playCount;

    @Schema(description = "最大播放次数")
    private Integer maxPlayCount;

    @Schema(description = "计划播放时间")
    private LocalDateTime scheduledTime;

    @Schema(description = "播放开始时间")
    private LocalDateTime playStartTime;

    @Schema(description = "播放结束时间")
    private LocalDateTime playEndTime;

    @Schema(description = "播放时长(秒)")
    private Integer duration;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "语速")
    private Double speed;

    @Schema(description = "音量")
    private Integer volume;

    @Schema(description = "语音引擎")
    private String voiceEngine;

    @Schema(description = "语音名称")
    private String voiceName;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "关联业务ID")
    private String bizId;

    @Schema(description = "关联业务类型")
    private String bizType;

    @Schema(description = "预计播放时间")
    private LocalDateTime estimatedTime;
}