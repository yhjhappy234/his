package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 病历提交DTO
 */
@Data
@Schema(description = "病历提交请求")
public class EmrSubmitDTO {

    @NotBlank(message = "病历记录ID不能为空")
    @Schema(description = "病历记录ID", required = true)
    private String recordId;

    @NotBlank(message = "记录类型不能为空")
    @Schema(description = "记录类型(门诊病历/入院记录/病程记录/出院记录/手术记录)", required = true)
    private String recordType;
}