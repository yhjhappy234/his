package com.yhj.his.module.emr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "outpatient_emr", indexes = {
    @Index(name = "idx_visit_id", columnList = "visit_id"),
    @Index(name = "idx_patient_id", columnList = "patient_id"),
    @Index(name = "idx_visit_date", columnList = "visit_date"),
    @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "门诊病历")
public class OutpatientEmr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "就诊ID")
    @Column(name = "visit_id", length = 36, nullable = false)
    private String visitId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "就诊日期")
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Schema(description = "就诊序号")
    @Column(name = "visit_no", length = 30)
    private String visitNo;

    @Schema(description = "科室ID")
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Schema(description = "医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    // 主诉与病史
    @Schema(description = "主诉")
    @Column(name = "chief_complaint", columnDefinition = "TEXT", nullable = false)
    private String chiefComplaint;

    @Schema(description = "现病史")
    @Column(name = "present_illness", columnDefinition = "TEXT", nullable = false)
    private String presentIllness;

    @Schema(description = "既往史")
    @Column(name = "past_history", columnDefinition = "TEXT")
    private String pastHistory;

    @Schema(description = "个人史")
    @Column(name = "personal_history", columnDefinition = "TEXT")
    private String personalHistory;

    @Schema(description = "家族史")
    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;

    @Schema(description = "过敏史")
    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;

    // 体格检查
    @Schema(description = "体温")
    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    @Schema(description = "脉搏")
    @Column(name = "pulse")
    private Integer pulse;

    @Schema(description = "呼吸")
    @Column(name = "respiration")
    private Integer respiration;

    @Schema(description = "血压")
    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Schema(description = "体重(kg)")
    @Column(name = "weight", precision = 5, scale = 1)
    private BigDecimal weight;

    @Schema(description = "身高(cm)")
    @Column(name = "height")
    private Integer height;

    @Schema(description = "一般检查")
    @Column(name = "general_exam", columnDefinition = "TEXT")
    private String generalExam;

    @Schema(description = "专科检查")
    @Column(name = "specialist_exam", columnDefinition = "TEXT")
    private String specialistExam;

    @Schema(description = "辅助检查")
    @Column(name = "auxiliary_exam", columnDefinition = "TEXT")
    private String auxiliaryExam;

    // 诊断与治疗
    @Schema(description = "主要诊断编码")
    @Column(name = "primary_diagnosis_code", length = 50)
    private String primaryDiagnosisCode;

    @Schema(description = "主要诊断名称")
    @Column(name = "primary_diagnosis_name", length = 200)
    private String primaryDiagnosisName;

    @Schema(description = "次要诊断")
    @Column(name = "secondary_diagnosis", columnDefinition = "TEXT")
    private String secondaryDiagnosis;

    @Schema(description = "治疗方案")
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Schema(description = "医嘱/注意事项")
    @Column(name = "medical_advice", columnDefinition = "TEXT")
    private String medicalAdvice;

    @Schema(description = "模板ID")
    @Column(name = "template_id", length = 36)
    private String templateId;

    // 状态与质控
    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EmrStatus status = EmrStatus.DRAFT;

    @Schema(description = "质控评分")
    @Column(name = "qc_score")
    private Integer qcScore;

    @Schema(description = "质控等级")
    @Column(name = "qc_level", length = 20)
    @Enumerated(EnumType.STRING)
    private QcLevel qcLevel;

    @Schema(description = "缺陷列表(JSON)")
    @Column(name = "qc_defects", columnDefinition = "TEXT")
    private String qcDefects;

    @Schema(description = "提交时间")
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Schema(description = "审核时间")
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID")
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    @Schema(description = "审核人姓名")
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    @Schema(description = "审核意见")
    @Column(name = "audit_comment", length = 500)
    private String auditComment;
}