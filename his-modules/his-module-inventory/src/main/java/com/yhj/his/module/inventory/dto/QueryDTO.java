package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 查询参数DTO
 */
@Data
@Schema(description = "查询参数DTO")
public class QueryDTO {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "库房ID")
    private String warehouseId;

    @Schema(description = "物资ID")
    private String materialId;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "预警类型")
    private String alertType;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}