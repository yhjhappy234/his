package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 处方开立结果VO
 */
@Data
@Schema(description = "处方开立结果")
public class PrescriptionResultVO {

    @Schema(description = "处方ID")
    private String prescriptionId;

    @Schema(description = "处方号")
    private String prescriptionNo;

    @Schema(description = "处方总金额")
    private BigDecimal totalAmount;

    @Schema(description = "处方明细")
    private List<PrescriptionDetailVO> details;

    @Schema(description = "警告信息")
    private List<String> warnings;

    /**
     * 处方明细
     */
    @Data
    @Schema(description = "处方明细")
    public static class PrescriptionDetailVO {

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "药品规格")
        private String drugSpec;

        @Schema(description = "数量")
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

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "皮试要求")
        private String skinTest;
    }
}