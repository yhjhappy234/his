package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物资信息DTO
 */
@Data
@Schema(description = "物资信息DTO")
public class MaterialDTO {

    @Schema(description = "物资编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物资编码不能为空")
    private String materialCode;

    @Schema(description = "物资名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物资名称不能为空")
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
}