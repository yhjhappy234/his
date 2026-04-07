package com.yhj.his.module.hr.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.hr.enums.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 排班模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_schedule_template", indexes = {
    @Index(name = "idx_template_dept", columnList = "dept_id"),
    @Index(name = "idx_template_name", columnList = "template_name")
})
@Schema(description = "排班模板")
public class ScheduleTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "template_name", length = 50, nullable = false)
    @Schema(description = "模板名称")
    private String templateName;

    @Column(name = "template_code", length = 20, unique = true)
    @Schema(description = "模板编码")
    private String templateCode;

    @Column(name = "dept_id", length = 36)
    @Schema(description = "科室ID(为空表示全局模板)")
    private String deptId;

    @Column(name = "dept_name", length = 100)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "week_day")
    @Schema(description = "星期几(1-7)")
    private Integer weekDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 20, nullable = false)
    @Schema(description = "班次类型")
    private ScheduleType scheduleType;

    @Column(name = "start_time")
    @Schema(description = "上班时间")
    private LocalTime startTime;

    @Column(name = "end_time")
    @Schema(description = "下班时间")
    private LocalTime endTime;

    @Column(name = "location", length = 50)
    @Schema(description = "工作地点")
    private String location;

    @Column(name = "description", length = 200)
    @Schema(description = "模板描述")
    private String description;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(启用/禁用)")
    private String status = "启用";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}