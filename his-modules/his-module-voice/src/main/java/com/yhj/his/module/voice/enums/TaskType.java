package com.yhj.his.module.voice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 语音任务类型枚举
 */
@Getter
@AllArgsConstructor
public enum TaskType {

    CALL_NUMBER("CALL_NUMBER", "叫号"),
    REPORT_NOTICE("REPORT_NOTICE", "报告通知"),
    FIND_PERSON("FIND_PERSON", "寻人"),
    ALL_NOTICE("ALL_NOTICE", "全员通知"),
    STAFF_NOTICE("STAFF_NOTICE", "医护通知"),
    REMINDER("REMINDER", "提醒"),
    EMERGENCY("EMERGENCY", "紧急通知"),
    MEDICATION_NOTICE("MEDICATION_NOTICE", "取药提醒");

    private final String code;
    private final String desc;
}