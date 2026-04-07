package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工更新请求DTO
 */
@Data
@Schema(description = "员工更新请求")
public class EmployeeUpdateDTO {

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

    @Schema(description = "职位")
    private String position;

    @Schema(description = "职位级别")
    private Integer positionLevel;

    @Schema(description = "用工类型")
    private String employmentType;

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

    @Schema(description = "备注")
    private String remark;
}