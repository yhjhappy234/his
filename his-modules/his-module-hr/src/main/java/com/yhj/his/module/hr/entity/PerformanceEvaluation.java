package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 绩效评分实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_performance_evaluation", indexes = {
    @Index(name = "idx_eval_emp", columnList = "employee_id"),
    @Index(name = "idx_eval_period", columnList = "period_start, period_end"),
    @Index(name = "idx_eval_status", columnList = "status")
})
@Schema(description = "绩效评分")
public class PerformanceEvaluation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "evaluation_no", length = 30, nullable = false, unique = true)
    @Schema(description = "考核单号")
    private String evaluationNo;

    @Column(name = "employee_id", length = 36, nullable = false)
    @Schema(description = "员工ID")
    private String employeeId;

    @Column(name = "employee_no", length = 20)
    @Schema(description = "工号")
    private String employeeNo;

    @Column(name = "employee_name", length = 50)
    @Schema(description = "姓名")
    private String employeeName;

    @Column(name = "dept_id", length = 36)
    @Schema(description = "科室ID")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "period_start", nullable = false)
    @Schema(description = "考核周期开始")
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    @Schema(description = "考核周期结束")
    private LocalDate periodEnd;

    @Column(name = "evaluation_type", length = 20)
    @Schema(description = "考核类型(月度/季度/年度)")
    private String evaluationType;

    @Column(name = "total_score", precision = 6, scale = 2)
    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Column(name = "level", length = 10)
    @Schema(description = "考核等级(优秀/良好/合格/不合格)")
    private String level;

    @Column(name = "workload_score", precision = 6, scale = 2)
    @Schema(description = "工作量得分")
    private BigDecimal workloadScore;

    @Column(name = "quality_score", precision = 6, scale = 2)
    @Schema(description = "质量得分")
    private BigDecimal qualityScore;

    @Column(name = "service_score", precision = 6, scale = 2)
    @Schema(description = "服务得分")
    private BigDecimal serviceScore;

    @Column(name = "attendance_score", precision = 6, scale = 2)
    @Schema(description = "考勤得分")
    private BigDecimal attendanceScore;

    @Column(name = "other_score", precision = 6, scale = 2)
    @Schema(description = "其他得分")
    private BigDecimal otherScore;

    @Column(name = "evaluator_id", length = 36)
    @Schema(description = "考评人ID")
    private String evaluatorId;

    @Column(name = "evaluator_name", length = 50)
    @Schema(description = "考评人姓名")
    private String evaluatorName;

    @Column(name = "evaluate_time")
    @Schema(description = "考评时间")
    private LocalDateTime evaluateTime;

    @Column(name = "comment", length = 500)
    @Schema(description = "考评评语")
    private String comment;

    @Column(name = "approver_id", length = 36)
    @Schema(description = "审核人ID")
    private String approverId;

    @Column(name = "approver_name", length = 50)
    @Schema(description = "审核人姓名")
    private String approverName;

    @Column(name = "approve_time")
    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status", length = 20)
    @Schema(description = "审核状态")
    private ApprovalStatus approveStatus = ApprovalStatus.PENDING;

    @Column(name = "approve_remark", length = 200)
    @Schema(description = "审核意见")
    private String approveRemark;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态")
    private String status = "待审核";
}