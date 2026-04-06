package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 入库登记DTO
 */
@Data
@Schema(description = "入库登记DTO")
public class InboundDTO {

    @Schema(description = "入库类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "入库类型不能为空")
    private String inboundType;

    @Schema(description = "库房ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "库房ID不能为空")
    private String warehouseId;

    @Schema(description = "供应商ID")
    private String supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "入库日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "入库日期不能为空")
    private LocalDate inboundDate;

    @Schema(description = "申请人ID")
    private String applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "入库明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "入库明细不能为空")
    @Valid
    private List<InboundItemDTO> items;
}