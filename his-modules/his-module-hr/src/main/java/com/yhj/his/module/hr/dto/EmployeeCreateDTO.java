package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工创建请求DTO
 */
@Data
@Schema(description = "员工创建请求")
public class EmployeeCreateDTO {

    @NotBlank(message = "员工工号不能为空")
    @Schema(description = "员工工号", required = true)
    private String employeeNo;

    @NotBlank(message = "员工姓名不能为空")
    @Schema(description = "员工姓名", required = true)
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

    @NotBlank(message = "所属科室不能为空")
    @Schema(description = "所属科室ID", required = true)
    private String deptId;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "职位级别")
    private Integer positionLevel;

    @NotNull(message = "入职日期不能为空")
    @Schema(description = "入职日期", required = true)
    private LocalDate employmentDate;

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