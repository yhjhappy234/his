package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 处方审核DTO
 */
@Data
@Schema(description = "处方审核请求")
public class PrescriptionAuditDTO {

    @NotBlank(message = "发药ID不能为空")
    @Schema(description = "发药ID", required = true)
    private String dispenseId;

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

    @Schema(description = "明细审核列表")
    private List<DetailAuditDTO> details;

    public boolean getApproved() {
        return "通过".equals(auditResult);
    }

    @Data
    @Schema(description = "明细审核")
    public static class DetailAuditDTO {

        @Schema(description = "明细ID")
        private String detailId;

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "审核结果")
        private String auditResult;

        @Schema(description = "审核说明")
        private String auditRemark;
    }
}