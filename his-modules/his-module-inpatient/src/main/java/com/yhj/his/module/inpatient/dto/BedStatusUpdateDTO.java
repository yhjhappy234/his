package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 床位状态变更请求DTO
 */
@Data
@Schema(description = "床位状态变更请求")
public class BedStatusUpdateDTO {

    @NotBlank(message = "床位ID不能为空")
    @Schema(description = "床位ID")
    private String bedId;

    @NotBlank(message = "新状态不能为空")
    @Schema(description = "新状态")
    private String newStatus;

    @Schema(description = "变更原因")
    private String reason;

    @Schema(description = "操作人ID")
    private String operatorId;
}