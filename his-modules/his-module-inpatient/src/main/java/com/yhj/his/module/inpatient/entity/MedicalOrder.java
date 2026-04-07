package com.yhj.his.module.inpatient.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.inpatient.enums.OrderCategory;
import com.yhj.his.module.inpatient.enums.OrderStatus;
import com.yhj.his.module.inpatient.enums.OrderType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 医嘱信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medical_order", indexes = {
        @Index(name = "idx_order_no", columnList = "order_no", unique = true),
        @Index(name = "idx_admission", columnList = "admission_id"),
        @Index(name = "idx_patient", columnList = "patient_id"),
        @Index(name = "idx_status", columnList = "status")
})
public class MedicalOrder extends BaseEntity {

    /**
     * 医嘱编号
     */
    @Column(name = "order_no", length = 30, nullable = false, unique = true)
    private String orderNo;

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
     * 医嘱类型
     */
    @Column(name = "order_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    /**
     * 医嘱分类
     */
    @Column(name = "order_category", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderCategory orderCategory;

    /**
     * 医嘱内容
     */
    @Column(name = "order_content", columnDefinition = "TEXT", nullable = false)
    private String orderContent;

    /**
     * 医嘱详情(JSON)
     */
    @Column(name = "order_detail", columnDefinition = "TEXT")
    private String orderDetail;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 执行时间描述
     */
    @Column(name = "execute_time", length = 50)
    private String executeTime;

    /**
     * 执行频次
     */
    @Column(name = "frequency", length = 50)
    private String frequency;

    /**
     * 开立医生ID
     */
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    /**
     * 开立医生姓名
     */
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    /**
     * 医嘱时间
     */
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    /**
     * 审核护士ID
     */
    @Column(name = "nurse_id", length = 20)
    private String nurseId;

    /**
     * 审核护士姓名
     */
    @Column(name = "nurse_name", length = 50)
    private String nurseName;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    /**
     * 停止医生ID
     */
    @Column(name = "stop_doctor_id", length = 20)
    private String stopDoctorId;

    /**
     * 停止医生姓名
     */
    @Column(name = "stop_doctor_name", length = 50)
    private String stopDoctorName;

    /**
     * 停止时间
     */
    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    /**
     * 停止原因
     */
    @Column(name = "stop_reason", length = 200)
    private String stopReason;

    /**
     * 组号(成组医嘱)
     */
    @Column(name = "group_no")
    private Integer groupNo;
}