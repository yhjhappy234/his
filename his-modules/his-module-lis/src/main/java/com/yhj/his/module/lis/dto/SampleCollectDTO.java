package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 样本采集DTO
 */
@Data
@Schema(description = "样本采集请求")
public class SampleCollectDTO {

    @Schema(description = "申请ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请ID不能为空")
    private String requestId;

    @Schema(description = "采集人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "采集人ID不能为空")
    private String collectorId;

    @Schema(description = "采集人姓名")
    private String collectorName;

    @Schema(description = "采集时间")
    private LocalDateTime collectionTime;

    @Schema(description = "采集地点")
    private String collectionLocation;

    @Schema(description = "备注")
    private String remark;
}