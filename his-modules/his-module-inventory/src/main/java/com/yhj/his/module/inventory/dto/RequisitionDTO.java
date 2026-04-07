package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 领料申请DTO
 */
@Data
@Schema(description = "领料申请DTO")
public class RequisitionDTO {

    @Schema(description = "库房ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "库房ID不能为空")
    private String warehouseId;

    @Schema(description = "申领科室ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申领科室ID不能为空")
    private String deptId;

    @Schema(description = "申领科室名称")
    private String deptName;

    @Schema(description = "申请人ID")
    private String applicantId;

    @Schema(description = "申请人姓名")
    private String applicantName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "申领明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "申领明细不能为空")
    @Valid
    private List<RequisitionItemDTO> items;
}