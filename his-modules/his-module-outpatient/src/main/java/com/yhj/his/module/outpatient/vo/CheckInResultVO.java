package com.yhj.his.module.outpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 签到结果VO
 */
@Data
@Schema(description = "签到结果")
public class CheckInResultVO {

    @Schema(description = "排队序号")
    private Integer queueNo;

    @Schema(description = "等候人数")
    private Integer waitCount;

    @Schema(description = "预计等待时间(分钟)")
    private Integer estimatedWaitTime;

    @Schema(description = "诊室")
    private String clinicRoom;

    @Schema(description = "医生姓名")
    private String doctorName;
}