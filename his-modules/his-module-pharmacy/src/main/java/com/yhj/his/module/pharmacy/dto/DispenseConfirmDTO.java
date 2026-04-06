package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发药确认DTO
 */
@Data
@Schema(description = "发药确认请求")
public class DispenseConfirmDTO {

    @NotBlank(message = "发药ID不能为空")
    @Schema(description = "发药ID", required = true)
    private String dispenseId;

    @NotBlank(message = "发药人ID不能为空")
    @Schema(description = "发药人ID", required = true)
    private String dispenserId;

    @Schema(description = "发药人姓名")
    private String dispenserName;

    @Schema(description = "发药明细")
    private List<DetailConfirmDTO> details;

    @Data
    @Schema(description = "发药明细确认")
    public static class DetailConfirmDTO {

        @Schema(description = "明细ID")
        private String detailId;

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "批号")
        private String batchNo;

        @NotNull(message = "数量不能为空")
        @Schema(description = "发药数量", required = true)
        private BigDecimal quantity;
    }
}