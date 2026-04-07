package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 医嘱审核请求DTO
 */
@Data
@Schema(description = "医嘱审核请求")
public class OrderAuditDTO {

    @NotBlank(message = "医嘱ID不能为空")
    @Schema(description = "医嘱ID")
    private String orderId;

    @NotBlank(message = "审核护士ID不能为空")
    @Schema(description = "审核护士ID")
    private String nurseId;

    @Schema(description = "审核护士姓名")
    private String nurseName;

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果(通过/驳回)")
    private String auditResult;

    @Schema(description = "审核备注")
    private String auditRemark;
}