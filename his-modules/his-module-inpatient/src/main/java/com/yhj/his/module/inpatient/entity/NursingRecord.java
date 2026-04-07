package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inpatient.enums.NursingRecordType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 护理记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "nursing_record", indexes = {
        @Index(name = "idx_admission", columnList = "admission_id"),
        @Index(name = "idx_patient", columnList = "patient_id"),
        @Index(name = "idx_record_time", columnList = "record_time")
})
public class NursingRecord extends BaseEntity {

    /**
     * 住院ID
     */
    @Column(name = "admission_id", length = 36, nullable = false)
    private String admissionId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    /**
     * 记录时间
     */
    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;

    /**
     * 记录类型
     */
    @Column(name = "record_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private NursingRecordType recordType;

    /**
     * 体温(℃)
     */
    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    /**
     * 脉搏(次/分)
     */
    @Column(name = "pulse")
    private Integer pulse;

    /**
     * 呼吸(次/分)
     */
    @Column(name = "respiration")
    private Integer respiration;

    /**
     * 收缩压
     */
    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;

    /**
     * 舒张压
     */
    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;

    /**
     * 血氧饱和度(%)
     */
    @Column(name = "spo2")
    private Integer spo2;

    /**
     * 体重(kg)
     */
    @Column(name = "weight", precision = 5, scale = 1)
    private BigDecimal weight;

    /**
     * 身高(cm)
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 入量(ml)
     */
    @Column(name = "intake", precision = 8, scale = 2)
    private BigDecimal intake;

    /**
     * 出量(ml)
     */
    @Column(name = "output", precision = 8, scale = 2)
    private BigDecimal output;

    /**
     * 尿量(ml)
     */
    @Column(name = "urine", precision = 8, scale = 2)
    private BigDecimal urine;

    /**
     * 大便情况
     */
    @Column(name = "stool", length = 20)
    private String stool;

    /**
     * 护理内容
     */
    @Column(name = "nursing_content", columnDefinition = "TEXT")
    private String nursingContent;

    /**
     * 护理措施
     */
    @Column(name = "nursing_measures", columnDefinition = "TEXT")
    private String nursingMeasures;

    /**
     * 记录护士ID
     */
    @Column(name = "nurse_id", length = 20, nullable = false)
    private String nurseId;

    /**
     * 记录护士姓名
     */
    @Column(name = "nurse_name", length = 50)
    private String nurseName;
}