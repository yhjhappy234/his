package com.yhj.his.module.outpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 预约挂号请求DTO
 */
@Data
@Schema(description = "预约挂号请求")
public class AppointmentCreateRequest {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @NotBlank(message = "科室ID不能为空")
    @Schema(description = "科室ID", required = true)
    private String deptId;

    @Schema(description = "医生ID(可选,不指定则随机分配)")
    private String doctorId;

    @Schema(description = "排班ID(可选)")
    private String scheduleId;

    @NotNull(message = "就诊日期不能为空")
    @Schema(description = "就诊日期", required = true)
    private LocalDate scheduleDate;

    @Schema(description = "时间段: 上午/下午")
    private String timePeriod;

    @Schema(description = "挂号类型: 普通/专家/特需")
    private String registrationType;

    @Schema(description = "来源: 现场/微信/APP/电话")
    private String source;

    @Schema(description = "备注")
    private String remark;
}