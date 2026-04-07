package com.yhj.his.module.finance.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 门诊收费明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outpatient_billing_item", indexes = {
        @Index(name = "idx_billing_id", columnList = "billingId"),
        @Index(name = "idx_item_id", columnList = "itemId")
})
@Schema(description = "门诊收费明细")
public class OutpatientBillingItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "billing_id", length = 36, nullable = false)
    @Schema(description = "收费记录ID")
    private String billingId;

    @Column(name = "item_id", length = 36, nullable = false)
    @Schema(description = "收费项目ID")
    private String itemId;

    @Column(name = "item_code", length = 20)
    @Schema(description = "项目编码")
    private String itemCode;

    @Column(name = "item_name", length = 100)
    @Schema(description = "项目名称")
    private String itemName;

    @Column(name = "item_category", length = 20)
    @Schema(description = "项目分类")
    @Enumerated(EnumType.STRING)
    private PriceItem.ItemCategory itemCategory;

    @Column(name = "item_unit", length = 20)
    @Schema(description = "单位")
    private String itemUnit;

    @Column(name = "quantity", precision = 10, scale = 2)
    @Schema(description = "数量")
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 10, scale = 4)
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Column(name = "amount", precision = 10, scale = 2)
    @Schema(description = "金额")
    private BigDecimal amount;

    @Column(name = "insurance_type", length = 20)
    @Schema(description = "医保类型")
    @Enumerated(EnumType.STRING)
    private PriceItem.InsuranceType insuranceType;

    @Column(name = "insurance_amount", precision = 10, scale = 2)
    @Schema(description = "医保支付金额")
    private BigDecimal insuranceAmount;

    @Column(name = "self_pay_amount", precision = 10, scale = 2)
    @Schema(description = "自付金额")
    private BigDecimal selfPayAmount;

    @Column(name = "prescription_id", length = 36)
    @Schema(description = "处方ID")
    private String prescriptionId;

    @Column(name = "request_id", length = 36)
    @Schema(description = "申请ID(检查检验申请)")
    private String requestId;

    @Column(name = "status", length = 20)
    @Schema(description = "状态: NORMAL-正常, REFUNDED-已退费")
    @Enumerated(EnumType.STRING)
    private BillingItemStatus status = BillingItemStatus.NORMAL;

    @Column(name = "refund_status", length = 20)
    @Schema(description = "退费状态")
    @Enumerated(EnumType.STRING)
    private OutpatientBilling.RefundStatus refundStatus = OutpatientBilling.RefundStatus.NONE;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;

    /**
     * 收费明细状态枚举
     */
    public enum BillingItemStatus {
        NORMAL("正常"),
        REFUNDED("已退费");

        private final String description;

        BillingItemStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}