package com.yhj.his.module.emr.dto;

import com.yhj.his.module.emr.enums.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 病历模板保存DTO
 */
@Data
@Schema(description = "病历模板保存请求")
public class EmrTemplateSaveDTO {

    @Schema(description = "模板ID(更新时必填)")
    private String id;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", required = true)
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

    @Schema(description = "备注")
    private String remark;
}