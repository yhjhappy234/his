package com.yhj.his.module.voice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模板类型枚举
 */
@Getter
@AllArgsConstructor
public enum TemplateType {

    CALL_STANDARD("CALL_STANDARD", "标准叫号模板"),
    CALL_RETRY("CALL_RETRY", "过号重呼模板"),
    CALL_RECHECK("CALL_RECHECK", "复诊叫号模板"),
    REPORT_LAB("REPORT_LAB", "检验报告模板"),
    REPORT_RADIOLOGY("REPORT_RADIOLOGY", "影像报告模板"),
    FIND_PATIENT("FIND_PATIENT", "患者寻人模板"),
    FIND_FAMILY("FIND_FAMILY", "家属寻人模板"),
    FIND_STAFF("FIND_STAFF", "医护寻人模板"),
    SYSTEM_NOTICE("SYSTEM_NOTICE", "系统公告模板"),
    EMERGENCY_NOTICE("EMERGENCY_NOTICE", "紧急通知模板"),
    CRITICAL_VALUE("CRITICAL_VALUE", "危急值通知模板"),
    MEDICATION("MEDICATION", "取药提醒模板");

    private final String code;
    private final String desc;
}