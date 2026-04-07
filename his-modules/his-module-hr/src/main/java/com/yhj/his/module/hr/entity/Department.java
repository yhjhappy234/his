package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.hr.enums.DepartmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室信息实体（HR视角）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_department", indexes = {
    @Index(name = "idx_dept_code", columnList = "dept_code"),
    @Index(name = "idx_dept_type", columnList = "dept_type"),
    @Index(name = "idx_parent", columnList = "parent_id")
})
@Schema(description = "科室信息")
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "dept_code", length = 20, nullable = false, unique = true)
    @Schema(description = "科室编码")
    private String deptCode;

    @Column(name = "dept_name", length = 100, nullable = false)
    @Schema(description = "科室名称")
    private String deptName;

    @Column(name = "dept_alias", length = 50)
    @Schema(description = "科室别名")
    private String deptAlias;

    @Enumerated(EnumType.STRING)
    @Column(name = "dept_type", length = 20, nullable = false)
    @Schema(description = "科室类型")
    private DepartmentType deptType;

    @Column(name = "dept_category", length = 20)
    @Schema(description = "科室分类")
    private String deptCategory;

    @Column(name = "parent_id", length = 36)
    @Schema(description = "上级科室ID")
    private String parentId;

    @Column(name = "dept_level")
    @Schema(description = "科室层级")
    private Integer deptLevel;

    @Column(name = "dept_path", length = 200)
    @Schema(description = "科室路径")
    private String deptPath;

    @Column(name = "dept_leader_id", length = 36)
    @Schema(description = "负责人ID")
    private String deptLeaderId;

    @Column(name = "dept_leader_name", length = 50)
    @Schema(description = "负责人姓名")
    private String deptLeaderName;

    @Column(name = "dept_phone", length = 20)
    @Schema(description = "科室电话")
    private String deptPhone;

    @Column(name = "dept_location", length = 100)
    @Schema(description = "科室位置")
    private String deptLocation;

    @Column(name = "bed_count")
    @Schema(description = "床位数")
    private Integer bedCount;

    @Column(name = "outpatient_room", length = 20)
    @Schema(description = "门诊诊室")
    private String outpatientRoom;

    @Column(name = "sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态")
    private String status = "正常";

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}