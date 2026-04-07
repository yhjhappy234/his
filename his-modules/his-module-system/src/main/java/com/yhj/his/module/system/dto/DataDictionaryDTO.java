package com.yhj.his.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典DTO
 */
@Data
@Schema(description = "数据字典创建/更新请求")
public class DataDictionaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典ID(更新时必填)")
    private String id;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 50, message = "字典类型长度不能超过50")
    @Schema(description = "字典类型", required = true)
    private String dictType;

    @Schema(description = "字典编码(创建时必填)")
    @Size(max = 50, message = "字典编码长度不能超过50")
    private String dictCode;

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100")
    @Schema(description = "字典名称", required = true)
    private String dictName;

    @Schema(description = "字典值")
    @Size(max = 200, message = "字典值长度不能超过200")
    private String dictValue;

    @Schema(description = "父级编码")
    @Size(max = 50, message = "父级编码长度不能超过50")
    private String parentCode;

    @Schema(description = "层级")
    private Integer dictLevel;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "是否默认值")
    private Boolean isDefault;

    @Schema(description = "描述")
    @Size(max = 200, message = "描述长度不能超过200")
    private String description;
}