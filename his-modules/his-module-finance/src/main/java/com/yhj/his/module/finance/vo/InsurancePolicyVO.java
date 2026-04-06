package com.yhj.his.module.finance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医保政策VO
 */
@Data
@Schema(description = "医保政策视图对象")
public class InsurancePolicyVO {

    @Schema(description = "政策ID")
    private String id;

    @Schema(description = "政策名称")
    private String policyName;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "医保类型描述")
    private String insuranceTypeDesc;

    @Schema(description = "起付线")
    private BigDecimal deductibleLine;

    @Schema(description = "封顶线")
    private BigDecimal capLine;

    @Schema(description = "甲类报销比例")
    private BigDecimal classARatio;

    @Schema(description = "乙类报销比例")
    private BigDecimal classBRatio;

    @Schema(description = "丙类报销比例")
    private BigDecimal classCRatio;

    @Schema(description = "门诊报销比例")
    private BigDecimal outpatientRatio;

    @Schema(description = "住院报销比例")
    private BigDecimal inpatientRatio;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}