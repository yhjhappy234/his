package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 薪资记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_salary_record", indexes = {
    @Index(name = "idx_salary_emp", columnList = "employee_id"),
    @Index(name = "idx_salary_month", columnList = "salary_month"),
    @Index(name = "idx_salary_status", columnList = "status")
})
@Schema(description = "薪资记录")
public class SalaryRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "salary_no", length = 30, nullable = false, unique = true)
    @Schema(description = "薪资单号")
    private String salaryNo;

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

    @Column(name = "salary_month", length = 7, nullable = false)
    @Schema(description = "薪资月份(yyyy-MM)")
    private String salaryMonth;

    @Column(name = "base_salary", precision = 10, scale = 2)
    @Schema(description = "基本工资")
    private BigDecimal baseSalary;

    @Column(name = "position_salary", precision = 10, scale = 2)
    @Schema(description = "岗位工资")
    private BigDecimal positionSalary;

    @Column(name = "performance_salary", precision = 10, scale = 2)
    @Schema(description = "绩效工资")
    private BigDecimal performanceSalary;

    @Column(name = "seniority_salary", precision = 10, scale = 2)
    @Schema(description = "工龄工资")
    private BigDecimal senioritySalary;

    @Column(name = "night_allowance", precision = 10, scale = 2)
    @Schema(description = "夜班津贴")
    private BigDecimal nightAllowance;

    @Column(name = "overtime_pay", precision = 10, scale = 2)
    @Schema(description = "加班费")
    private BigDecimal overtimePay;

    @Column(name = "holiday_pay", precision = 10, scale = 2)
    @Schema(description = "节假日费")
    private BigDecimal holidayPay;

    @Column(name = "other_allowance", precision = 10, scale = 2)
    @Schema(description = "其他津贴")
    private BigDecimal otherAllowance;

    @Column(name = "gross_salary", precision = 10, scale = 2)
    @Schema(description = "应发合计")
    private BigDecimal grossSalary;

    @Column(name = "social_insurance", precision = 10, scale = 2)
    @Schema(description = "社保扣款")
    private BigDecimal socialInsurance;

    @Column(name = "housing_fund", precision = 10, scale = 2)
    @Schema(description = "公积金扣款")
    private BigDecimal housingFund;

    @Column(name = "income_tax", precision = 10, scale = 2)
    @Schema(description = "个人所得税")
    private BigDecimal incomeTax;

    @Column(name = "other_deduction", precision = 10, scale = 2)
    @Schema(description = "其他扣款")
    private BigDecimal otherDeduction;

    @Column(name = "total_deduction", precision = 10, scale = 2)
    @Schema(description = "扣款合计")
    private BigDecimal totalDeduction;

    @Column(name = "net_salary", precision = 10, scale = 2)
    @Schema(description = "实发工资")
    private BigDecimal netSalary;

    @Column(name = "work_days")
    @Schema(description = "应出勤天数")
    private Integer workDays;

    @Column(name = "actual_work_days")
    @Schema(description = "实际出勤天数")
    private Integer actualWorkDays;

    @Column(name = "overtime_hours", precision = 5, scale = 1)
    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Column(name = "leave_days", precision = 4, scale = 1)
    @Schema(description = "请假天数")
    private BigDecimal leaveDays;

    @Column(name = "calculate_time")
    @Schema(description = "计算时间")
    private LocalDateTime calculateTime;

    @Column(name = "calculator_id", length = 36)
    @Schema(description = "计算人ID")
    private String calculatorId;

    @Column(name = "calculator_name", length = 50)
    @Schema(description = "计算人姓名")
    private String calculatorName;

    @Column(name = "approver_id", length = 36)
    @Schema(description = "审核人ID")
    private String approverId;

    @Column(name = "approver_name", length = 50)
    @Schema(description = "审核人姓名")
    private String approverName;

    @Column(name = "approve_time")
    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(待审核/已审核/已发放)")
    private String status = "待审核";

    @Column(name = "pay_time")
    @Schema(description = "发放时间")
    private LocalDateTime payTime;

    @Column(name = "remark", length = 500)
    @Schema(description = "备注")
    private String remark;
}