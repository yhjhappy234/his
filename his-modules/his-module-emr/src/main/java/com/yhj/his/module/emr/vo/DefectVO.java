package com.yhj.his.module.emr.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 缺陷详情VO
 */
@Data
@Schema(description = "缺陷详情")
public class DefectVO {

    @Schema(description = "缺陷编码")
    private String defectCode;

    @Schema(description = "缺陷名称")
    private String defectName;

    @Schema(description = "缺陷类型")
    private String defectType;

    @Schema(description = "扣分")
    private Integer score;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否必填")
    private Boolean isRequired;
}