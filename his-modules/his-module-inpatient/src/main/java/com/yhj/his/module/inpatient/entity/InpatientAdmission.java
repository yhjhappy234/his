package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inpatient.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 住院记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inpatient_admission", indexes = {
        @Index(name = "idx_patient", columnList = "patient_id"),
        @Index(name = "idx_admission_time", columnList = "admission_time"),
        @Index(name = "idx_dept_status", columnList = "dept_id, status"),
        @Index(name = "idx_bed", columnList = "ward_id, bed_no")
})
public class InpatientAdmission extends BaseEntity {

    /**
     * 住院号
     */
    @Column(name = "admission_no", length = 20, nullable = false, unique = true)
    private String admissionNo;

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
     * 身份证号
     */
    @Column(name = "id_card_no", length = 18)
    private String idCardNo;

    /**
     * 性别
     */
    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

    /**
     * 出生日期
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 住址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 入院时间
     */
    @Column(name = "admission_time", nullable = false)
    private LocalDateTime admissionTime;

    /**
     * 入院类型
     */
    @Column(name = "admission_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private AdmissionType admissionType;

    /**
     * 入院来源
     */
    @Column(name = "admission_source", length = 50)
    private String admissionSource;

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
     * 病区ID
     */
    @Column(name = "ward_id", length = 20, nullable = false)
    private String wardId;

    /**
     * 病区名称
     */
    @Column(name = "ward_name", length = 100)
    private String wardName;

    /**
     * 病房号
     */
    @Column(name = "room_no", length = 20)
    private String roomNo;

    /**
     * 床位号
     */
    @Column(name = "bed_no", length = 20)
    private String bedNo;

    /**
     * 主治医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 主治医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 责任护士ID
     */
    @Column(name = "nurse_id", length = 20)
    private String nurseId;

    /**
     * 责任护士姓名
     */
    @Column(name = "nurse_name", length = 50)
    private String nurseName;

    /**
     * 入院诊断
     */
    @Column(name = "admission_diagnosis", length = 500)
    private String admissionDiagnosis;

    /**
     * 入院诊断编码
     */
    @Column(name = "admission_diagnosis_code", length = 50)
    private String admissionDiagnosisCode;

    /**
     * 出院诊断
     */
    @Column(name = "discharge_diagnosis", length = 500)
    private String dischargeDiagnosis;

    /**
     * 出院诊断编码
     */
    @Column(name = "discharge_diagnosis_code", length = 50)
    private String dischargeDiagnosisCode;

    /**
     * 护理等级
     */
    @Column(name = "nursing_level", length = 20)
    @Enumerated(EnumType.STRING)
    private NursingLevel nursingLevel;

    /**
     * 饮食类型
     */
    @Column(name = "diet_type", length = 20)
    @Enumerated(EnumType.STRING)
    private DietType dietType;

    /**
     * 过敏信息
     */
    @Column(name = "allergy_info", columnDefinition = "TEXT")
    private String allergyInfo;

    /**
     * 医保类型
     */
    @Column(name = "insurance_type", length = 50)
    private String insuranceType;

    /**
     * 医保卡号
     */
    @Column(name = "insurance_no", length = 50)
    private String insuranceNo;

    /**
     * 预交金总额
     */
    @Column(name = "deposit", precision = 12, scale = 2)
    private BigDecimal deposit = BigDecimal.ZERO;

    /**
     * 费用总额
     */
    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    /**
     * 已结算金额
     */
    @Column(name = "settled_cost", precision = 12, scale = 2)
    private BigDecimal settledCost = BigDecimal.ZERO;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private AdmissionStatus status = AdmissionStatus.PENDING;

    /**
     * 出院时间
     */
    @Column(name = "discharge_time")
    private LocalDateTime dischargeTime;

    /**
     * 出院类型
     */
    @Column(name = "discharge_type", length = 20)
    @Enumerated(EnumType.STRING)
    private DischargeType dischargeType;

    /**
     * 联系人
     */
    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
}