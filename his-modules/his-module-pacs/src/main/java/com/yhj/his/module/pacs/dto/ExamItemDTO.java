package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "检查项目DTO")
public class ExamItemDTO {

    @Schema(description = "项目ID(更新时必填)")
    private String id;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "项目名称", required = true)
    private String itemName;

    @Schema(description = "检查类型", required = true)
    private String examType;

    @Schema(description = "检查部位")
    private String examPart;

    @Schema(description = "检查方法")
    private String examMethod;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "报告时限(小时)")
    private Integer turnaroundTime = 24;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "是否需要造影")
    private Boolean needContrast = false;

    @Schema(description = "是否需要预约")
    private Boolean needSchedule = true;

    @Schema(description = "检查时长(分钟)")
    private Integer examDuration;

    @Schema(description = "准备要求")
    private String preparationRequirement;

    @Schema(description = "注意事项")
    private String attention;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status = "启用";

    @Schema(description = "备注")
    private String remark;
}