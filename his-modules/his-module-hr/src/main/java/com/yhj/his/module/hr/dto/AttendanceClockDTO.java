package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 考勤打卡请求DTO
 */
@Data
@Schema(description = "考勤打卡请求")
public class AttendanceClockDTO {

    @NotBlank(message = "员工ID不能为空")
    @Schema(description = "员工ID", required = true)
    private String employeeId;

    @Schema(description = "打卡日期(默认当天)")
    private LocalDate attendanceDate;

    @Schema(description = "打卡类型(签到/签退)")
    private String clockType;

    @Schema(description = "打卡时间(默认当前时间)")
    private LocalTime clockTime;

    @Schema(description = "备注")
    private String remark;
}