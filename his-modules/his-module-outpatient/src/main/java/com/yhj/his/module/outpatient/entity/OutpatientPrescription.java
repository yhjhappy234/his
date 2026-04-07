package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门诊处方实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outpatient_prescription", indexes = {
        @Index(name = "idx_pres_registration", columnList = "registration_id"),
        @Index(name = "idx_pres_patient", columnList = "patient_id"),
        @Index(name = "idx_pres_status", columnList = "status, prescription_date")
})
public class OutpatientPrescription extends BaseEntity {

    /**
     * 处方号
     */
    @Column(name = "prescription_no", length = 30, nullable = false, unique = true)
    private String prescriptionNo;

    /**
     * 挂号ID
     */
    @Column(name = "registration_id", length = 36, nullable = false)
    private String registrationId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name", length = 50, nullable = false)
    private String patientName;

    /**
     * 性别
     */
    @Column(name = "gender", length = 10)
    private String gender;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 科室ID
     */
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 开方医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 开方医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 处方类型: 西药/中药
     */
    @Column(name = "prescription_type", length = 20, nullable = false)
    private String prescriptionType;

    /**
     * 处方日期
     */
    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    /**
     * 诊断编码
     */
    @Column(name = "diagnosis_code", length = 50)
    private String diagnosisCode;

    /**
     * 诊断名称
     */
    @Column(name = "diagnosis_name", length = 200)
    private String diagnosisName;

    /**
     * 处方总金额
     */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 收费状态: 未收费/已收费/已退费
     */
    @Column(name = "pay_status", length = 20)
    private String payStatus = "未收费";

    /**
     * 收费时间
     */
    @Column(name = "pay_time")
    private LocalDateTime payTime;

    /**
     * 状态: 有效/已作废/已退费
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "有效";

    /**
     * 审核状态: 待审核/审核通过/审核不通过
     */
    @Column(name = "audit_status", length = 20)
    private String auditStatus;

    /**
     * 审核人ID
     */
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    /**
     * 审核人姓名
     */
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    /**
     * 处方备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}