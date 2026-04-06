package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核DTO
 */
@Data
@Schema(description = "审核DTO")
public class AuditDTO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "记录ID不能为空")
    private String id;

    @Schema(description = "审核结果", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "审核结果不能为空")
    private String result;

    @Schema(description = "审核意见")
    private String remark;

    @Schema(description = "审核人ID")
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;
}