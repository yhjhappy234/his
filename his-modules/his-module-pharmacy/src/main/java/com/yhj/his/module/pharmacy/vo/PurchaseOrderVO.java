package com.yhj.his.module.pharmacy.vo;

import com.yhj.his.module.pharmacy.enums.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购订单VO
 */
@Data
@Schema(description = "采购订单响应")
public class PurchaseOrderVO {

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "订单日期")
    private LocalDate orderDate;

    @Schema(description = "预计到货日期")
    private LocalDate expectedDate;

    @Schema(description = "总数量")
    private BigDecimal totalQuantity;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "状态")
    private PurchaseOrderStatus status;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核意见")
    private String auditRemark;

    @Schema(description = "采购明细")
    private List<PurchaseItemVO> items;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Data
    @Schema(description = "采购明细响应")
    public static class PurchaseItemVO {

        @Schema(description = "明细ID")
        private String itemId;

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品编码")
        private String drugCode;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "规格")
        private String drugSpec;

        @Schema(description = "单位")
        private String drugUnit;

        @Schema(description = "采购数量")
        private BigDecimal quantity;

        @Schema(description = "已入库数量")
        private BigDecimal receivedQuantity;

        @Schema(description = "进价")
        private BigDecimal purchasePrice;

        @Schema(description = "金额")
        private BigDecimal amount;

        @Schema(description = "备注")
        private String remark;
    }
}