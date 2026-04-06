package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 批量排班请求DTO
 */
@Data
@Schema(description = "批量排班请求")
public class BatchScheduleDTO {

    @Schema(description = "员工ID列表")
    private List<String> employeeIds;

    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期", required = true)
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期", required = true)
    private LocalDate endDate;

    @Schema(description = "排班模板ID")
    private String templateId;

    @Schema(description = "排班规则：按星期配置班次")
    private List<DayScheduleConfig> dayConfigs;

    @Data
    @Schema(description = "按星期配置班次")
    public static class DayScheduleConfig {
        @Schema(description = "星期几(1-7)")
        private Integer weekDay;
        @Schema(description = "班次类型")
        private String scheduleType;
        @Schema(description = "上班时间")
        private String startTime;
        @Schema(description = "下班时间")
        private String endTime;
    }
}