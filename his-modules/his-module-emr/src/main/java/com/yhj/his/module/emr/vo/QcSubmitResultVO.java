package com.yhj.his.module.emr.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 质控提交结果VO
 */
@Data
@Schema(description = "质控提交结果")
public class QcSubmitResultVO {

    @Schema(description = "病历记录ID")
    private String recordId;

    @Schema(description = "质控评分")
    private Integer qcScore;

    @Schema(description = "质控等级")
    private String qcLevel;

    @Schema(description = "是否合格")
    private Boolean passed;

    @Schema(description = "缺陷列表")
    private List<DefectVO> defects;
}