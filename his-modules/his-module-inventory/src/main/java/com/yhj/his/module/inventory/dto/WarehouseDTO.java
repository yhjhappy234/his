package com.yhj.his.module.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 库房信息DTO
 */
@Data
@Schema(description = "库房信息DTO")
public class WarehouseDTO {

    @Schema(description = "库房编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "库房编码不能为空")
    private String warehouseCode;

    @Schema(description = "库房名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "库房名称不能为空")
    private String warehouseName;

    @Schema(description = "库房类型")
    private String warehouseType;

    @Schema(description = "所属科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "管理员ID")
    private String managerId;

    @Schema(description = "管理员姓名")
    private String managerName;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}