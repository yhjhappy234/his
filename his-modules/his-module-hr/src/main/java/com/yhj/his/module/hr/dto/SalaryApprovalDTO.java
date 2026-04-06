package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 薪资审核请求DTO
 */
@Data
@Schema(description = "薪资审核请求")
public class SalaryApprovalDTO {

    @NotBlank(message = "薪资记录ID不能为空")
    @Schema(description = "薪资记录ID", required = true)
    private String salaryId;

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果(APPROVED/REJECTED)", required = true)
    private String approveResult;

    @Schema(description = "审核意见")
    private String approveRemark;
}