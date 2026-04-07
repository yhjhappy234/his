package com.yhj.his.module.pharmacy.dto;

import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 药品更新DTO
 */
@Data
@Schema(description = "药品更新请求")
public class DrugUpdateDTO {

    @Schema(description = "药品名称")
    private String drugName;

    @Schema(description = "通用名")
    private String genericName;

    @Schema(description = "商品名")
    private String tradeName;

    @Schema(description = "拼音码")
    private String pinyinCode;

    @Schema(description = "自定义码")
    private String customCode;

    @Schema(description = "药品分类")
    private DrugCategory drugCategory;

    @Schema(description = "剂型")
    private String drugForm;

    @Schema(description = "规格")
    private String drugSpec;

    @Schema(description = "最小单位")
    private String drugUnit;

    @Schema(description = "包装单位")
    private String packageUnit;

    @Schema(description = "包装数量")
    private Integer packageQuantity;

    @Schema(description = "生产厂家")
    private String manufacturer;

    @Schema(description = "产地")
    private String origin;

    @Schema(description = "批准文号")
    private String approvalNo;

    @Schema(description = "进价")
    private BigDecimal purchasePrice;

    @Schema(description = "零售价")
    private BigDecimal retailPrice;

    @Schema(description = "是否处方药")
    private Boolean isPrescription;

    @Schema(description = "是否OTC")
    private Boolean isOtc;

    @Schema(description = "是否基药")
    private Boolean isEssential;

    @Schema(description = "是否医保")
    private Boolean isInsurance;

    @Schema(description = "医保编码")
    private String insuranceCode;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "储存条件")
    private String storageCondition;

    @Schema(description = "有效期(月)")
    private Integer shelfLife;

    @Schema(description = "效期预警天数")
    private Integer alertDays;

    @Schema(description = "库存下限")
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    private BigDecimal maxStock;

    @Schema(description = "状态")
    private DrugStatus status;
}