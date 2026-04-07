package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 检查项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "exam_item")
public class ExamItem extends BaseEntity {

    @Column(name = "item_code", length = 20, unique = true)
    private String itemCode;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "exam_type", length = 20, nullable = false)
    private String examType;

    @Column(name = "exam_part", length = 50)
    private String examPart;

    @Column(name = "exam_method", length = 50)
    private String examMethod;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "turnaround_time")
    private Integer turnaroundTime = 24;

    @Column(name = "equipment_type", length = 50)
    private String equipmentType;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "启用";

    @Column(name = "need_contrast")
    private Boolean needContrast = false;

    @Column(name = "need_schedule")
    private Boolean needSchedule = true;

    @Column(name = "exam_duration")
    private Integer examDuration;

    @Column(name = "preparation_requirement", columnDefinition = "TEXT")
    private String preparationRequirement;

    @Column(name = "attention", columnDefinition = "TEXT")
    private String attention;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "remark", length = 500)
    private String remark;
}