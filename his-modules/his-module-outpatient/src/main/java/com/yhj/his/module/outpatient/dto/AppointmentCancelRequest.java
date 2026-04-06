package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取消预约请求DTO
 */
@Data
@Schema(description = "取消预约请求")
public class AppointmentCancelRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @Schema(description = "取消原因")
    private String cancelReason;
}