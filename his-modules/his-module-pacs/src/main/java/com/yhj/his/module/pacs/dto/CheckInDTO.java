package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "检查登记DTO")
public class CheckInDTO {

    @Schema(description = "申请ID", required = true)
    @NotBlank(message = "申请ID不能为空")
    private String requestId;

    @Schema(description = "设备ID")
    private String equipmentId;

    @Schema(description = "设备名称")
    private String equipmentName;

    @Schema(description = "机房号")
    private String roomNo;

    @Schema(description = "技师ID")
    private String technicianId;

    @Schema(description = "技师姓名")
    private String technicianName;

    @Schema(description = "备注")
    private String remark;
}