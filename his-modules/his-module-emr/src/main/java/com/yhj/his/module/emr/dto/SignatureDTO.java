package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 电子签名DTO
 */
@Data
@Schema(description = "电子签名请求")
public class SignatureDTO {

    @NotBlank(message = "同意书ID不能为空")
    @Schema(description = "同意书ID", required = true)
    private String consentId;

    @Schema(description = "医生签名(图片URL)")
    private String doctorSignature;

    @Schema(description = "患者签名(图片URL)")
    private String patientSignature;

    @Schema(description = "代理人姓名")
    private String agentName;

    @Schema(description = "代理人与患者关系")
    private String agentRelation;

    @Schema(description = "代理人身份证号")
    private String agentIdCard;

    @Schema(description = "代理人签名(图片URL)")
    private String agentSignature;
}