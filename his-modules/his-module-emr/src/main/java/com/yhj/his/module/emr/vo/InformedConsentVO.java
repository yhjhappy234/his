package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知情同意书VO
 */
@Data
@Schema(description = "知情同意书详情")
public class InformedConsentVO {

    @Schema(description = "同意书ID")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "同意书类型")
    private ConsentType consentType;

    @Schema(description = "同意书名称")
    private String consentName;

    @Schema(description = "同意书内容")
    private String consentContent;

    @Schema(description = "风险说明")
    private String riskDescription;

    @Schema(description = "告知医生ID")
    private String doctorId;

    @Schema(description = "告知医生姓名")
    private String doctorName;

    @Schema(description = "医生签名")
    private String doctorSignature;

    @Schema(description = "医生签署时间")
    private LocalDateTime signTime;

    @Schema(description = "患者签名")
    private String patientSignature;

    @Schema(description = "患者签署时间")
    private LocalDateTime patientSignTime;

    @Schema(description = "代理人姓名")
    private String agentName;

    @Schema(description = "代理人与患者关系")
    private String agentRelation;

    @Schema(description = "代理人签名")
    private String agentSignature;

    @Schema(description = "代理人签署时间")
    private LocalDateTime agentSignTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "关联手术ID")
    private String operationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}