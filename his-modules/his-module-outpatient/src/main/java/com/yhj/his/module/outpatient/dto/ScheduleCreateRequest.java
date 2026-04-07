package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班创建请求DTO
 */
@Data
@Schema(description = "排班创建请求")
public class ScheduleCreateRequest {

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @NotBlank(message = "医生ID不能为空")
    @Schema(description = "医生ID", required = true)
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "医生职称")
    private String doctorTitle;

    @NotNull(message = "排班日期不能为空")
    @Schema(description = "排班日期", required = true)
    private LocalDate scheduleDate;

    @NotBlank(message = "时间段不能为空")
    @Schema(description = "时间段: 上午/下午/全天", required = true)
    private String timePeriod;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @NotNull(message = "总号源数不能为空")
    @Schema(description = "总号源数", required = true)
    private Integer totalQuota;

    @Schema(description = "挂号类型: 普通/专家/特需")
    private String registrationType;

    @Schema(description = "挂号费")
    private BigDecimal registrationFee;

    @Schema(description = "诊查费")
    private BigDecimal diagnosisFee;

    @Schema(description = "诊室")
    private String clinicRoom;

    @Schema(description = "备注")
    private String remark;
}