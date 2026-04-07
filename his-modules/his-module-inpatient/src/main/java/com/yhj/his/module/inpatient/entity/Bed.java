package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.BedType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 床位信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bed", indexes = {
        @Index(name = "idx_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_ward_bed", columnNames = {"ward_id", "bed_no"})
})
public class Bed extends BaseEntity {

    /**
     * 床位号
     */
    @Column(name = "bed_no", length = 20, nullable = false)
    private String bedNo;

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
    @Column(name = "room_no", length = 20, nullable = false)
    private String roomNo;

    /**
     * 床位类型
     */
    @Column(name = "bed_type", length = 20)
    @Enumerated(EnumType.STRING)
    private BedType bedType;

    /**
     * 床位等级
     */
    @Column(name = "bed_level", length = 20)
    private String bedLevel;

    /**
     * 床位费/天
     */
    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private BedStatus status = BedStatus.VACANT;

    /**
     * 住院ID
     */
    @Column(name = "admission_id", length = 36)
    private String admissionId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id", length = 20)
    private String patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name", length = 50)
    private String patientName;

    /**
     * 预留时间
     */
    @Column(name = "reserved_time")
    private LocalDateTime reservedTime;

    /**
     * 预留患者ID
     */
    @Column(name = "reserved_patient_id", length = 20)
    private String reservedPatientId;

    /**
     * 设施配置(JSON)
     */
    @Column(name = "facilities", columnDefinition = "TEXT")
    private String facilities;
}