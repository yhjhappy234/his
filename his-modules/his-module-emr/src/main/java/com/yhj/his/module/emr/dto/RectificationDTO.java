package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 整改通知DTO
 */
@Data
@Schema(description = "整改通知请求")
public class RectificationDTO {

    @NotBlank(message = "质控结果ID不能为空")
    @Schema(description = "质控结果ID", required = true)
    private String qcResultId;

    @Schema(description = "整改备注")
    private String rectifyComment;
}