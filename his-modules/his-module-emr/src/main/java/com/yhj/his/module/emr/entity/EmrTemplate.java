package com.yhj.his.module.emr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.emr.enums.TemplateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 病历模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "emr_template")
@Schema(description = "病历模板")
public class EmrTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模板名称")
    @Column(name = "template_name", length = 100, nullable = false)
    private String templateName;

    @Schema(description = "模板类型")
    @Column(name = "template_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    @Schema(description = "模板分类")
    @Column(name = "category", length = 50)
    private String category;

    @Schema(description = "模板内容")
    @Column(name = "template_content", columnDefinition = "TEXT")
    private String templateContent;

    @Schema(description = "适用科室ID")
    @Column(name = "dept_id", length = 20)
    private String deptId;

    @Schema(description = "适用科室名称")
    @Column(name = "dept_name", length = 100)
    private String deptName;

    @Schema(description = "创建医生ID")
    @Column(name = "creator_id", length = 20)
    private String creatorId;

    @Schema(description = "创建医生姓名")
    @Column(name = "creator_name", length = 50)
    private String creatorName;

    @Schema(description = "是否公开")
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Schema(description = "是否启用")
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Schema(description = "使用次数")
    @Column(name = "use_count")
    private Integer useCount = 0;

    @Schema(description = "备注")
    @Column(name = "remark", length = 500)
    private String remark;
}