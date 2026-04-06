package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 库房类型枚举
 */
@Getter
public enum WarehouseType {

    MAIN("总库", "全院物资总库"),
    DEPARTMENT("科室库", "科室二级库"),
    PHARMACY("药房库", "药品专用库"),
    OPERATING_ROOM("手术室库", "手术室专用库"),
    EMERGENCY("急诊库", "急诊专用库");

    private final String name;
    private final String description;

    WarehouseType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}