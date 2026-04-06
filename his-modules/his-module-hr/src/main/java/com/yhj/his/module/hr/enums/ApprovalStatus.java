package com.yhj.his.module.hr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApprovalStatus {

    PENDING("待审批", "等待审批"),
    APPROVED("已通过", "审批通过"),
    REJECTED("已拒绝", "审批拒绝"),
    CANCELLED("已取消", "已取消");

    private final String name;
    private final String description;
}