package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪资记录VO
 */
@Data
@Schema(description = "薪资记录信息")
public class SalaryRecordVO {

    @Schema(description = "薪资记录ID")
    private String id;

    @Schema(description = "薪资单号")
    private String salaryNo;

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

    @Schema(description = "薪资月份")
    private String salaryMonth;

    @Schema(description = "基本工资")
    private BigDecimal baseSalary;

    @Schema(description = "岗位工资")
    private BigDecimal positionSalary;

    @Schema(description = "绩效工资")
    private BigDecimal performanceSalary;

    @Schema(description = "工龄工资")
    private BigDecimal senioritySalary;

    @Schema(description = "夜班津贴")
    private BigDecimal nightAllowance;

    @Schema(description = "加班费")
    private BigDecimal overtimePay;

    @Schema(description = "节假日费")
    private BigDecimal holidayPay;

    @Schema(description = "其他津贴")
    private BigDecimal otherAllowance;

    @Schema(description = "应发合计")
    private BigDecimal grossSalary;

    @Schema(description = "社保扣款")
    private BigDecimal socialInsurance;

    @Schema(description = "公积金扣款")
    private BigDecimal housingFund;

    @Schema(description = "个人所得税")
    private BigDecimal incomeTax;

    @Schema(description = "其他扣款")
    private BigDecimal otherDeduction;

    @Schema(description = "扣款合计")
    private BigDecimal totalDeduction;

    @Schema(description = "实发工资")
    private BigDecimal netSalary;

    @Schema(description = "应出勤天数")
    private Integer workDays;

    @Schema(description = "实际出勤天数")
    private Integer actualWorkDays;

    @Schema(description = "加班时长")
    private BigDecimal overtimeHours;

    @Schema(description = "请假天数")
    private BigDecimal leaveDays;

    @Schema(description = "计算时间")
    private LocalDateTime calculateTime;

    @Schema(description = "计算人姓名")
    private String calculatorName;

    @Schema(description = "审核人姓名")
    private String approverName;

    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "发放时间")
    private LocalDateTime payTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}