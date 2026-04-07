package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样本拒收DTO
 */
@Data
@Schema(description = "样本拒收请求")
public class SampleRejectDTO {

    @Schema(description = "样本编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "样本编号不能为空")
    private String sampleNo;

    @Schema(description = "拒收原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "拒收原因不能为空")
    private String rejectReason;

    @Schema(description = "拒收人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "拒收人ID不能为空")
    private String rejectUserId;

    @Schema(description = "拒收时间")
    private LocalDateTime rejectTime;

    @Schema(description = "备注")
    private String remark;
}