package com.yhj.his.module.emr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.emr.enums.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 知情同意书实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "informed_consent", indexes = {
    @Index(name = "idx_consent_admission_id", columnList = "admission_id"),
    @Index(name = "idx_consent_patient_id", columnList = "patient_id"),
    @Index(name = "idx_consent_type", columnList = "consent_type")
})
@Schema(description = "知情同意书")
public class InformedConsent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "住院ID")
    @Column(name = "admission_id", length = 36)
    private String admissionId;

    @Schema(description = "就诊ID")
    @Column(name = "visit_id", length = 36)
    private String visitId;

    @Schema(description = "患者ID")
    @Column(name = "patient_id", length = 20, nullable = false)
    private String patientId;

    @Schema(description = "患者姓名")
    @Column(name = "patient_name", length = 50)
    private String patientName;

    @Schema(description = "同意书类型")
    @Column(name = "consent_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ConsentType consentType;

    @Schema(description = "同意书名称")
    @Column(name = "consent_name", length = 100)
    private String consentName;

    @Schema(description = "同意书内容")
    @Column(name = "consent_content", columnDefinition = "TEXT")
    private String consentContent;

    @Schema(description = "风险说明")
    @Column(name = "risk_description", columnDefinition = "TEXT")
    private String riskDescription;

    // 医生信息
    @Schema(description = "告知医生ID")
    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Schema(description = "告知医生姓名")
    @Column(name = "doctor_name", length = 50)
    private String doctorName;

    @Schema(description = "医生签名(图片URL)")
    @Column(name = "doctor_signature", length = 200)
    private String doctorSignature;

    @Schema(description = "医生签署时间")
    @Column(name = "sign_time")
    private LocalDateTime signTime;

    // 患者/代理人签名
    @Schema(description = "患者签名(图片URL)")
    @Column(name = "patient_signature", length = 200)
    private String patientSignature;

    @Schema(description = "患者签署时间")
    @Column(name = "patient_sign_time")
    private LocalDateTime patientSignTime;

    @Schema(description = "代理人姓名")
    @Column(name = "agent_name", length = 50)
    private String agentName;

    @Schema(description = "代理人与患者关系")
    @Column(name = "agent_relation", length = 50)
    private String agentRelation;

    @Schema(description = "代理人身份证号")
    @Column(name = "agent_id_card", length = 20)
    private String agentIdCard;

    @Schema(description = "代理人签名(图片URL)")
    @Column(name = "agent_signature", length = 200)
    private String agentSignature;

    @Schema(description = "代理人签署时间")
    @Column(name = "agent_sign_time")
    private LocalDateTime agentSignTime;

    // 状态
    @Schema(description = "状态(待签署/已签署/已拒绝)")
    @Column(name = "status", length = 20, nullable = false)
    private String status = "待签署";

    @Schema(description = "拒绝原因")
    @Column(name = "refuse_reason", length = 500)
    private String refuseReason;

    @Schema(description = "关联手术ID")
    @Column(name = "operation_id", length = 36)
    private String operationId;

    @Schema(description = "模板ID")
    @Column(name = "template_id", length = 36)
    private String templateId;
}