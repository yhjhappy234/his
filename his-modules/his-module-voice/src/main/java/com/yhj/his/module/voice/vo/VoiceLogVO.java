package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 语音播报日志VO
 */
@Data
@Schema(description = "语音播报日志响应")
public class VoiceLogVO {

    @Schema(description = "日志ID")
    private String logId;

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

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备分组")
    private String deviceGroup;

    @Schema(description = "播放结果")
    private String playResult;

    @Schema(description = "播放结果描述")
    private String playResultDesc;

    @Schema(description = "播放时长(秒)")
    private Integer playDuration;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "播放时间")
    private LocalDateTime playTime;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}