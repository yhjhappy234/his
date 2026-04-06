package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门诊病历实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outpatient_record", indexes = {
        @Index(name = "idx_record_registration", columnList = "registration_id"),
        @Index(name = "idx_record_patient", columnList = "patient_id, visit_date"),
        @Index(name = "idx_record_doctor", columnList = "doctor_id, visit_date")
})
public class OutpatientRecord extends BaseEntity {

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
     * 就诊序号
     */
    @Column(name = "visit_no", length = 30)
    private String visitNo;

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
     * 医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 就诊日期
     */
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    /**
     * 主诉
     */
    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @Column(name = "present_illness", columnDefinition = "TEXT")
    private String presentIllness;

    /**
     * 既往史
     */
    @Column(name = "past_history", columnDefinition = "TEXT")
    private String pastHistory;

    /**
     * 过敏史
     */
    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;

    /**
     * 个人史
     */
    @Column(name = "personal_history", columnDefinition = "TEXT")
    private String personalHistory;

    /**
     * 家族史
     */
    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;

    /**
     * 体温
     */
    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    /**
     * 脉搏
     */
    @Column(name = "pulse")
    private Integer pulse;

    /**
     * 呼吸
     */
    @Column(name = "respiration")
    private Integer respiration;

    /**
     * 血压
     */
    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    /**
     * 身高(cm)
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 体重(kg)
     */
    @Column(name = "weight", precision = 5, scale = 1)
    private BigDecimal weight;

    /**
     * 体格检查
     */
    @Column(name = "physical_exam", columnDefinition = "TEXT")
    private String physicalExam;

    /**
     * 辅助检查
     */
    @Column(name = "auxiliary_exam", columnDefinition = "TEXT")
    private String auxiliaryExam;

    /**
     * 诊断编码(ICD-10)
     */
    @Column(name = "diagnosis_code", length = 50)
    private String diagnosisCode;

    /**
     * 诊断名称
     */
    @Column(name = "diagnosis_name", length = 200)
    private String diagnosisName;

    /**
     * 诊断类型: 主要/次要
     */
    @Column(name = "diagnosis_type", length = 20)
    private String diagnosisType;

    /**
     * 治疗方案
     */
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    /**
     * 医嘱/注意事项
     */
    @Column(name = "medical_advice", columnDefinition = "TEXT")
    private String medicalAdvice;

    /**
     * 状态: 草稿/已提交/已作废
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "草稿";

    /**
     * 提交时间
     */
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    /**
     * 备注
     */
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
}