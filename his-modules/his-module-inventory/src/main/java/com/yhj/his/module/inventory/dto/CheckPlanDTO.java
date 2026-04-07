package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 盘点计划DTO
 */
@Data
@Schema(description = "盘点计划DTO")
public class CheckPlanDTO {

    @Schema(description = "盘点类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "盘点类型不能为空")
    private String checkType;

    @Schema(description = "库房ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "库房ID不能为空")
    private String warehouseId;

    @Schema(description = "盘点日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate checkDate;

    @Schema(description = "盘点人ID")
    private String checkerId;

    @Schema(description = "盘点人姓名")
    private String checkerName;

    @Schema(description = "备注")
    private String remark;
}