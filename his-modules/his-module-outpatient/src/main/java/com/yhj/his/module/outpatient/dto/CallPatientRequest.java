package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 叫号请求DTO
 */
@Data
@Schema(description = "叫号请求")
public class CallPatientRequest {

    @NotBlank(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", required = true)
    private String registrationId;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "诊室")
    private String clinicRoom;
}