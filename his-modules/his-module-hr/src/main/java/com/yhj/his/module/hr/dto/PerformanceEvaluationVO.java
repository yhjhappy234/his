package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 绩效评分VO
 */
@Data
@Schema(description = "绩效评分信息")
public class PerformanceEvaluationVO {

    @Schema(description = "绩效评分ID")
    private String id;

    @Schema(description = "考核单号")
    private String evaluationNo;

    @Schema(description = "员工ID")
    private String employeeId;

    @Schema(description = "工号")
    private String employeeNo;

    @Schema(description = "姓名")
    private String employeeName;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "考核周期开始")
    private LocalDate periodStart;

    @Schema(description = "考核周期结束")
    private LocalDate periodEnd;

    @Schema(description = "考核类型")
    private String evaluationType;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "考核等级")
    private String level;

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

    @Schema(description = "考评人ID")
    private String evaluatorId;

    @Schema(description = "考评人姓名")
    private String evaluatorName;

    @Schema(description = "考评时间")
    private LocalDateTime evaluateTime;

    @Schema(description = "考评评语")
    private String comment;

    @Schema(description = "审核人ID")
    private String approverId;

    @Schema(description = "审核人姓名")
    private String approverName;

    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Schema(description = "审核状态")
    private String approveStatus;

    @Schema(description = "审核意见")
    private String approveRemark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}