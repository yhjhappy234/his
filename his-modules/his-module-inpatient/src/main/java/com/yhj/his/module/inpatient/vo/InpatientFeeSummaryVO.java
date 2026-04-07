package com.yhj.his.module.inpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 住院费用汇总VO
 */
@Data
@Schema(description = "住院费用汇总")
public class InpatientFeeSummaryVO {

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "住院号")
    private String admissionNo;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "总费用")
    private BigDecimal totalCost;

    @Schema(description = "床位费")
    private BigDecimal bedFee;

    @Schema(description = "药品费")
    private BigDecimal drugFee;

    @Schema(description = "检查费")
    private BigDecimal examinationFee;

    @Schema(description = "检验费")
    private BigDecimal labTestFee;

    @Schema(description = "治疗费")
    private BigDecimal treatmentFee;

    @Schema(description = "护理费")
    private BigDecimal nursingFee;

    @Schema(description = "材料费")
    private BigDecimal materialFee;

    @Schema(description = "预交金总额")
    private BigDecimal deposit;

    @Schema(description = "未结算金额")
    private BigDecimal unsettledAmount;

    @Schema(description = "已结算金额")
    private BigDecimal settledAmount;
}