package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 样本状态枚举
 */
@Getter
@AllArgsConstructor
public enum SampleStatus {

    PENDING("待采集", "等待采集"),
    COLLECTED("已采集", "样本已采集"),
    RECEIVED("已核收", "样本已核收"),
    REJECTED("已拒收", "样本已拒收"),
    TESTING("检测中", "正在检测"),
    COMPLETED("已完成", "检测完成"),
    DISCARDED("已废弃", "样本已废弃");

    private final String code;
    private final String description;
}