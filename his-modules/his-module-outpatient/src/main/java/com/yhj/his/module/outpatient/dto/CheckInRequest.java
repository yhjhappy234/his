package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 签到请求DTO
 */
@Data
@Schema(description = "签到请求")
public class CheckInRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;
}