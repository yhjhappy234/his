package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 入院评估请求DTO
 */
@Data
@Schema(description = "入院评估请求")
public class AdmissionAssessmentDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "评估护士ID不能为空")
    @Schema(description = "评估护士ID")
    private String nurseId;

    @Schema(description = "评估护士姓名")
    private String nurseName;

    @Schema(description = "跌倒风险评估")
    private AssessmentDetail fallRiskAssessment;

    @Schema(description = "压疮风险评估")
    private AssessmentDetail pressureUlcerRiskAssessment;

    @Schema(description = "疼痛评估")
    private AssessmentDetail painAssessment;

    @Schema(description = "营养评估")
    private AssessmentDetail nutritionAssessment;

    @Schema(description = "自理能力评估")
    private AssessmentDetail selfCareAssessment;

    @Schema(description = "评估备注")
    private String remarks;

    /**
     * 评估详情
     */
    @Data
    @Schema(description = "评估详情")
    public static class AssessmentDetail {
        @Schema(description = "评估得分")
        private Integer score;

        @Schema(description = "风险等级")
        private String riskLevel;

        @Schema(description = "评估结果详情(JSON)")
        private String detail;
    }
}