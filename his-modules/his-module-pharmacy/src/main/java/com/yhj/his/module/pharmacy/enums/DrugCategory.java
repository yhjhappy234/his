package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 药品分类枚举
 */
@Getter
@AllArgsConstructor
public enum DrugCategory {

    WESTERN("西药", "化学药品"),
    CHINESE_PATENT("中成药", "中药制剂"),
    CHINESE_HERBAL("中草药", "中药材"),
    BIOLOGICAL("生物制品", "疫苗、血液制品");

    private final String name;
    private final String description;
}