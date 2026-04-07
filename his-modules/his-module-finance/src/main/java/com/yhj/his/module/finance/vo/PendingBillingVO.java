package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 待收费项目VO
 */
@Data
@Schema(description = "待收费项目视图对象")
public class PendingBillingVO {

    @Schema(description = "就诊ID")
    private String visitId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "就诊序号")
    private String visitNo;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "待收费项目列表")
    private List<PendingItem> pendingItems;

    @Schema(description = "待收费项目列表(别名)")
    private List<PendingBillingItemVO> items;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    /**
     * 待收费项目明细
     */
    @Data
    @Schema(description = "待收费项目明细")
    public static class PendingItem {

        @Schema(description = "项目ID")
        private String itemId;

        @Schema(description = "项目编码")
        private String itemCode;

        @Schema(description = "项目名称")
        private String itemName;

        @Schema(description = "项目分类")
        private String itemCategory;

        @Schema(description = "单价")
        private BigDecimal unitPrice;

        @Schema(description = "数量")
        private Integer quantity;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "医保类型")
        private String insuranceType;

        @Schema(description = "医保报销比例")
        private BigDecimal reimbursementRatio;

        @Schema(description = "医保支付金额")
        private BigDecimal insurancePayAmount;

        @Schema(description = "自付金额")
        private BigDecimal selfPayAmount;

        @Schema(description = "开单医生")
        private String doctorName;

        @Schema(description = "开单时间")
        private java.time.LocalDateTime orderTime;
    }
}