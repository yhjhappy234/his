package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 处方开立请求DTO
 */
@Data
@Schema(description = "处方开立请求")
public class PrescriptionCreateRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @NotBlank(message = "处方类型不能为空")
    @Schema(description = "处方类型: 西药/中药", required = true)
    private String prescriptionType;

    @Schema(description = "诊断编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @NotEmpty(message = "处方明细不能为空")
    @Schema(description = "处方明细列表", required = true)
    private List<PrescriptionDetailRequest> details;

    @Schema(description = "备注")
    private String remark;

    /**
     * 处方明细请求
     */
    @Data
    @Schema(description = "处方明细")
    public static class PrescriptionDetailRequest {

        @NotBlank(message = "药品ID不能为空")
        @Schema(description = "药品ID", required = true)
        private String drugId;

        @NotBlank(message = "药品名称不能为空")
        @Schema(description = "药品名称", required = true)
        private String drugName;

        @Schema(description = "药品规格")
        private String drugSpec;

        @Schema(description = "单位")
        private String drugUnit;

        @Schema(description = "剂型")
        private String drugForm;

        @NotNull(message = "数量不能为空")
        @Schema(description = "数量", required = true)
        private BigDecimal quantity;

        @Schema(description = "用法")
        private String dosage;

        @Schema(description = "使用频率")
        private String frequency;

        @Schema(description = "用药天数")
        private Integer days;

        @Schema(description = "给药途径")
        private String route;

        @Schema(description = "单价")
        private BigDecimal unitPrice;

        @Schema(description = "组号(用于输液分组)")
        private Integer groupNo;

        @Schema(description = "皮试要求")
        private String skinTest;

        @Schema(description = "是否基药")
        private Boolean isEssential;

        @Schema(description = "是否医保")
        private Boolean isMedicalInsurance;

        @Schema(description = "备注")
        private String remark;
    }
}