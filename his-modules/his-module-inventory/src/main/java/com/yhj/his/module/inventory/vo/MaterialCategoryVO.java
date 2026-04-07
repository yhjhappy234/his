package com.yhj.his.module.inventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物资分类VO
 */
@Data
@Schema(description = "物资分类VO")
public class MaterialCategoryVO {

    @Schema(description = "分类ID")
    private String id;

    @Schema(description = "分类编码")
    private String categoryCode;

    @Schema(description = "分类名称")
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
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子分类")
    private List<MaterialCategoryVO> children;
}