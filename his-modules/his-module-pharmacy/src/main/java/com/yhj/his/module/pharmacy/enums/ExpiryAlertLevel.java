package com.yhj.his.module.pharmacy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 效期预警级别枚举
 */
@Getter
@AllArgsConstructor
public enum ExpiryAlertLevel {

    NORMAL("正常", "绿色", "有效期>180天"),
    ATTENTION("关注", "蓝色", "有效期90-180天"),
    WARNING("预警", "黄色", "有效期30-90天"),
    URGENT("紧急", "红色", "有效期<30天");

    private final String name;
    private final String color;
    private final String description;

    /**
     * 根据剩余天数获取预警级别
     */
    public static ExpiryAlertLevel getByDaysRemaining(int daysRemaining) {
        if (daysRemaining < 30) {
            return URGENT;
        } else if (daysRemaining < 90) {
            return WARNING;
        } else if (daysRemaining < 180) {
            return ATTENTION;
        } else {
            return NORMAL;
        }
    }
}