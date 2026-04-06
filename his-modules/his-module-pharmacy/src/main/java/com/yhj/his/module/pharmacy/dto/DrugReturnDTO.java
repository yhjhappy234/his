package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 退药处理DTO
 */
@Data
@Schema(description = "退药处理请求")
public class DrugReturnDTO {

    @NotBlank(message = "原发药ID不能为空")
    @Schema(description = "原发药ID", required = true)
    private String originalDispenseId;

    @NotBlank(message = "退药人ID不能为空")
    @Schema(description = "退药人ID", required = true)
    private String returnOperatorId;

    @Schema(description = "退药人姓名")
    private String returnOperatorName;

    @Schema(description = "退药原因")
    private String returnReason;

    @Schema(description = "退药明细")
    private List<ReturnDetailDTO> details;

    @Data
    @Schema(description = "退药明细")
    public static class ReturnDetailDTO {

        @Schema(description = "明细ID")
        private String detailId;

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "批号")
        private String batchNo;

        @Schema(description = "退药数量")
        private BigDecimal quantity;

        @Schema(description = "退药原因")
        private String reason;
    }
}