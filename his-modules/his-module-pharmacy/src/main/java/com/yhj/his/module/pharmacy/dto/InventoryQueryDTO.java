package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存查询DTO
 */
@Data
@Schema(description = "库存查询请求")
public class InventoryQueryDTO {

    @Schema(description = "药品ID")
    private String drugId;

    @Schema(description = "关键词(药品名称/编码)")
    private String keyword;

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "批号")
    private String batchNo;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}