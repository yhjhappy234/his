package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 护理评估请求DTO
 */
@Data
@Schema(description = "护理评估请求")
public class NursingAssessmentDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotBlank(message = "评估类型不能为空")
    @Schema(description = "评估类型(跌倒评估/压疮评估/疼痛评估/营养评估/自理能力评估)")
    private String assessmentType;

    @NotBlank(message = "评估护士ID不能为空")
    @Schema(description = "评估护士ID")
    private String nurseId;

    @Schema(description = "评估护士姓名")
    private String nurseName;

    @Schema(description = "评估得分")
    private Integer score;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "评估结果详情(JSON)")
    private String assessmentResult;

    @Schema(description = "护理建议")
    private String nursingSuggestion;
}