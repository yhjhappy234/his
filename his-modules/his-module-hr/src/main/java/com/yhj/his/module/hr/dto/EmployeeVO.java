package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工信息VO
 */
@Data
@Schema(description = "员工信息")
public class EmployeeVO {

    @Schema(description = "员工ID")
    private String id;

    @Schema(description = "员工工号")
    private String employeeNo;

    @Schema(description = "员工姓名")
    private String employeeName;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "照片URL")
    private String photo;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "毕业院校")
    private String school;

    @Schema(description = "专业")
    private String specialty;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "职称取得日期")
    private LocalDate titleDate;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "职位级别")
    private Integer positionLevel;

    @Schema(description = "入职日期")
    private LocalDate employmentDate;

    @Schema(description = "用工类型")
    private String employmentType;

    @Schema(description = "工龄")
    private Integer workYears;

    @Schema(description = "工资卡号")
    private String bankAccount;

    @Schema(description = "开户银行")
    private String bankName;

    @Schema(description = "基本工资")
    private BigDecimal baseSalary;

    @Schema(description = "岗位工资")
    private BigDecimal positionSalary;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "离职日期")
    private LocalDate leaveDate;

    @Schema(description = "离职原因")
    private String leaveReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}