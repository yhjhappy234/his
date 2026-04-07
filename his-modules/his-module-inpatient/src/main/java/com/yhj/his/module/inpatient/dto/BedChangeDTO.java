package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 床位调换请求DTO
 */
@Data
@Schema(description = "床位调换请求")
public class BedChangeDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "新病区ID不能为空")
    @Schema(description = "新病区ID")
    private String newWardId;

    @NotBlank(message = "新床位号不能为空")
    @Schema(description = "新床位号")
    private String newBedNo;

    @Schema(description = "调换原因")
    private String reason;

    @Schema(description = "操作人ID")
    private String operatorId;
}