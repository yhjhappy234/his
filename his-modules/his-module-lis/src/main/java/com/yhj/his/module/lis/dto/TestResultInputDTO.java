package com.yhj.his.module.lis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 检验结果录入DTO
 */
@Data
@Schema(description = "检验结果录入请求")
public class TestResultInputDTO {

    @Schema(description = "申请ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请ID不能为空")
    private String requestId;

    @Schema(description = "样本ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "样本ID不能为空")
    private String sampleId;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "项目ID不能为空")
    private String itemId;

    @Schema(description = "检测值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "检测值不能为空")
    private String testValue;

    @Schema(description = "检测人ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "检测人ID不能为空")
    private String testerId;

    @Schema(description = "检测人姓名")
    private String testerName;

    @Schema(description = "检测时间")
    private LocalDateTime testTime;

    @Schema(description = "仪器ID")
    private String instrumentId;

    @Schema(description = "仪器名称")
    private String instrumentName;

    @Schema(description = "试剂批号")
    private String reagentLot;

    @Schema(description = "申请明细ID")
    private String requestItemId;

    @Schema(description = "备注")
    private String remark;
}