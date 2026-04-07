package com.yhj.his.module.outpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 患者信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
public class Patient extends BaseEntity {

    /**
     * 患者唯一标识
     */
    @Column(name = "patient_id", length = 20, nullable = false, unique = true)
    private String patientId;

    /**
     * 身份证号
     */
    @Column(name = "id_card_no", length = 18, unique = true)
    private String idCardNo;

    /**
     * 姓名
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 性别: 男/女/未知
     */
    @Column(name = "gender", length = 10, nullable = false)
    private String gender;

    /**
     * 出生日期
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * 联系电话
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 现住址
     */
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 紧急联系人
     */
    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    /**
     * 血型: A/B/AB/O/未知
     */
    @Column(name = "blood_type", length = 10)
    private String bloodType;

    /**
     * 过敏史
     */
    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;

    /**
     * 病史
     */
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    /**
     * 医保卡号
     */
    @Column(name = "medical_insurance_no", length = 30)
    private String medicalInsuranceNo;

    /**
     * 患者状态: 正常/注销
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status = "正常";

    /**
     * 爽约次数
     */
    @Column(name = "no_show_count", nullable = false)
    private Integer noShowCount = 0;

    /**
     * 是否黑名单
     */
    @Column(name = "is_blacklist", nullable = false)
    private Boolean isBlacklist = false;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}