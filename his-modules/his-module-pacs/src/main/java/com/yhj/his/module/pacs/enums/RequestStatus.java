package com.yhj.his.module.pacs.enums;

import lombok.Getter;

@Getter
public enum RequestStatus {
    PENDING("待预约", "申请已提交，等待预约"),
    SCHEDULED("已预约", "已安排预约时间"),
    REGISTERED("已登记", "患者已到检登记"),
    IN_PROGRESS("检查中", "正在进行检查"),
    COMPLETED("检查完成", "检查已完成，待报告"),
    REPORTED("已报告", "报告已完成"),
    CANCELLED("已取消", "检查已取消");

    private final String name;
    private final String description;

    RequestStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }
}