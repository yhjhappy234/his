package com.yhj.his.module.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 叫号播报请求
 */
@Data
@Schema(description = "叫号播报请求")
public class CallNumberRequest {

    @Schema(description = "排队号", required = true, example = "15")
    @NotBlank(message = "排队号不能为空")
    private String queueNo;

    @Schema(description = "患者姓名", required = true)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "诊室号", required = true)
    @NotBlank(message = "诊室号不能为空")
    private String roomNo;

    @Schema(description = "诊室名称")
    private String roomName;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "叫号类型: STANDARD-标准叫号, RETRY-过号重呼, RECHECK-复诊叫号", example = "STANDARD")
    private String callType = "STANDARD";

    @Schema(description = "目标设备分组编码列表")
    private String[] targetGroups;

    @Schema(description = "目标设备ID列表")
    private String[] targetDevices;

    @Schema(description = "是否脱敏显示患者姓名")
    private Boolean desensitize = true;

    @Schema(description = "关联业务ID(如挂号ID)")
    private String bizId;
}