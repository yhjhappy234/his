package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预交金VO
 */
@Data
@Schema(description = "预交金视图对象")
public class PrepaymentVO {

    @Schema(description = "预交金ID")
    private String id;

    @Schema(description = "预交金单号")
    private String prepaymentNo;

    @Schema(description = "收据号")
    private String receiptNo;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "类型")
    private String depositType;

    @Schema(description = "类型描述")
    private String depositTypeDesc;

    @Schema(description = "金额")
    private BigDecimal depositAmount;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付方式描述")
    private String paymentMethodDesc;

    @Schema(description = "操作前余额")
    private BigDecimal balanceBefore;

    @Schema(description = "操作后余额")
    private BigDecimal balanceAfter;

    @Schema(description = "操作员ID")
    private String operatorId;

    @Schema(description = "操作员姓名")
    private String operatorName;

    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}