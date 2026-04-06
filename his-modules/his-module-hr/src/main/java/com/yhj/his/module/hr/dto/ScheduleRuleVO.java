package com.yhj.his.module.hr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 排班规则VO
 */
@Data
@Schema(description = "排班规则信息")
public class ScheduleRuleVO {

    @Schema(description = "规则ID")
    private String id;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "规则值")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}