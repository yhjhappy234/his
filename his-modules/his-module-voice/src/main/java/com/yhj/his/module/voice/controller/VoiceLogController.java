package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.enums.PlayResult;
import com.yhj.his.module.voice.enums.TaskType;
import com.yhj.his.module.voice.service.VoiceLogService;
import com.yhj.his.module.voice.vo.VoiceLogVO;
import com.yhj.his.module.voice.vo.VoiceStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 语音日志控制器
 */
@Tag(name = "语音日志管理", description = "播报日志查询与统计接口")
@RestController
@RequestMapping("/api/voice/v1/log")
@RequiredArgsConstructor
public class VoiceLogController {

    private final VoiceLogService voiceLogService;

    @Operation(summary = "查询日志详情", description = "根据ID查询日志详情")
    @GetMapping("/detail/{logId}")
    public Result<VoiceLogVO> getLogById(@Parameter(description = "日志ID") @PathVariable String logId) {
        return voiceLogService.getLogById(logId);
    }

    @Operation(summary = "根据任务ID查询日志", description = "根据任务ID查询播报日志")
    @GetMapping("/by-task/{taskId}")
    public Result<VoiceLogVO> getLogsByTaskId(@Parameter(description = "任务ID") @PathVariable String taskId) {
        return voiceLogService.getLogsByTaskId(taskId);
    }

    @Operation(summary = "根据任务编号查询日志", description = "根据任务编号查询播报日志")
    @GetMapping("/by-task-no/{taskNo}")
    public Result<VoiceLogVO> getLogsByTaskNo(@Parameter(description = "任务编号") @PathVariable String taskNo) {
        return voiceLogService.getLogsByTaskNo(taskNo);
    }

    @Operation(summary = "根据设备ID查询日志", description = "根据设备ID查询播报日志")
    @GetMapping("/by-device/{deviceId}")
    public Result<VoiceLogVO> getLogsByDeviceId(@Parameter(description = "设备ID") @PathVariable String deviceId) {
        return voiceLogService.getLogsByDeviceId(deviceId);
    }

    @Operation(summary = "分页查询日志列表", description = "分页查询播报日志列表")
    @GetMapping("/list")
    public Result<PageResult<VoiceLogVO>> getLogList(
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType,
            @Parameter(description = "播放结果") @RequestParam(required = false) String playResult,
            @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        TaskType type = null;
        if (taskType != null) {
            type = Arrays.stream(TaskType.values())
                    .filter(t -> t.getCode().equals(taskType))
                    .findFirst()
                    .orElse(null);
        }

        PlayResult result = null;
        if (playResult != null) {
            result = Arrays.stream(PlayResult.values())
                    .filter(r -> r.getCode().equals(playResult))
                    .findFirst()
                    .orElse(null);
        }

        return voiceLogService.getLogList(type, result, deviceId, startTime, endTime, keyword, pageNum, pageSize);
    }

    @Operation(summary = "查询统计数据", description = "查询指定时间范围内的播报统计数据")
    @GetMapping("/statistics")
    public Result<VoiceStatisticsVO> getStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return voiceLogService.getStatistics(startTime, endTime);
    }

    @Operation(summary = "查询今日统计", description = "查询今日播报统计数据")
    @GetMapping("/statistics/today")
    public Result<VoiceStatisticsVO> getTodayStatistics() {
        return voiceLogService.getTodayStatistics();
    }

    @Operation(summary = "查询本月统计", description = "查询本月播报统计数据")
    @GetMapping("/statistics/month")
    public Result<VoiceStatisticsVO> getMonthStatistics() {
        return voiceLogService.getMonthStatistics();
    }

    @Operation(summary = "导出日志数据", description = "导出播报日志数据(CSV格式)")
    @GetMapping("/export")
    public Result<byte[]> exportLogs(
            @Parameter(description = "开始时间") @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType) {

        TaskType type = null;
        if (taskType != null) {
            type = Arrays.stream(TaskType.values())
                    .filter(t -> t.getCode().equals(taskType))
                    .findFirst()
                    .orElse(null);
        }

        return voiceLogService.exportLogs(startTime, endTime, type);
    }
}