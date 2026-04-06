package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 药品查询DTO
 */
@Data
@Schema(description = "药品查询请求")
public class DrugQueryDTO {

    @Schema(description = "关键词(药品名称/编码/拼音码)")
    private String keyword;

    @Schema(description = "药品分类")
    private String drugCategory;

    @Schema(description = "剂型")
    private String drugForm;

    @Schema(description = "是否处方药")
    private Boolean isPrescription;

    @Schema(description = "是否OTC")
    private Boolean isOtc;

    @Schema(description = "是否医保")
    private Boolean isInsurance;

    @Schema(description = "生产厂家")
    private String manufacturer;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}