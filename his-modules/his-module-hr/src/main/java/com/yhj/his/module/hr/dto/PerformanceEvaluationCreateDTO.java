package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 绩效评分创建请求DTO
 */
@Data
@Schema(description = "绩效评分创建请求")
public class PerformanceEvaluationCreateDTO {

    @Schema(description = "员工ID")
    private String employeeId;

    @Schema(description = "考核周期开始")
    private LocalDate periodStart;

    @Schema(description = "考核周期结束")
    private LocalDate periodEnd;

    @Schema(description = "考核类型(月度/季度/年度)")
    private String evaluationType;

    @Schema(description = "工作量得分")
    private BigDecimal workloadScore;

    @Schema(description = "质量得分")
    private BigDecimal qualityScore;

    @Schema(description = "服务得分")
    private BigDecimal serviceScore;

    @Schema(description = "考勤得分")
    private BigDecimal attendanceScore;

    @Schema(description = "其他得分")
    private BigDecimal otherScore;

    @Schema(description = "考评评语")
    private String comment;
}