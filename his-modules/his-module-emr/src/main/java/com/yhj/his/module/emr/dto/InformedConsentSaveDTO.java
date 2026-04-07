package com.yhj.his.module.emr.dto;

import com.yhj.his.module.emr.enums.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 知情同意书保存DTO
 */
@Data
@Schema(description = "知情同意书保存请求")
public class InformedConsentSaveDTO {

    @Schema(description = "同意书ID(更新时必填)")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "就诊ID")
    private String visitId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @NotNull(message = "同意书类型不能为空")
    @Schema(description = "同意书类型", required = true)
    private ConsentType consentType;

    @Schema(description = "同意书名称")
    private String consentName;

    @Schema(description = "同意书内容")
    private String consentContent;

    @Schema(description = "风险说明")
    private String riskDescription;

    @NotBlank(message = "告知医生ID不能为空")
    @Schema(description = "告知医生ID", required = true)
    private String doctorId;

    @Schema(description = "告知医生姓名")
    private String doctorName;

    @Schema(description = "关联手术ID")
    private String operationId;

    @Schema(description = "模板ID")
    private String templateId;
}