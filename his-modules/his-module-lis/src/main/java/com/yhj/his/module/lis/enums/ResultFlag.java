package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果标识枚举
 */
@Getter
@AllArgsConstructor
public enum ResultFlag {

    NORMAL("正常", "结果正常"),
    HIGH("偏高", "结果偏高"),
    LOW("偏低", "结果偏低"),
    CRITICAL("危急", "危急值"),
    POSITIVE("阳性", "结果阳性"),
    NEGATIVE("阴性", "结果阴性");

    private final String code;
    private final String description;
}