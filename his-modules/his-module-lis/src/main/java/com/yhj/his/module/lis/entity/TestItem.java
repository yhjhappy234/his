package com.yhj.his.module.lis.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.SpecimenType;
import com.yhj.his.module.lis.enums.TestItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 检验项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_item", indexes = {
        @Index(name = "idx_item_code", columnList = "item_code", unique = true),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_status", columnList = "status")
})
@Schema(description = "检验项目")
public class TestItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "项目编码")
    @Column(name = "item_code", length = 20, nullable = false, unique = true)
    private String itemCode;

    @Schema(description = "项目名称")
    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Schema(description = "英文名称")
    @Column(name = "item_name_en", length = 100)
    private String itemNameEn;

    @Schema(description = "拼音码")
    @Column(name = "pinyin_code", length = 50)
    private String pinyinCode;

    @Schema(description = "分类")
    @Column(name = "category", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TestItemCategory category;

    @Schema(description = "标本类型")
    @Column(name = "specimen_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private SpecimenType specimenType;

    @Schema(description = "检测方法")
    @Column(name = "test_method", length = 50)
    private String testMethod;

    @Schema(description = "结果单位")
    @Column(name = "unit", length = 20)
    private String unit;

    @Schema(description = "参考值下限")
    @Column(name = "reference_min", precision = 10, scale = 4)
    private BigDecimal referenceMin;

    @Schema(description = "参考值上限")
    @Column(name = "reference_max", precision = 10, scale = 4)
    private BigDecimal referenceMax;

    @Schema(description = "参考值文本描述")
    @Column(name = "reference_text", length = 100)
    private String referenceText;

    @Schema(description = "危急值下限")
    @Column(name = "critical_low", precision = 10, scale = 4)
    private BigDecimal criticalLow;

    @Schema(description = "危急值上限")
    @Column(name = "critical_high", precision = 10, scale = 4)
    private BigDecimal criticalHigh;

    @Schema(description = "是否有危急值")
    @Column(name = "is_critical", nullable = false)
    private Boolean critical = false;

    @Schema(description = "价格")
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Schema(description = "报告时限(小时)")
    @Column(name = "turnaround_time")
    private Integer turnaroundTime;

    @Schema(description = "默认仪器ID")
    @Column(name = "instrument_id", length = 20)
    private String instrumentId;

    @Schema(description = "默认试剂ID")
    @Column(name = "reagent_id", length = 20)
    private String reagentId;

    @Schema(description = "状态")
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TestItemStatus status = TestItemStatus.NORMAL;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}