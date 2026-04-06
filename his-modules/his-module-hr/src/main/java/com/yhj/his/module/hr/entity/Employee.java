package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.hr.enums.EmployeeStatus;
import com.yhj.his.module.hr.enums.EmploymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_employee", indexes = {
    @Index(name = "idx_employee_no", columnList = "employee_no"),
    @Index(name = "idx_dept", columnList = "dept_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "员工信息")
public class Employee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "employee_no", length = 20, nullable = false, unique = true)
    @Schema(description = "员工工号")
    private String employeeNo;

    @Column(name = "employee_name", length = 50, nullable = false)
    @Schema(description = "员工姓名")
    private String employeeName;

    @Column(name = "gender", length = 1)
    @Schema(description = "性别")
    private String gender;

    @Column(name = "birth_date")
    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Column(name = "id_card_no", length = 18)
    @Schema(description = "身份证号")
    private String idCardNo;

    @Column(name = "phone", length = 20)
    @Schema(description = "联系电话")
    private String phone;

    @Column(name = "email", length = 50)
    @Schema(description = "邮箱")
    private String email;

    @Column(name = "photo", length = 200)
    @Schema(description = "照片URL")
    private String photo;

    @Column(name = "education", length = 20)
    @Schema(description = "学历")
    private String education;

    @Column(name = "school", length = 100)
    @Schema(description = "毕业院校")
    private String school;

    @Column(name = "specialty", length = 50)
    @Schema(description = "专业")
    private String specialty;

    @Column(name = "title", length = 20)
    @Schema(description = "职称")
    private String title;

    @Column(name = "title_date")
    @Schema(description = "职称取得日期")
    private LocalDate titleDate;

    @Column(name = "dept_id", length = 36, nullable = false)
    @Schema(description = "所属科室ID")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "position", length = 50)
    @Schema(description = "职位")
    private String position;

    @Column(name = "position_level")
    @Schema(description = "职位级别")
    private Integer positionLevel;

    @Column(name = "employment_date", nullable = false)
    @Schema(description = "入职日期")
    private LocalDate employmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20)
    @Schema(description = "用工类型")
    private EmploymentType employmentType;

    @Column(name = "work_years")
    @Schema(description = "工龄")
    private Integer workYears;

    @Column(name = "bank_account", length = 30)
    @Schema(description = "工资卡号")
    private String bankAccount;

    @Column(name = "bank_name", length = 50)
    @Schema(description = "开户银行")
    private String bankName;

    @Column(name = "base_salary", precision = 10, scale = 2)
    @Schema(description = "基本工资")
    private BigDecimal baseSalary;

    @Column(name = "position_salary", precision = 10, scale = 2)
    @Schema(description = "岗位工资")
    private BigDecimal positionSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态")
    private EmployeeStatus status = EmployeeStatus.ON_JOB;

    @Column(name = "leave_date")
    @Schema(description = "离职日期")
    private LocalDate leaveDate;

    @Column(name = "leave_reason", length = 200)
    @Schema(description = "离职原因")
    private String leaveReason;
}