package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 差异处理DTO
 */
@Data
@Schema(description = "差异处理DTO")
public class CheckAdjustDTO {

    @Schema(description = "盘点单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "盘点单ID不能为空")
    private String checkId;

    @Schema(description = "调整人ID")
    private String adjusterId;

    @Schema(description = "调整人姓名")
    private String adjusterName;

    @Schema(description = "需调整的明细ID列表")
    private List<String> itemIds;

    @Schema(description = "备注")
    private String remark;
}