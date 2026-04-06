package com.yhj.his.module.lis.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.VisitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检验申请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_request", indexes = {
        @Index(name = "idx_request_no", columnList = "request_no", unique = true),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_visit_id", columnList = "visit_id"),
        @Index(name = "idx_request_time", columnList = "request_time"),
        @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "检验申请")
public class TestRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请单号")
    @Column(name = "request_no", length = 30, nullable = false, unique = true)
    private String requestNo;

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

    @Schema(description = "身份证号")
    @Column(name = "id_card_no", length = 18)
    private String idCardNo;

    @Schema(description = "就诊类型")
    @Column(name = "visit_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    @Schema(description = "就诊ID")
    @Column(name = "visit_id", length = 36)
    private String visitId;

    @Schema(description = "住院ID")
    @Column(name = "admission_id", length = 36)
    private String admissionId;

    @Schema(description = "申请科室ID")
    @Column(name = "dept_id", length = 20, nullable = false)
    private String deptId;

    @Schema(description = "科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Schema(description = "申请医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "临床诊断")
    @Column(name = "clinical_diagnosis", length = 200)
    private String clinicalDiagnosis;

    @Schema(description = "临床信息")
    @Column(name = "clinical_info", columnDefinition = "TEXT")
    private String clinicalInfo;

    @Schema(description = "申请时间")
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Schema(description = "是否急诊")
    @Column(name = "is_emergency", nullable = false)
    private Boolean emergency = false;

    @Schema(description = "急诊级别")
    @Column(name = "emergency_level", length = 20)
    private String emergencyLevel;

    @Schema(description = "样本状态")
    @Column(name = "sample_status", length = 20)
    private String sampleStatus;

    @Schema(description = "报告状态")
    @Column(name = "report_status", length = 20)
    private String reportStatus;

    @Schema(description = "总金额")
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Schema(description = "收费状态")
    @Column(name = "pay_status", length = 20)
    private String payStatus;

    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TestRequestStatus status = TestRequestStatus.REQUESTED;

    @Schema(description = "取消原因")
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    @Schema(description = "取消时间")
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Schema(description = "取消人ID")
    @Column(name = "cancel_user_id", length = 20)
    private String cancelUserId;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}