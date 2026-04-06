package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统参数VO
 */
@Data
@Schema(description = "系统参数信息")
public class SystemParameterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "参数ID")
    private String id;

    @Schema(description = "参数编码")
    private String paramCode;

    @Schema(description = "参数名称")
    private String paramName;

    @Schema(description = "参数值")
    private String paramValue;

    @Schema(description = "参数类型")
    private String paramType;

    @Schema(description = "参数分组")
    private String paramGroup;

    @Schema(description = "参数描述")
    private String description;

    @Schema(description = "是否系统参数")
    private Boolean isSystem;

    @Schema(description = "是否可编辑")
    private Boolean isEditable;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}