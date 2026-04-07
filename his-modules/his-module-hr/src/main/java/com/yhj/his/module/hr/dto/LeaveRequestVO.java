package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请VO
 */
@Data
@Schema(description = "请假申请信息")
public class LeaveRequestVO {

    @Schema(description = "请假申请ID")
    private String id;

    @Schema(description = "申请单号")
    private String requestNo;

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

    @Schema(description = "请假类型")
    private String leaveType;

    @Schema(description = "请假原因")
    private String leaveReason;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "请假天数")
    private BigDecimal leaveDays;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "审批人ID")
    private String approverId;

    @Schema(description = "审批人姓名")
    private String approverName;

    @Schema(description = "审批时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批状态")
    private String approveStatus;

    @Schema(description = "审批意见")
    private String approveRemark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "附件URL")
    private String attachment;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}