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
 * 加班记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_overtime", indexes = {
    @Index(name = "idx_overtime_emp", columnList = "employee_id"),
    @Index(name = "idx_overtime_status", columnList = "status"),
    @Index(name = "idx_overtime_date", columnList = "overtime_date")
})
@Schema(description = "加班记录")
public class Overtime extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "overtime_no", length = 30, nullable = false, unique = true)
    @Schema(description = "加班单号")
    private String overtimeNo;

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

    @Column(name = "overtime_date", nullable = false)
    @Schema(description = "加班日期")
    private LocalDate overtimeDate;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Column(name = "overtime_hours", precision = 4, scale = 1, nullable = false)
    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Column(name = "overtime_type", length = 20)
    @Schema(description = "加班类型(工作日加班/周末加班/节假日加班)")
    private String overtimeType;

    @Column(name = "overtime_reason", length = 500)
    @Schema(description = "加班原因")
    private String overtimeReason;

    @Column(name = "apply_time", nullable = false)
    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Column(name = "approver_id", length = 36)
    @Schema(description = "审批人ID")
    private String approverId;

    @Column(name = "approver_name", length = 50)
    @Schema(description = "审批人姓名")
    private String approverName;

    @Column(name = "approve_time")
    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status", length = 20)
    @Schema(description = "审批状态")
    private ApprovalStatus approveStatus = ApprovalStatus.PENDING;

    @Column(name = "approve_remark", length = 200)
    @Schema(description = "审批意见")
    private String approveRemark;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态")
    private String status = "待审批";

    @Column(name = "compensate_type", length = 20)
    @Schema(description = "补偿类型(调休/加班费)")
    private String compensateType;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}