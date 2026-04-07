package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班信息VO
 */
@Data
@Schema(description = "排班信息")
public class ScheduleVO {

    @Schema(description = "排班ID")
    private String scheduleId;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "医生ID")
    private String doctorId;

    @Schema(description = "医生姓名")
    private String doctorName;

    @Schema(description = "医生职称")
    private String doctorTitle;

    @Schema(description = "排班日期")
    private LocalDate scheduleDate;

    @Schema(description = "时间段")
    private String timePeriod;

    @Schema(description = "开始时间")
    private LocalTime startTime;

    @Schema(description = "结束时间")
    private LocalTime endTime;

    @Schema(description = "总号源数")
    private Integer totalQuota;

    @Schema(description = "已预约数")
    private Integer bookedQuota;

    @Schema(description = "剩余号源数")
    private Integer availableQuota;

    @Schema(description = "挂号类型")
    private String registrationType;

    @Schema(description = "挂号费")
    private BigDecimal registrationFee;

    @Schema(description = "诊查费")
    private BigDecimal diagnosisFee;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "停诊原因")
    private String stopReason;

    @Schema(description = "诊室")
    private String clinicRoom;
}