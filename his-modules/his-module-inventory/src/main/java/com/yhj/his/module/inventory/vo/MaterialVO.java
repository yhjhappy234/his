package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 物资信息VO
 */
@Data
@Schema(description = "物资信息VO")
public class MaterialVO {

    @Schema(description = "物资ID")
    private String id;

    @Schema(description = "物资编码")
    private String materialCode;

    @Schema(description = "物资名称")
    private String materialName;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "规格")
    private String materialSpec;

    @Schema(description = "单位")
    private String materialUnit;

    @Schema(description = "生产厂家")
    private String manufacturer;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "产地")
    private String origin;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "调价日期")
    private LocalDate priceDate;

    @Schema(description = "库存下限")
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    private BigDecimal maxStock;

    @Schema(description = "安全库存")
    private BigDecimal safetyStock;

    @Schema(description = "有效期(月)")
    private Integer shelfLife;

    @Schema(description = "储存条件")
    private String storageCondition;

    @Schema(description = "是否医疗耗材")
    private Boolean isMedical;

    @Schema(description = "是否无菌")
    private Boolean isSterile;

    @Schema(description = "是否可复用")
    private Boolean isReusable;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "当前库存总量")
    private BigDecimal currentStock;
}