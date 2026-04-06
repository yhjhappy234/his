package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 确认DTO
 */
@Data
@Schema(description = "确认DTO")
public class ConfirmDTO {

    @Schema(description = "记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "记录ID不能为空")
    private String id;

    @Schema(description = "操作人ID")
    private String operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "备注")
    private String remark;
}