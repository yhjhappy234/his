package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 发药查询DTO
 */
@Data
@Schema(description = "发药查询请求")
public class DispenseQueryDTO {

    @Schema(description = "药房ID")
    private String pharmacyId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "处方号")
    private String prescriptionNo;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "发药状态")
    private String dispenseStatus;

    @Schema(description = "开始日期")
    private LocalDateTime startDate;

    @Schema(description = "结束日期")
    private LocalDateTime endDate;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}