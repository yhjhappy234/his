package com.yhj.his.module.voice.entity;

import com.yhj.his.common.core.domain.BaseEntity;
import com.yhj.his.module.voice.enums.PlayResult;
import com.yhj.his.module.voice.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 语音播报日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "voice_log")
public class VoiceLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @Column(name = "task_id", length = 36)
    private String taskId;

    /**
     * 任务编号
     */
    @Column(name = "task_no", length = 30)
    private String taskNo;

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 20)
    private TaskType taskType;

    /**
     * 播报内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 设备ID
     */
    @Column(name = "device_id", length = 36)
    private String deviceId;

    /**
     * 设备名称
     */
    @Column(name = "device_name", length = 100)
    private String deviceName;

    /**
     * 设备分组
     */
    @Column(name = "device_group", length = 100)
    private String deviceGroup;

    /**
     * 播放结果
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "play_result", length = 20)
    private PlayResult playResult;

    /**
     * 播放时长(秒)
     */
    @Column(name = "play_duration")
    private Integer playDuration;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 播放时间
     */
    @Column(name = "play_time", nullable = false)
    private LocalDateTime playTime;

    /**
     * 播放参数(JSON格式)
     */
    @Column(name = "play_params", columnDefinition = "TEXT")
    private String playParams;

    /**
     * 操作人ID
     */
    @Column(name = "operator_id", length = 36)
    private String operatorId;

    /**
     * 操作人姓名
     */
    @Column(name = "operator_name", length = 50)
    private String operatorName;
}