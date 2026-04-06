package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收费项目VO
 */
@Data
@Schema(description = "收费项目视图对象")
public class PriceItemVO {

    @Schema(description = "项目ID")
    private String id;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "项目分类")
    private String itemCategory;

    @Schema(description = "项目分类描述")
    private String itemCategoryDesc;

    @Schema(description = "单位")
    private String itemUnit;

    @Schema(description = "规格")
    private String itemSpec;

    @Schema(description = "标准价格")
    private BigDecimal standardPrice;

    @Schema(description = "零售价格")
    private BigDecimal retailPrice;

    @Schema(description = "批发价格")
    private BigDecimal wholesalePrice;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保类型描述")
    private String insuranceTypeDesc;

    @Schema(description = "医保编码")
    private String insuranceCode;

    @Schema(description = "医保价格")
    private BigDecimal insurancePrice;

    @Schema(description = "报销比例")
    private BigDecimal reimbursementRatio;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期")
    private LocalDate expireDate;

    @Schema(description = "版本号")
    private String versionNo;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}