package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.enums.PlayResult;
import com.yhj.his.module.voice.enums.TaskType;
import com.yhj.his.module.voice.vo.VoiceLogVO;
import com.yhj.his.module.voice.vo.VoiceStatisticsVO;

import java.time.LocalDateTime;

/**
 * 语音日志服务接口
 */
public interface VoiceLogService {

    /**
     * 记录播报日志
     */
    Result<Void> logPlay(String taskId, String taskNo, TaskType taskType, String content,
                         String deviceId, String deviceName, String deviceGroup,
                         PlayResult playResult, Integer playDuration, String errorMessage);

    /**
     * 根据ID查询日志
     */
    Result<VoiceLogVO> getLogById(String logId);

    /**
     * 根据任务ID查询日志列表
     */
    Result<VoiceLogVO> getLogsByTaskId(String taskId);

    /**
     * 根据任务编号查询日志列表
     */
    Result<VoiceLogVO> getLogsByTaskNo(String taskNo);

    /**
     * 根据设备ID查询日志列表
     */
    Result<VoiceLogVO> getLogsByDeviceId(String deviceId);

    /**
     * 分页查询日志列表
     */
    Result<PageResult<VoiceLogVO>> getLogList(TaskType taskType, PlayResult playResult,
                                               String deviceId, LocalDateTime startTime, LocalDateTime endTime,
                                               String keyword, Integer pageNum, Integer pageSize);

    /**
     * 查询指定时间范围内的统计
     */
    Result<VoiceStatisticsVO> getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询今日统计
     */
    Result<VoiceStatisticsVO> getTodayStatistics();

    /**
     * 查询本月统计
     */
    Result<VoiceStatisticsVO> getMonthStatistics();

    /**
     * 导出日志数据
     */
    Result<byte[]> exportLogs(LocalDateTime startTime, LocalDateTime endTime, TaskType taskType);
}