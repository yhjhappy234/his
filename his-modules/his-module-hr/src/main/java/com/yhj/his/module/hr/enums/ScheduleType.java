package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 班次类型枚举
 */
@Getter
@AllArgsConstructor
public enum ScheduleType {

    DAY_SHIFT("白班", "08:00", "16:00"),
    NIGHT_SHIFT("夜班", "16:00", "24:00"),
    LATE_NIGHT("晚班", "00:00", "08:00"),
    FULL_DAY("全天班", "08:00", "24:00"),
    REST("休息", null, null),
    HOLIDAY("节假日", null, null);

    private final String name;
    private final String startTime;
    private final String endTime;
}