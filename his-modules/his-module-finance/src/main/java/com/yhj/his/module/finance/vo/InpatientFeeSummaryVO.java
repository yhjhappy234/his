package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "住院费用汇总VO")
public class InpatientFeeSummaryVO {

    @Schema(description = "住院ID")
    private String inpatientId;

    @Schema(description = "入院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "住院号")
    private String inpatientNo;

    @Schema(description = "总费用")
    private BigDecimal totalFee;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "预交金总额")
    private BigDecimal prepaidAmount;

    @Schema(description = "总预交金")
    private BigDecimal totalDeposit;

    @Schema(description = "预交金余额")
    private BigDecimal depositBalance;

    @Schema(description = "医保报销金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "欠费金额")
    private BigDecimal arrears;

    @Schema(description = "费用分类明细")
    private List<FeeCategoryDetail> categoryDetails;

    @Data
    @Schema(description = "费用分类明细")
    public static class FeeCategoryDetail {

        @Schema(description = "分类名称")
        private String categoryName;

        @Schema(description = "分类金额")
        private BigDecimal amount;

        @Schema(description = "占比")
        private BigDecimal percentage;
    }
}
