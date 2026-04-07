package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 停止医嘱请求DTO
 */
@Data
@Schema(description = "停止医嘱请求")
public class OrderStopDTO {

    @NotBlank(message = "医嘱ID不能为空")
    @Schema(description = "医嘱ID")
    private String orderId;

    @NotBlank(message = "停止医生ID不能为空")
    @Schema(description = "停止医生ID")
    private String stopDoctorId;

    @Schema(description = "停止医生姓名")
    private String stopDoctorName;

    @Schema(description = "停止原因")
    private String stopReason;
}