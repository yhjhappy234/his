package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样本核收DTO
 */
@Data
@Schema(description = "样本核收请求")
public class SampleReceiveDTO {

    @Schema(description = "样本编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "样本编号不能为空")
    private String sampleNo;

    @Schema(description = "接收人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "接收人ID不能为空")
    private String receiverId;

    @Schema(description = "接收人姓名")
    private String receiverName;

    @Schema(description = "接收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "存放位置")
    private String storageLocation;

    @Schema(description = "检验组")
    private String testGroup;

    @Schema(description = "备注")
    private String remark;
}