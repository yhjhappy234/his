package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 请假审批请求DTO
 */
@Data
@Schema(description = "请假审批请求")
public class LeaveApprovalDTO {

    @NotBlank(message = "请假申请ID不能为空")
    @Schema(description = "请假申请ID", required = true)
    private String leaveId;

    @NotBlank(message = "审批结果不能为空")
    @Schema(description = "审批结果(APPROVED/REJECTED)", required = true)
    private String approveResult;

    @Schema(description = "审批意见")
    private String approveRemark;
}