package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 采购审核DTO
 */
@Data
@Schema(description = "采购审核请求")
public class PurchaseAuditDTO {

    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true)
    private String orderId;

    @NotBlank(message = "审核人ID不能为空")
    @Schema(description = "审核人ID", required = true)
    private String auditorId;

    @Schema(description = "审核人姓名")
    private String auditorName;

    @NotBlank(message = "审核结果不能为空")
    @Schema(description = "审核结果(通过/不通过)", required = true)
    private String auditResult;

    @Schema(description = "审核意见")
    private String auditRemark;

    public boolean getApproved() {
        return "通过".equals(auditResult);
    }
}