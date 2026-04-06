package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 危急值确认DTO
 */
@Data
@Schema(description = "危急值确认请求")
public class CriticalValueConfirmDTO {

    @Schema(description = "危急值ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "危急值ID不能为空")
    private String criticalValueId;

    @Schema(description = "接收科室", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "接收科室不能为空")
    private String receiverDept;

    @Schema(description = "接收人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "接收人姓名不能为空")
    private String receiverName;

    @Schema(description = "接收电话")
    private String receiverPhone;

    @Schema(description = "接收确认时间")
    private LocalDateTime receiveTime;

    @Schema(description = "备注")
    private String remark;
}