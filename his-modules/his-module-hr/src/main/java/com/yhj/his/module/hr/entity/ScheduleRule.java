package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 排班规则实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_schedule_rule", indexes = {
    @Index(name = "idx_rule_dept", columnList = "dept_id"),
    @Index(name = "idx_rule_type", columnList = "rule_type")
})
@Schema(description = "排班规则")
public class ScheduleRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "rule_name", length = 50, nullable = false)
    @Schema(description = "规则名称")
    private String ruleName;

    @Column(name = "rule_code", length = 20, nullable = false, unique = true)
    @Schema(description = "规则编码")
    private String ruleCode;

    @Column(name = "dept_id", length = 36)
    @Schema(description = "科室ID(为空表示全局规则)")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "rule_type", length = 20, nullable = false)
    @Schema(description = "规则类型(工时规则/休息规则/夜班规则等)")
    private String ruleType;

    @Column(name = "rule_value", length = 200, nullable = false)
    @Schema(description = "规则值")
    private String ruleValue;

    @Column(name = "rule_unit", length = 20)
    @Schema(description = "规则单位(天/小时/次)")
    private String ruleUnit;

    @Column(name = "description", length = 500)
    @Schema(description = "规则描述")
    private String description;

    @Column(name = "priority")
    @Schema(description = "优先级")
    private Integer priority;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(启用/禁用)")
    private String status = "启用";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}