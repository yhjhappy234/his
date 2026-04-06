package com.yhj.his.module.pacs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "检查项目VO")
public class ExamItemVO {

    @Schema(description = "项目ID")
    private String id;

    @Schema(description = "项目编码")
    private String itemCode;

    @Schema(description = "项目名称")
    private String itemName;

    @Schema(description = "检查类型")
    private String examType;

    @Schema(description = "检查部位")
    private String examPart;

    @Schema(description = "检查方法")
    private String examMethod;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "报告时限(小时)")
    private Integer turnaroundTime;

    @Schema(description = "设备类型")
    private String equipmentType;

    @Schema(description = "是否需要造影")
    private Boolean needContrast;

    @Schema(description = "是否需要预约")
    private Boolean needSchedule;

    @Schema(description = "检查时长(分钟)")
    private Integer examDuration;

    @Schema(description = "准备要求")
    private String preparationRequirement;

    @Schema(description = "注意事项")
    private String attention;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;
}