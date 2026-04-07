package com.yhj.his.module.pacs.enums;

import lombok.Getter;

@Getter
public enum ExamType {
    XRAY("X线", "X-Ray"),
    CT("CT", "Computed Tomography"),
    MRI("MRI", "Magnetic Resonance Imaging"),
    ULTRASOUND("超声", "Ultrasound"),
    ENDOSCOPY("内镜", "Endoscopy"),
    NUCLEAR("核医学", "Nuclear Medicine");

    private final String name;
    private final String description;

    ExamType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}