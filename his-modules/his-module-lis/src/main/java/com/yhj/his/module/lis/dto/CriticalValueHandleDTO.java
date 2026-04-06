package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 危急值处理DTO
 */
@Data
@Schema(description = "危急值处理请求")
public class CriticalValueHandleDTO {

    @Schema(description = "危急值ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "危急值ID不能为空")
    private String criticalValueId;

    @Schema(description = "处理人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理人ID不能为空")
    private String handlerId;

    @Schema(description = "处理人姓名")
    private String handlerName;

    @Schema(description = "处理结果", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "备注")
    private String remark;
}