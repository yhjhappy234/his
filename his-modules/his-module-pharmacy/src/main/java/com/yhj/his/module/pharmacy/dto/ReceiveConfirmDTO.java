package com.yhj.his.module.pharmacy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 入库验收DTO
 */
@Data
@Schema(description = "入库验收请求")
public class ReceiveConfirmDTO {

    @NotBlank(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true)
    private String orderId;

    @NotBlank(message = "药房ID不能为空")
    @Schema(description = "药房ID", required = true)
    private String pharmacyId;

    @Schema(description = "药房名称")
    private String pharmacyName;

    @NotBlank(message = "验收人ID不能为空")
    @Schema(description = "验收人ID", required = true)
    private String receiverId;

    @Schema(description = "验收人姓名")
    private String receiverName;

    @Schema(description = "验收明细")
    private List<ReceiveItemDTO> items;

    @Data
    @Schema(description = "验收明细")
    public static class ReceiveItemDTO {

        @Schema(description = "明细ID")
        private String itemId;

        @Schema(description = "药品ID")
        private String drugId;

        @NotBlank(message = "批号不能为空")
        @Schema(description = "批号", required = true)
        private String batchNo;

        @Schema(description = "生产日期")
        private LocalDate productionDate;

        @NotBlank(message = "有效期不能为空")
        @Schema(description = "有效期", required = true)
        private LocalDate expiryDate;

        @Schema(description = "入库数量")
        private BigDecimal quantity;

        @Schema(description = "进价")
        private BigDecimal purchasePrice;

        @Schema(description = "零售价")
        private BigDecimal retailPrice;

        @Schema(description = "库位")
        private String location;

        @Schema(description = "验收结果")
        private String receiveResult;

        @Schema(description = "验收备注")
        private String remark;
    }
}