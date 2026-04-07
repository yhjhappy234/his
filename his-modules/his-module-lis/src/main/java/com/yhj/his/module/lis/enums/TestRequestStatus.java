package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 检验申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum TestRequestStatus {

    REQUESTED("申请", "已申请"),
    SAMPLED("已采样", "样本已采集"),
    RECEIVED("已核收", "样本已核收"),
    TESTING("检测中", "正在检测"),
    AUDITED("已审核", "结果已审核"),
    PUBLISHED("已发布", "报告已发布"),
    CANCELLED("已取消", "申请已取消");

    private final String code;
    private final String description;
}