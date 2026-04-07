package com.yhj.his.module.pharmacy.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 采购订单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "purchase_order_item", indexes = {
        @Index(name = "idx_item_order_id", columnList = "order_id"),
        @Index(name = "idx_item_drug_id", columnList = "drug_id")
})
@Schema(description = "采购订单明细")
public class PurchaseOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    @Column(name = "order_id", length = 36, nullable = false)
    private String orderId;

    @Schema(description = "药品ID")
    @Column(name = "drug_id", length = 36, nullable = false)
    private String drugId;

    @Schema(description = "药品编码")
    @Column(name = "drug_code", length = 30)
    private String drugCode;

    @Schema(description = "药品名称")
    @Column(name = "drug_name", length = 100)
    private String drugName;

    @Schema(description = "规格")
    @Column(name = "drug_spec", length = 50)
    private String drugSpec;

    @Schema(description = "单位")
    @Column(name = "drug_unit", length = 20)
    private String drugUnit;

    @Schema(description = "采购数量")
    @Column(name = "quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Schema(description = "已入库数量")
    @Column(name = "received_quantity", precision = 10, scale = 2)
    private BigDecimal receivedQuantity = BigDecimal.ZERO;

    @Schema(description = "进价")
    @Column(name = "purchase_price", precision = 10, scale = 4)
    private BigDecimal purchasePrice;

    @Schema(description = "金额")
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Schema(description = "备注")
    @Column(name = "remark", length = 200)
    private String remark;
}