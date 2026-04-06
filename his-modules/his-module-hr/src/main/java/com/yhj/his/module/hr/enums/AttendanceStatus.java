package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 考勤状态枚举
 */
@Getter
@AllArgsConstructor
public enum AttendanceStatus {

    NORMAL("正常", "按时打卡"),
    LATE("迟到", "迟到打卡"),
    EARLY_LEAVE("早退", "早退打卡"),
    ABSENT("旷工", "未打卡"),
    LEAVE("请假", "请假状态"),
    TIME_OFF("调休", "调休状态"),
    OVERTIME("加班", "加班状态");

    private final String name;
    private final String description;
}