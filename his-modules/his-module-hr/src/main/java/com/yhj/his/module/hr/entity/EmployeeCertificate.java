package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工证件资质实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_employee_certificate", indexes = {
    @Index(name = "idx_emp_cert_emp", columnList = "employee_id"),
    @Index(name = "idx_emp_cert_type", columnList = "cert_type")
})
@Schema(description = "员工证件资质")
public class EmployeeCertificate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "employee_id", length = 36, nullable = false)
    @Schema(description = "员工ID")
    private String employeeId;

    @Column(name = "employee_no", length = 20)
    @Schema(description = "员工工号")
    private String employeeNo;

    @Column(name = "employee_name", length = 50)
    @Schema(description = "员工姓名")
    private String employeeName;

    @Column(name = "cert_type", length = 20, nullable = false)
    @Schema(description = "证件类型(执业证书/资格证书/学历证书等)")
    private String certType;

    @Column(name = "cert_name", length = 100, nullable = false)
    @Schema(description = "证件名称")
    private String certName;

    @Column(name = "cert_no", length = 50)
    @Schema(description = "证件编号")
    private String certNo;

    @Column(name = "cert_level", length = 20)
    @Schema(description = "证件级别")
    private String certLevel;

    @Column(name = "issue_org", length = 100)
    @Schema(description = "发证机构")
    private String issueOrg;

    @Column(name = "issue_date")
    @Schema(description = "发证日期")
    private LocalDate issueDate;

    @Column(name = "valid_start_date")
    @Schema(description = "有效期开始")
    private LocalDate validStartDate;

    @Column(name = "valid_end_date")
    @Schema(description = "有效期结束")
    private LocalDate validEndDate;

    @Column(name = "cert_file", length = 200)
    @Schema(description = "证件文件URL")
    private String certFile;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(有效/过期/注销)")
    private String status = "有效";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}