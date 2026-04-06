package com.yhj.his.module.emr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 质控等级枚举
 */
@Getter
@AllArgsConstructor
public enum QcLevel {

    LEVEL_A("甲级", 90),
    LEVEL_B("乙级", 75),
    LEVEL_C("丙级", 0);

    private final String description;
    private final int minScore;

    public static QcLevel fromScore(int score) {
        if (score >= LEVEL_A.getMinScore()) {
            return LEVEL_A;
        } else if (score >= LEVEL_B.getMinScore()) {
            return LEVEL_B;
        } else {
            return LEVEL_C;
        }
    }
}