package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 加班审批请求DTO
 */
@Data
@Schema(description = "加班审批请求")
public class OvertimeApprovalDTO {

    @NotBlank(message = "加班记录ID不能为空")
    @Schema(description = "加班记录ID", required = true)
    private String overtimeId;

    @NotBlank(message = "审批结果不能为空")
    @Schema(description = "审批结果(APPROVED/REJECTED)", required = true)
    private String approveResult;

    @Schema(description = "审批意见")
    private String approveRemark;
}