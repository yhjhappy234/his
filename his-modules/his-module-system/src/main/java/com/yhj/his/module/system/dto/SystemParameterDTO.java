package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统参数DTO
 */
@Data
@Schema(description = "系统参数创建/更新请求")
public class SystemParameterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "参数ID(更新时必填)")
    private String id;

    @Schema(description = "参数编码(创建时必填)")
    @Size(max = 50, message = "参数编码长度不能超过50")
    private String paramCode;

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称长度不能超过100")
    @Schema(description = "参数名称", required = true)
    private String paramName;

    @Schema(description = "参数值")
    @Size(max = 500, message = "参数值长度不能超过500")
    private String paramValue;

    @Schema(description = "参数类型: 业务参数/系统参数/接口参数/安全参数/显示参数")
    private String paramType;

    @Schema(description = "参数分组")
    @Size(max = 50, message = "参数分组长度不能超过50")
    private String paramGroup;

    @Schema(description = "参数描述")
    @Size(max = 200, message = "参数描述长度不能超过200")
    private String description;

    @Schema(description = "是否系统参数")
    private Boolean isSystem;

    @Schema(description = "是否可编辑")
    private Boolean isEditable;

    @Schema(description = "排序号")
    private Integer sortOrder;
}