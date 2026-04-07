package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 加班申请创建请求DTO
 */
@Data
@Schema(description = "加班申请创建请求")
public class OvertimeCreateDTO {

    @NotBlank(message = "员工ID不能为空")
    @Schema(description = "员工ID", required = true)
    private String employeeId;

    @NotNull(message = "加班日期不能为空")
    @Schema(description = "加班日期", required = true)
    private LocalDate overtimeDate;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", required = true)
    private LocalDateTime endTime;

    @Schema(description = "加班时长(小时)")
    private BigDecimal overtimeHours;

    @Schema(description = "加班类型")
    private String overtimeType;

    @Schema(description = "加班原因")
    private String overtimeReason;

    @Schema(description = "补偿类型")
    private String compensateType;

    @Schema(description = "备注")
    private String remark;
}