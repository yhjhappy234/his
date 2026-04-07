package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.SpecimenType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 检验申请明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_request_item", indexes = {
        @Index(name = "idx_request_id", columnList = "request_id"),
        @Index(name = "idx_item_id", columnList = "item_id")
})
@Schema(description = "检验申请明细")
public class TestRequestItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    @Column(name = "request_id", length = 36, nullable = false)
    private String requestId;

    @Schema(description = "项目ID")
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Schema(description = "项目编码")
    @Column(name = "item_code", length = 20)
    private String itemCode;

    @Schema(description = "项目名称")
    @Column(name = "item_name", length = 100)
    private String itemName;

    @Schema(description = "标本类型")
    @Column(name = "specimen_type", length = 20)
    @Enumerated(EnumType.STRING)
    private SpecimenType specimenType;

    @Schema(description = "价格")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Schema(description = "样本ID")
    @Column(name = "sample_id", length = 36)
    private String sampleId;

    @Schema(description = "结果状态")
    @Column(name = "result_status", length = 20)
    private String resultStatus;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}