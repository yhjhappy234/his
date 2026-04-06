package com.yhj.his.module.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 门诊退费DTO
 */
@Data
@Schema(description = "门诊退费请求")
public class OutpatientRefundDTO {

    @NotBlank(message = "收费单号不能为空")
    @Schema(description = "收费单号")
    private String billingNo;

    @Schema(description = "退费项目明细ID列表(为空则全部退费)")
    private java.util.List<String> itemIds;

    @Schema(description = "退费原因")
    private String refundReason;

    @Schema(description = "退费方式")
    private String refundMethod;
}