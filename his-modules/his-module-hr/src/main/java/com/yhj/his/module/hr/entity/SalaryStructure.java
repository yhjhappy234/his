package com.yhj.his.module.hr.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 薪资结构实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "hr_salary_structure", indexes = {
    @Index(name = "idx_salary_struct_code", columnList = "structure_code"),
    @Index(name = "idx_salary_struct_type", columnList = "salary_type")
})
@Schema(description = "薪资结构")
public class SalaryStructure extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "structure_name", length = 50, nullable = false)
    @Schema(description = "结构名称")
    private String structureName;

    @Column(name = "structure_code", length = 20, unique = true)
    @Schema(description = "结构编码")
    private String structureCode;

    @Column(name = "salary_type", length = 20, nullable = false)
    @Schema(description = "薪资类型(基本工资/岗位工资/绩效工资/津贴/扣款等)")
    private String salaryType;

    @Column(name = "item_name", length = 50, nullable = false)
    @Schema(description = "项目名称")
    private String itemName;

    @Column(name = "item_code", length = 20)
    @Schema(description = "项目编码")
    private String itemCode;

    @Column(name = "default_amount", precision = 10, scale = 2)
    @Schema(description = "默认金额")
    private BigDecimal defaultAmount;

    @Column(name = "min_amount", precision = 10, scale = 2)
    @Schema(description = "最小金额")
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 10, scale = 2)
    @Schema(description = "最大金额")
    private BigDecimal maxAmount;

    @Column(name = "calc_formula", length = 500)
    @Schema(description = "计算公式")
    private String calcFormula;

    @Column(name = "taxable")
    @Schema(description = "是否应税")
    private Boolean taxable = true;

    @Column(name = "insuranceable")
    @Schema(description = "是否计入社保基数")
    private Boolean insuranceable = true;

    @Column(name = "sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @Column(name = "status", length = 20, nullable = false)
    @Schema(description = "状态(启用/禁用)")
    private String status = "启用";

    @Column(name = "description", length = 200)
    @Schema(description = "描述")
    private String description;

    @Column(name = "remark", length = 200)
    @Schema(description = "备注")
    private String remark;
}