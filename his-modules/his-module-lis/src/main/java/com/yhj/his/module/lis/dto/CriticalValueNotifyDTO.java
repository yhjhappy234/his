package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 危急值通知DTO
 */
@Data
@Schema(description = "危急值通知请求")
public class CriticalValueNotifyDTO {

    @Schema(description = "危急值ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "危急值ID不能为空")
    private String criticalValueId;

    @Schema(description = "通知方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "通知方式不能为空")
    private String notifyMethod;

    @Schema(description = "通知人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "通知人ID不能为空")
    private String notifierId;

    @Schema(description = "通知人姓名")
    private String notifierName;

    @Schema(description = "通知时间")
    private LocalDateTime notifyTime;

    @Schema(description = "备注")
    private String remark;
}