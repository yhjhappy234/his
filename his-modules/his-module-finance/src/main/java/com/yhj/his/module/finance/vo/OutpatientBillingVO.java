package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 门诊收费VO
 */
@Data
@Schema(description = "门诊收费视图对象")
public class OutpatientBillingVO {

    @Schema(description = "收费ID")
    private String id;

    @Schema(description = "收费单号")
    private String billingNo;

    @Schema(description = "发票号")
    private String invoiceNo;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "就诊序号")
    private String visitNo;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "收费日期")
    private LocalDate billingDate;

    @Schema(description = "收费时间")
    private LocalDateTime billingTime;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保类型描述")
    private String insuranceTypeDesc;

    @Schema(description = "医保卡号")
    private String insuranceCardNo;

    @Schema(description = "收费员ID")
    private String operatorId;

    @Schema(description = "收费员姓名")
    private String operatorName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "退费状态")
    private String refundStatus;

    @Schema(description = "退费状态描述")
    private String refundStatusDesc;

    @Schema(description = "退费时间")
    private LocalDateTime refundTime;

    @Schema(description = "退费原因")
    private String refundReason;

    @Schema(description = "收费明细列表")
    private List<OutpatientBillingItemVO> items;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}