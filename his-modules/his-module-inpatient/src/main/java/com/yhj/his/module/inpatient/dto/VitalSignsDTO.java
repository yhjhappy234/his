package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生命体征录入请求DTO
 */
@Data
@Schema(description = "生命体征录入请求")
public class VitalSignsDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotNull(message = "记录时间不能为空")
    @Schema(description = "记录时间")
    private LocalDateTime recordTime;

    @Schema(description = "体温(℃)")
    private BigDecimal temperature;

    @Schema(description = "脉搏(次/分)")
    private Integer pulse;

    @Schema(description = "呼吸(次/分)")
    private Integer respiration;

    @Schema(description = "收缩压")
    private Integer bloodPressureSystolic;

    @Schema(description = "舒张压")
    private Integer bloodPressureDiastolic;

    @Schema(description = "血氧饱和度(%)")
    private Integer spo2;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "身高(cm)")
    private Integer height;

    @NotBlank(message = "记录护士ID不能为空")
    @Schema(description = "记录护士ID")
    private String nurseId;

    @Schema(description = "记录护士姓名")
    private String nurseName;
}