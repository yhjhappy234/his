package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.LeaveType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_leave_request", indexes = {
    @Index(name = "idx_leave_emp", columnList = "employee_id"),
    @Index(name = "idx_leave_status", columnList = "status"),
    @Index(name = "idx_leave_dates", columnList = "start_date, end_date")
})
@Schema(description = "请假申请")
public class LeaveRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "request_no", length = 30, nullable = false, unique = true)
    @Schema(description = "申请单号")
    private String requestNo;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", length = 20, nullable = false)
    @Schema(description = "请假类型")
    private LeaveType leaveType;

    @Column(name = "leave_reason", length = 500)
    @Schema(description = "请假原因")
    private String leaveReason;

    @Column(name = "start_date", nullable = false)
    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Column(name = "leave_days", precision = 4, scale = 1)
    @Schema(description = "请假天数")
    private BigDecimal leaveDays;

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

    @Column(name = "attachment", length = 500)
    @Schema(description = "附件URL")
    private String attachment;
}