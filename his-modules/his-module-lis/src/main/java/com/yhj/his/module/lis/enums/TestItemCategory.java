package com.yhj.his.module.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 检验项目分类枚举
 */
@Getter
@AllArgsConstructor
public enum TestItemCategory {

    BIOCHEMISTRY("生化", "生化检验"),
    IMMUNOLOGY("免疫", "免疫检验"),
    BLOOD_ROUTINE("血常规", "血常规检验"),
    URINE_ROUTINE("尿常规", "尿常规检验"),
    COAGULATION("凝血", "凝血功能检验"),
    MICROBIOLOGY("微生物", "微生物检验"),
    TUMOR_MARKER("肿瘤标志物", "肿瘤标志物检测"),
    THYROID("甲状腺", "甲状腺功能检测"),
    LIVER_FUNCTION("肝功能", "肝功能检测"),
    KIDNEY_FUNCTION("肾功能", "肾功能检测"),
    BLOOD_LIPID("血脂", "血脂检测"),
    OTHER("其他", "其他检验");

    private final String code;
    private final String description;
}