package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医嘱执行请求DTO
 */
@Data
@Schema(description = "医嘱执行请求")
public class OrderExecuteDTO {

    @NotBlank(message = "医嘱ID不能为空")
    @Schema(description = "医嘱ID")
    private String orderId;

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotNull(message = "执行时间不能为空")
    @Schema(description = "执行时间")
    private LocalDateTime executeTime;

    @NotBlank(message = "执行护士ID不能为空")
    @Schema(description = "执行护士ID")
    private String executeNurseId;

    @Schema(description = "执行护士姓名")
    private String executeNurseName;

    @Schema(description = "执行结果")
    private String executeResult;

    @Schema(description = "执行详情(JSON)")
    private String executeDetail;
}