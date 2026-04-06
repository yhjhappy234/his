package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

/**
 * 排班模板创建请求DTO
 */
@Data
@Schema(description = "排班模板创建请求")
public class ScheduleTemplateCreateDTO {

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", required = true)
    private String templateName;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "科室ID(为空表示全局模板)")
    private String deptId;

    @Schema(description = "星期几(1-7)")
    private Integer weekDay;

    @NotBlank(message = "班次类型不能为空")
    @Schema(description = "班次类型", required = true)
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
}