package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 检验项目更新DTO
 */
@Data
@Schema(description = "检验项目更新请求")
public class TestItemUpdateDTO {

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "英文名称")
    private String itemNameEn;

    @Schema(description = "拼音码")
    private String pinyinCode;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "标本类型")
    private String specimenType;

    @Schema(description = "检测方法")
    private String testMethod;

    @Schema(description = "结果单位")
    private String unit;

    @Schema(description = "参考值下限")
    private BigDecimal referenceMin;

    @Schema(description = "参考值上限")
    private BigDecimal referenceMax;

    @Schema(description = "参考值文本描述")
    private String referenceText;

    @Schema(description = "危急值下限")
    private BigDecimal criticalLow;

    @Schema(description = "危急值上限")
    private BigDecimal criticalHigh;

    @Schema(description = "是否有危急值")
    private Boolean critical;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "报告时限(小时)")
    private Integer turnaroundTime;

    @Schema(description = "默认仪器ID")
    private String instrumentId;

    @Schema(description = "默认试剂ID")
    private String reagentId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}