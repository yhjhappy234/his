package com.yhj.his.module.pacs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "预约安排DTO")
public class ScheduleDTO {

    @Schema(description = "申请ID", required = true)
    @NotBlank(message = "申请ID不能为空")
    private String requestId;

    @Schema(description = "预约时间", required = true)
    private LocalDateTime scheduleTime;

    @Schema(description = "机房号")
    private String roomNo;

    @Schema(description = "备注")
    private String remark;
}