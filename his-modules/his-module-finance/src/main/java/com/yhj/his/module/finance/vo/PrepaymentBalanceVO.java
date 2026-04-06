package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "预交金余额VO")
public class PrepaymentBalanceVO {

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "住院号")
    private String inpatientNo;

    @Schema(description = "入院ID")
    private String admissionId;

    @Schema(description = "预交金余额")
    private BigDecimal balance;

    @Schema(description = "当前余额")
    private BigDecimal currentBalance;

    @Schema(description = "总预交金")
    private BigDecimal totalDeposit;

    @Schema(description = "已使用金额")
    private BigDecimal usedAmount;

    @Schema(description = "预计费用")
    private BigDecimal estimatedCost;

    @Schema(description = "总费用")
    private BigDecimal totalFee;

    @Schema(description = "欠费金额")
    private BigDecimal arrears;

    @Schema(description = "押金预警线")
    private BigDecimal warningLine;

    @Schema(description = "预警阈值")
    private BigDecimal warningThreshold;

    @Schema(description = "余额状态")
    private String balanceStatus;
}