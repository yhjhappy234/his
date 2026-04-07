package com.yhj.his.module.voice.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.voice.enums.TaskStatus;
import com.yhj.his.module.voice.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 语音任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "voice_task")
public class VoiceTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号
     */
    @Column(name = "task_no", length = 30, nullable = false, unique = true)
    private String taskNo;

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 20, nullable = false)
    private TaskType taskType;

    /**
     * 播报内容
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 模板ID
     */
    @Column(name = "template_id", length = 36)
    private String templateId;

    /**
     * 模板参数(JSON格式)
     */
    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    /**
     * 优先级(1-10, 数字越小优先级越高)
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 5;

    /**
     * 目标设备ID列表(JSON格式)
     */
    @Column(name = "target_devices", columnDefinition = "TEXT")
    private String targetDevices;

    /**
     * 目标设备分组ID列表(JSON格式)
     */
    @Column(name = "target_groups", columnDefinition = "TEXT")
    private String targetGroups;

    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * 已播放次数
     */
    @Column(name = "play_count")
    private Integer playCount = 0;

    /**
     * 最大播放次数
     */
    @Column(name = "max_play_count")
    private Integer maxPlayCount = 1;

    /**
     * 计划播放时间
     */
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    /**
     * 实际播放开始时间
     */
    @Column(name = "play_start_time")
    private LocalDateTime playStartTime;

    /**
     * 播放结束时间
     */
    @Column(name = "play_end_time")
    private LocalDateTime playEndTime;

    /**
     * 播放时长(秒)
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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
     * 创建人ID
     */
    @Column(name = "creator_id", length = 36)
    private String creatorId;

    /**
     * 创建人姓名
     */
    @Column(name = "creator_name", length = 50)
    private String creatorName;

    /**
     * 关联业务ID
     */
    @Column(name = "biz_id", length = 50)
    private String bizId;

    /**
     * 关联业务类型
     */
    @Column(name = "biz_type", length = 30)
    private String bizType;
}