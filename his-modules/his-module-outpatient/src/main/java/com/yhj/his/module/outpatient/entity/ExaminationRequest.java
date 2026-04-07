package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 检查检验申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "examination_request", indexes = {
        @Index(name = "idx_exam_registration", columnList = "registration_id"),
        @Index(name = "idx_exam_patient", columnList = "patient_id"),
        @Index(name = "idx_exam_status", columnList = "status")
})
public class ExaminationRequest extends BaseEntity {

    /**
     * 申请单号
     */
    @Column(name = "request_no", length = 30, unique = true)
    private String requestNo;

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
    @Column(name = "patient_name", length = 50)
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
     * 申请医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 申请医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 申请类型: 检验/检查
     */
    @Column(name = "request_type", length = 20, nullable = false)
    private String requestType;

    /**
     * 申请日期
     */
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

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
     * 检查/检验项目
     */
    @Column(name = "exam_items", columnDefinition = "TEXT")
    private String examItems;

    /**
     * 临床摘要
     */
    @Column(name = "clinical_summary", columnDefinition = "TEXT")
    private String clinicalSummary;

    /**
     * 是否急诊
     */
    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    /**
     * 收费状态: 未收费/已收费
     */
    @Column(name = "pay_status", length = 20)
    private String payStatus = "未收费";

    /**
     * 状态: 待检查/已完成/已取消
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "待检查";

    /**
     * 申请时间
     */
    @Column(name = "request_time")
    private LocalDateTime requestTime;

    /**
     * 完成时间
     */
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}