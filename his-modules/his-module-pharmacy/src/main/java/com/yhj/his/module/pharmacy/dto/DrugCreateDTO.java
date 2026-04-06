package com.yhj.his.module.pharmacy.dto;

import com.yhj.his.module.pharmacy.enums.DrugCategory;
import com.yhj.his.module.pharmacy.enums.DrugStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 药品创建DTO
 */
@Data
@Schema(description = "药品创建请求")
public class DrugCreateDTO {

    @NotBlank(message = "药品编码不能为空")
    @Schema(description = "药品编码", required = true)
    private String drugCode;

    @NotBlank(message = "药品名称不能为空")
    @Schema(description = "药品名称", required = true)
    private String drugName;

    @Schema(description = "通用名")
    private String genericName;

    @Schema(description = "商品名")
    private String tradeName;

    @Schema(description = "拼音码")
    private String pinyinCode;

    @Schema(description = "自定义码")
    private String customCode;

    @NotNull(message = "药品分类不能为空")
    @Schema(description = "药品分类", required = true)
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

    @NotNull(message = "零售价不能为空")
    @Schema(description = "零售价", required = true)
    private BigDecimal retailPrice;

    @Schema(description = "是否处方药")
    private Boolean isPrescription = false;

    @Schema(description = "是否OTC")
    private Boolean isOtc = false;

    @Schema(description = "是否基药")
    private Boolean isEssential = false;

    @Schema(description = "是否医保")
    private Boolean isInsurance = false;

    @Schema(description = "医保编码")
    private String insuranceCode;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "储存条件")
    private String storageCondition;

    @Schema(description = "有效期(月)")
    private Integer shelfLife;

    @Schema(description = "效期预警天数")
    private Integer alertDays = 180;

    @Schema(description = "库存下限")
    private BigDecimal minStock;

    @Schema(description = "库存上限")
    private BigDecimal maxStock;

    @Schema(description = "状态")
    private DrugStatus status = DrugStatus.NORMAL;
}