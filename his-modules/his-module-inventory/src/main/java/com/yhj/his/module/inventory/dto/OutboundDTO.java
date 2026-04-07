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
 * 出库申请DTO
 */
@Data
@Schema(description = "出库申请DTO")
public class OutboundDTO {

    @Schema(description = "出库类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "出库类型不能为空")
    private String outboundType;

    @Schema(description = "出库库房ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "出库库房ID不能为空")
    private String warehouseId;

    @Schema(description = "目标库房ID(调拨)")
    private String targetWarehouseId;

    @Schema(description = "目标科室ID")
    private String targetDeptId;

    @Schema(description = "目标科室名称")
    private String targetDeptName;

    @Schema(description = "出库日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出库日期不能为空")
    private LocalDate outboundDate;

    @Schema(description = "申请人ID")
    private String applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "出库明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "出库明细不能为空")
    @Valid
    private List<OutboundItemDTO> items;
}