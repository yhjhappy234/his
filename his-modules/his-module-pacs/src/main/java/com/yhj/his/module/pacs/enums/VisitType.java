package com.yhj.his.module.pacs.enums;

import lombok.Getter;

@Getter
public enum VisitType {
    OUTPATIENT("门诊", "Outpatient"),
    INPATIENT("住院", "Inpatient"),
    EMERGENCY("急诊", "Emergency"),
    PHYSICAL_EXAM("体检", "Physical Examination");

    private final String name;
    private final String description;

    VisitType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}