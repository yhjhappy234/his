package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 住院结算VO
 */
@Data
@Schema(description = "住院结算视图对象")
public class InpatientSettlementVO {

    @Schema(description = "结算ID")
    private String id;

    @Schema(description = "结算单号")
    private String settlementNo;

    @Schema(description = "发票号")
    private String invoiceNo;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "入院日期")
    private LocalDate admissionDate;

    @Schema(description = "出院日期")
    private LocalDate dischargeDate;

    @Schema(description = "住院天数")
    private Integer hospitalDays;

    @Schema(description = "总费用")
    private BigDecimal totalAmount;

    @Schema(description = "床位费")
    private BigDecimal bedFee;

    @Schema(description = "药品费")
    private BigDecimal drugFee;

    @Schema(description = "检查费")
    private BigDecimal examFee;

    @Schema(description = "检验费")
    private BigDecimal testFee;

    @Schema(description = "治疗费")
    private BigDecimal treatmentFee;

    @Schema(description = "材料费")
    private BigDecimal materialFee;

    @Schema(description = "护理费")
    private BigDecimal nursingFee;

    @Schema(description = "其他费")
    private BigDecimal otherFee;

    @Schema(description = "预交金总额")
    private BigDecimal totalDeposit;

    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "退还金额")
    private BigDecimal refundAmount;

    @Schema(description = "补交金额")
    private BigDecimal supplementAmount;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保类型描述")
    private String insuranceTypeDesc;

    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @Schema(description = "医保申报号")
    private String insuranceClaimNo;

    @Schema(description = "结算时间")
    private LocalDateTime settlementTime;

    @Schema(description = "结算员ID")
    private String operatorId;

    @Schema(description = "结算员姓名")
    private String operatorName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}