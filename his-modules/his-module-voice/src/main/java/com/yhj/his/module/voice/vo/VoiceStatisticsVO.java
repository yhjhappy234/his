package com.yhj.his.module.voice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 播报统计VO
 */
@Data
@Schema(description = "播报统计响应")
public class VoiceStatisticsVO {

    @Schema(description = "总播报次数")
    private Long totalCount;

    @Schema(description = "成功次数")
    private Long successCount;

    @Schema(description = "失败次数")
    private Long failCount;

    @Schema(description = "成功率")
    private Double successRate;

    @Schema(description = "平均播放时长")
    private Double avgDuration;

    @Schema(description = "按任务类型统计")
    private Map<String, Long> typeCount;

    @Schema(description = "按设备分组统计")
    private Map<String, Long> groupCount;

    @Schema(description = "按日期统计")
    private Map<String, Long> dateCount;

    @Schema(description = "高峰时段(小时)")
    private Integer peakHour;

    @Schema(description = "高峰时段播报次数")
    private Long peakCount;
}