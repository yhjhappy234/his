package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 绩效审核请求DTO
 */
@Data
@Schema(description = "绩效审核请求")
public class PerformanceApprovalDTO {

    @NotBlank(message = "绩效评分ID不能为空")
    @Schema(description = "绩效评分ID", required = true)
    private String evaluationId;

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果(APPROVED/REJECTED)", required = true)
    private String approveResult;

    @Schema(description = "审核意见")
    private String approveRemark;
}