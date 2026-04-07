package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 加班记录VO
 */
@Data
@Schema(description = "加班记录信息")
public class OvertimeVO {

    @Schema(description = "加班记录ID")
    private String id;

    @Schema(description = "加班单号")
    private String overtimeNo;

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

    @Schema(description = "加班日期")
    private LocalDate overtimeDate;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Schema(description = "加班类型")
    private String overtimeType;

    @Schema(description = "加班原因")
    private String overtimeReason;

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

    @Schema(description = "补偿类型")
    private String compensateType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}