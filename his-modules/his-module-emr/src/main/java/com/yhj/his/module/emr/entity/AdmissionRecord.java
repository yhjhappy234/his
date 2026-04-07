package com.yhj.his.module.emr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
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
 * 入院记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admission_record", indexes = {
    @Index(name = "idx_adm_admission_id", columnList = "admission_id"),
    @Index(name = "idx_adm_patient_id", columnList = "patient_id")
})
@Schema(description = "入院记录")
public class AdmissionRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "住院ID")
    @Column(name = "admission_id", length = 36, nullable = false)
    private String admissionId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "性别")
    @Column(name = "gender", length = 1)
    private String gender;

    @Schema(description = "年龄")
    @Column(name = "age")
    private Integer age;

    @Schema(description = "入院日期")
    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Schema(description = "科室ID")
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Schema(description = "病区ID")
    @Column(name = "ward_id", length = 20)
    private String wardId;

    @Schema(description = "床位号")
    @Column(name = "bed_no", length = 20)
    private String bedNo;

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

    @Schema(description = "婚育史")
    @Column(name = "marriage_history", columnDefinition = "TEXT")
    private String marriageHistory;

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
    @Schema(description = "入院诊断编码")
    @Column(name = "admission_diagnosis_code", length = 50)
    private String admissionDiagnosisCode;

    @Schema(description = "入院诊断名称")
    @Column(name = "admission_diagnosis_name", length = 200)
    private String admissionDiagnosisName;

    @Schema(description = "诊疗计划")
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    // 医生信息
    @Schema(description = "书写医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "书写医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "书写时间")
    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Schema(description = "上级医生ID")
    @Column(name = "superior_doctor_id", length = 20)
    private String superiorDoctorId;

    @Schema(description = "上级医生姓名")
    @Column(name = "superior_doctor_name", length = 50)
    private String superiorDoctorName;

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
}