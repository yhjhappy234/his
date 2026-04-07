package com.yhj.his.module.voice.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.voice.enums.TemplateType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音模板实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "voice_template")
public class VoiceTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板编码
     */
    @Column(name = "template_code", length = 30, nullable = false, unique = true)
    private String templateCode;

    /**
     * 模板名称
     */
    @Column(name = "template_name", length = 100, nullable = false)
    private String templateName;

    /**
     * 模板类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", length = 20, nullable = false)
    private TemplateType templateType;

    /**
     * 内容模板(支持占位符如{patientName})
     */
    @Column(name = "content_template", columnDefinition = "TEXT", nullable = false)
    private String contentTemplate;

    /**
     * 参数定义(JSON格式)
     */
    @Column(name = "params_define", columnDefinition = "TEXT")
    private String paramsDefine;

    /**
     * 语音引擎
     */
    @Column(name = "voice_engine", length = 20)
    private String voiceEngine;

    /**
     * 语音名称
     */
    @Column(name = "voice_name", length = 50)
    private String voiceName;

    /**
     * 语速(0.5-2.0)
     */
    @Column(name = "speed")
    private Double speed = 1.0;

    /**
     * 音量(0-100)
     */
    @Column(name = "volume")
    private Integer volume = 80;

    /**
     * 音调(0-100)
     */
    @Column(name = "pitch")
    private Integer pitch = 50;

    /**
     * 前置提示音文件路径
     */
    @Column(name = "pre_audio", length = 200)
    private String preAudio;

    /**
     * 后置提示音文件路径
     */
    @Column(name = "post_audio", length = 200)
    private String postAudio;

    /**
     * 是否系统模板
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 备注
     */
    @Column(name = "remark", length = 200)
    private String remark;
}