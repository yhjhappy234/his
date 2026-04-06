package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班创建请求DTO
 */
@Data
@Schema(description = "排班创建请求")
public class ScheduleCreateDTO {

    @NotBlank(message = "员工ID不能为空")
    @Schema(description = "员工ID", required = true)
    private String employeeId;

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private String deptId;

    @NotNull(message = "排班日期不能为空")
    @Schema(description = "排班日期", required = true)
    private LocalDate scheduleDate;

    @NotBlank(message = "班次类型不能为空")
    @Schema(description = "班次类型", required = true)
    private String scheduleType;

    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Schema(description = "工作地点")
    private String location;

    @Schema(description = "排班模板ID")
    private String templateId;

    @Schema(description = "备注")
    private String remark;
}