package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 采购订单查询DTO
 */
@Data
@Schema(description = "采购订单查询请求")
public class PurchaseQueryDTO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}