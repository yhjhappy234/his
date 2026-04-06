package com.yhj.his.module.inventory.enums;

import lombok.Getter;

/**
 * 盘点类型枚举
 */
@Getter
public enum CheckType {

    PERIODIC("定期盘点", "月度/季度/年度盘点"),
    RANDOM("随机抽查", "不定期抽查盘点"),
    FULL("全盘", "全库盘点"),
    PARTIAL("抽盘", "部分物资盘点");

    private final String name;
    private final String description;

    CheckType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}