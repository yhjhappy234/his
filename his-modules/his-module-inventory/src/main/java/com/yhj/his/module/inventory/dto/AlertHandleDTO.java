package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 预警处理DTO
 */
@Data
@Schema(description = "预警处理DTO")
public class AlertHandleDTO {

    @Schema(description = "预警ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "预警ID不能为空")
    private String alertId;

    @Schema(description = "处理人ID")
    private String handlerId;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理备注")
    private String handleRemark;
}