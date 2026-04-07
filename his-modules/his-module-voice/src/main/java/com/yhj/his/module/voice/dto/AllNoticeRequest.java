package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 全员通知请求
 */
@Data
@Schema(description = "全员通知请求")
public class AllNoticeRequest {

    @Schema(description = "通知内容", required = true)
    @NotBlank(message = "通知内容不能为空")
    private String content;

    @Schema(description = "通知类型: SYSTEM-系统公告, EMERGENCY-紧急通知, MEETING-会议通知, FIRE-消防广播, WEATHER-天气预警", required = true)
    @NotBlank(message = "通知类型不能为空")
    private String noticeType;

    @Schema(description = "是否紧急")
    private Boolean urgent = false;

    @Schema(description = "目标设备分组编码列表(为空则全院广播)")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "优先级(1-10)", example = "1")
    private Integer priority = 1;

    @Schema(description = "是否重复播报")
    private Boolean repeat = false;

    @Schema(description = "重复次数")
    private Integer repeatCount = 1;

    @Schema(description = "重复间隔(秒)")
    private Integer repeatInterval = 10;

    @Schema(description = "操作人ID")
    private String operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;
}