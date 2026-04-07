package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.enums.VisitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发药记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dispense_record", indexes = {
        @Index(name = "idx_dispense_no", columnList = "dispense_no", unique = true),
        @Index(name = "idx_prescription_id", columnList = "prescription_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_dispense_time", columnList = "dispense_time")
})
@Schema(description = "发药记录")
public class DispenseRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "发药单号")
    @Column(name = "dispense_no", length = 30, nullable = false, unique = true)
    private String dispenseNo;

    @Schema(description = "处方ID")
    @Column(name = "prescription_id", length = 36)
    private String prescriptionId;

    @Schema(description = "处方号")
    @Column(name = "prescription_no", length = 30)
    private String prescriptionNo;

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

    @Schema(description = "就诊类型")
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", length = 20, nullable = false)
    private VisitType visitType;

    @Schema(description = "住院ID")
    @Column(name = "admission_id", length = 36)
    private String admissionId;

    @Schema(description = "科室ID")
    @Column(name = "dept_id", length = 20)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Schema(description = "医生ID")
    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    @Schema(description = "医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "药房ID")
    @Column(name = "pharmacy_id", length = 20, nullable = false)
    private String pharmacyId;

    @Schema(description = "药房名称")
    @Column(name = "pharmacy_name", length = 100)
    private String pharmacyName;

    @Schema(description = "总金额")
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Schema(description = "审核状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", length = 20)
    private AuditStatus auditStatus = AuditStatus.PENDING;

    @Schema(description = "审核人ID")
    @Column(name = "auditor_id", length = 20)
    private String auditorId;

    @Schema(description = "审核人姓名")
    @Column(name = "auditor_name", length = 50)
    private String auditorName;

    @Schema(description = "审核时间")
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Schema(description = "审核意见")
    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    @Schema(description = "发药状态")
    @Enumerated(EnumType.STRING)
    @Column(name = "dispense_status", length = 20)
    private DispenseStatus dispenseStatus = DispenseStatus.PENDING;

    @Schema(description = "发药人ID")
    @Column(name = "dispenser_id", length = 20)
    private String dispenserId;

    @Schema(description = "发药人姓名")
    @Column(name = "dispenser_name", length = 50)
    private String dispenserName;

    @Schema(description = "发药时间")
    @Column(name = "dispense_time")
    private LocalDateTime dispenseTime;

    @Schema(description = "患者确认接收")
    @Column(name = "receive_confirm", nullable = false)
    private Boolean receiveConfirm = false;

    @Schema(description = "接收确认时间")
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;
}