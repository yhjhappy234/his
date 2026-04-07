package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 危急值状态枚举
 */
@Getter
@AllArgsConstructor
public enum CriticalValueStatus {

    PENDING("待处理", "等待处理"),
    NOTIFIED("已通知", "已通知临床"),
    CONFIRMED("已确认", "临床已确认"),
    HANDLED("已处理", "处理完成"),
    CLOSED("已关闭", "已关闭");

    private final String code;
    private final String description;
}