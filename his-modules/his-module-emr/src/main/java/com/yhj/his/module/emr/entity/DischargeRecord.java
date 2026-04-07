package com.yhj.his.module.emr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.QcLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 出院记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "discharge_record", indexes = {
    @Index(name = "idx_dis_admission_id", columnList = "admission_id"),
    @Index(name = "idx_dis_patient_id", columnList = "patient_id")
})
@Schema(description = "出院记录")
public class DischargeRecord extends BaseEntity {

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

    @Schema(description = "入院日期")
    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Schema(description = "出院日期")
    @Column(name = "discharge_date")
    private LocalDate dischargeDate;

    @Schema(description = "住院天数")
    @Column(name = "hospital_days")
    private Integer hospitalDays;

    // 诊疗过程
    @Schema(description = "入院时情况")
    @Column(name = "admission_situation", columnDefinition = "TEXT")
    private String admissionSituation;

    @Schema(description = "诊疗经过")
    @Column(name = "treatment_process", columnDefinition = "TEXT")
    private String treatmentProcess;

    @Schema(description = "出院诊断编码")
    @Column(name = "discharge_diagnosis_code", length = 50)
    private String dischargeDiagnosisCode;

    @Schema(description = "出院诊断名称")
    @Column(name = "discharge_diagnosis_name", length = 200)
    private String dischargeDiagnosisName;

    @Schema(description = "出院时情况")
    @Column(name = "discharge_condition", columnDefinition = "TEXT")
    private String dischargeCondition;

    // 出院医嘱
    @Schema(description = "出院医嘱")
    @Column(name = "discharge_advice", columnDefinition = "TEXT")
    private String dischargeAdvice;

    @Schema(description = "出院带药")
    @Column(name = "discharge_medication", columnDefinition = "TEXT")
    private String dischargeMedication;

    @Schema(description = "复诊日期")
    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Schema(description = "复诊科室")
    @Column(name = "follow_up_dept", length = 100)
    private String followUpDept;

    // 医生信息
    @Schema(description = "医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "科室ID")
    @Column(name = "dept_id", length = 20)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

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
}