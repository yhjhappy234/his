package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 排班规则创建请求DTO
 */
@Data
@Schema(description = "排班规则创建请求")
public class ScheduleRuleCreateDTO {

    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称", required = true)
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "科室ID(为空表示全局规则)")
    private String deptId;

    @NotBlank(message = "规则类型不能为空")
    @Schema(description = "规则类型", required = true)
    private String ruleType;

    @NotBlank(message = "规则值不能为空")
    @Schema(description = "规则值", required = true)
    private String ruleValue;

    @Schema(description = "规则单位")
    private String ruleUnit;

    @Schema(description = "规则描述")
    private String description;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;
}