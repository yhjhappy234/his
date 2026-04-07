package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 盘点录入DTO(批量)
 */
@Data
@Schema(description = "盘点录入DTO")
public class CheckInputDTO {

    @Schema(description = "盘点单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "盘点单ID不能为空")
    private String checkId;

    @Schema(description = "盘点明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "盘点明细不能为空")
    private List<CheckItemDTO> items;
}