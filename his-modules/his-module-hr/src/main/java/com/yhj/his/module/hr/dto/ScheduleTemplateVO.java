package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 排班模板VO
 */
@Data
@Schema(description = "排班模板信息")
public class ScheduleTemplateVO {

    @Schema(description = "模板ID")
    private String id;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "星期几(1-7)")
    private Integer weekDay;

    @Schema(description = "班次类型")
    private String scheduleType;

    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "模板描述")
    private String description;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}