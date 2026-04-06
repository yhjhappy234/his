package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物资分类DTO
 */
@Data
@Schema(description = "物资分类DTO")
public class MaterialCategoryDTO {

    @Schema(description = "分类编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    @Schema(description = "父分类ID")
    private String parentId;

    @Schema(description = "分类层级")
    private Integer level;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态")
    @NotNull(message = "状态不能为空")
    private Integer status;
}