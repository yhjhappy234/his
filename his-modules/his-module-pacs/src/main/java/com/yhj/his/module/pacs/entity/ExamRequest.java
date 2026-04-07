package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检查申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_request", indexes = {
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_request_time", columnList = "request_time"),
        @Index(name = "idx_status", columnList = "status")
})
public class ExamRequest extends BaseEntity {

    @Column(name = "request_no", length = 30, nullable = false, unique = true)
    private String requestNo;

    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "id_card_no", length = 18)
    private String idCardNo;

    @Column(name = "visit_type", length = 20, nullable = false)
    private String visitType;

    @Column(name = "visit_id", length = 36)
    private String visitId;

    @Column(name = "admission_id", length = 36)
    private String admissionId;

    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Column(name = "doctor_id", length = 20)
    private String doctorId;

    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Column(name = "clinical_diagnosis", length = 200)
    private String clinicalDiagnosis;

    @Column(name = "clinical_info", columnDefinition = "TEXT")
    private String clinicalInfo;

    @Column(name = "exam_purpose", length = 100)
    private String examPurpose;

    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Column(name = "item_code", length = 20)
    private String itemCode;

    @Column(name = "item_name", length = 100)
    private String itemName;

    @Column(name = "exam_type", length = 20)
    private String examType;

    @Column(name = "exam_part", length = 50)
    private String examPart;

    @Column(name = "exam_method", length = 50)
    private String examMethod;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Column(name = "emergency_level", length = 20)
    private String emergencyLevel;

    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Column(name = "exam_time")
    private LocalDateTime examTime;

    @Column(name = "report_time")
    private LocalDateTime reportTime;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "待预约";

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "pay_status", length = 20)
    private String payStatus;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
}