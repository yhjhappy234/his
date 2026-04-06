package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 病历模板VO
 */
@Data
@Schema(description = "病历模板详情")
public class EmrTemplateVO {

    @Schema(description = "模板ID")
    private String id;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    private TemplateType templateType;

    @Schema(description = "模板分类")
    private String category;

    @Schema(description = "模板内容")
    private String templateContent;

    @Schema(description = "适用科室ID")
    private String deptId;

    @Schema(description = "适用科室名称")
    private String deptName;

    @Schema(description = "创建医生ID")
    private String creatorId;

    @Schema(description = "创建医生姓名")
    private String creatorName;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "使用次数")
    private Integer useCount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}