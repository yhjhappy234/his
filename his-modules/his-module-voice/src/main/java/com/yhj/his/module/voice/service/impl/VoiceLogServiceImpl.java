package com.yhj.his.module.voice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.voice.entity.VoiceLog;
import com.yhj.his.module.voice.enums.PlayResult;
import com.yhj.his.module.voice.enums.TaskType;
import com.yhj.his.module.voice.repository.VoiceLogRepository;
import com.yhj.his.module.voice.service.VoiceLogService;
import com.yhj.his.module.voice.vo.VoiceLogVO;
import com.yhj.his.module.voice.vo.VoiceStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 语音日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceLogServiceImpl implements VoiceLogService {

    private final VoiceLogRepository voiceLogRepository;
    private final SequenceGenerator sequenceGenerator;

    @Override
    @Transactional
    public Result<Void> logPlay(String taskId, String taskNo, TaskType taskType, String content,
                                String deviceId, String deviceName, String deviceGroup,
                                PlayResult playResult, Integer playDuration, String errorMessage) {
        VoiceLog logEntity = new VoiceLog();
        logEntity.setId(sequenceGenerator.uuid());
        logEntity.setTaskId(taskId);
        logEntity.setTaskNo(taskNo);
        logEntity.setTaskType(taskType);
        logEntity.setContent(content);
        logEntity.setDeviceId(deviceId);
        logEntity.setDeviceName(deviceName);
        logEntity.setDeviceGroup(deviceGroup);
        logEntity.setPlayResult(playResult);
        logEntity.setPlayDuration(playDuration);
        logEntity.setErrorMessage(errorMessage);
        logEntity.setPlayTime(LocalDateTime.now());

        voiceLogRepository.save(logEntity);

        log.debug("记录播报日志: taskNo={}, result={}", taskNo, playResult);
        return Result.successVoid();
    }

    @Override
    public Result<VoiceLogVO> getLogById(String logId) {
        VoiceLog logEntity = voiceLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "日志不存在"));
        return Result.success(convertToVO(logEntity));
    }

    @Override
    public Result<VoiceLogVO> getLogsByTaskId(String taskId) {
        List<VoiceLog> logs = voiceLogRepository.findByTaskId(taskId);
        if (logs.isEmpty()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "未找到相关日志");
        }
        VoiceLogVO vo = convertToVO(logs.get(0));
        return Result.success(vo);
    }

    @Override
    public Result<VoiceLogVO> getLogsByTaskNo(String taskNo) {
        List<VoiceLog> logs = voiceLogRepository.findByTaskNo(taskNo);
        if (logs.isEmpty()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "未找到相关日志");
        }
        VoiceLogVO vo = convertToVO(logs.get(0));
        return Result.success(vo);
    }

    @Override
    public Result<VoiceLogVO> getLogsByDeviceId(String deviceId) {
        List<VoiceLog> logs = voiceLogRepository.findByDeviceId(deviceId);
        if (logs.isEmpty()) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "未找到相关日志");
        }
        VoiceLogVO vo = convertToVO(logs.get(0));
        return Result.success(vo);
    }

    @Override
    public Result<PageResult<VoiceLogVO>> getLogList(TaskType taskType, PlayResult playResult,
                                                     String deviceId, LocalDateTime startTime, LocalDateTime endTime,
                                                     String keyword, Integer pageNum, Integer pageSize) {
        String kw = StrUtil.isNotBlank(keyword) ? "%" + keyword + "%" : null;

        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "playTime"));
        Page<VoiceLog> page = voiceLogRepository.searchLogs(taskType, playResult, deviceId, startTime, endTime, kw, pageRequest);

        List<VoiceLogVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<VoiceStatisticsVO> getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        VoiceStatisticsVO vo = new VoiceStatisticsVO();

        // 总播报次数
        Long totalCount = voiceLogRepository.countByPlayTimeBetween(startTime, endTime);
        vo.setTotalCount(totalCount);

        // 成功次数
        Long successCount = voiceLogRepository.countSuccessByPlayTimeBetween(startTime, endTime);
        vo.setSuccessCount(successCount);

        // 失败次数
        Long failCount = voiceLogRepository.countFailedByPlayTimeBetween(startTime, endTime);
        vo.setFailCount(failCount);

        // 成功率
        if (totalCount > 0) {
            vo.setSuccessRate((double) successCount / totalCount * 100);
        } else {
            vo.setSuccessRate(0.0);
        }

        // 平均播放时长
        Double avgDuration = voiceLogRepository.avgDurationByPlayTimeBetween(startTime, endTime);
        vo.setAvgDuration(avgDuration != null ? avgDuration : 0.0);

        // 按任务类型统计
        Map<String, Long> typeCount = new HashMap<>();
        List<Object[]> typeStats = voiceLogRepository.countByTaskType(startTime, endTime);
        for (Object[] stat : typeStats) {
            TaskType type = (TaskType) stat[0];
            Long count = (Long) stat[1];
            typeCount.put(type != null ? type.getCode() : "UNKNOWN", count);
        }
        vo.setTypeCount(typeCount);

        // 按设备分组统计
        Map<String, Long> groupCount = new HashMap<>();
        List<Object[]> groupStats = voiceLogRepository.countByDeviceGroup(startTime, endTime);
        for (Object[] stat : groupStats) {
            String group = (String) stat[0];
            Long count = (Long) stat[1];
            groupCount.put(group != null ? group : "UNKNOWN", count);
        }
        vo.setGroupCount(groupCount);

        // 按日期统计
        Map<String, Long> dateCount = new HashMap<>();
        List<Object[]> dateStats = voiceLogRepository.countByDate(startTime, endTime);
        for (Object[] stat : dateStats) {
            LocalDate date = (LocalDate) stat[0];
            Long count = (Long) stat[1];
            dateCount.put(date.toString(), count);
        }
        vo.setDateCount(dateCount);

        // 高峰时段
        Object[] peakStat = voiceLogRepository.findPeakHour(startTime, endTime);
        if (peakStat != null && peakStat.length >= 2) {
            vo.setPeakHour((Integer) peakStat[0]);
            vo.setPeakCount((Long) peakStat[1]);
        }

        return Result.success(vo);
    }

    @Override
    public Result<VoiceStatisticsVO> getTodayStatistics() {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = LocalDate.now().atTime(LocalTime.MAX);
        return getStatistics(startTime, endTime);
    }

    @Override
    public Result<VoiceStatisticsVO> getMonthStatistics() {
        LocalDateTime startTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = LocalDate.now().atTime(LocalTime.MAX);
        return getStatistics(startTime, endTime);
    }

    @Override
    public Result<byte[]> exportLogs(LocalDateTime startTime, LocalDateTime endTime, TaskType taskType) {
        // TODO: 实现日志导出功能(CSV/Excel格式)
        log.info("导出日志: startTime={}, endTime={}, taskType={}", startTime, endTime, taskType);
        return Result.success(new byte[0]);
    }

    /**
     * 转换为VO
     */
    private VoiceLogVO convertToVO(VoiceLog logEntity) {
        VoiceLogVO vo = new VoiceLogVO();
        vo.setLogId(logEntity.getId());
        vo.setTaskId(logEntity.getTaskId());
        vo.setTaskNo(logEntity.getTaskNo());
        vo.setTaskType(logEntity.getTaskType() != null ? logEntity.getTaskType().getCode() : null);
        vo.setTaskTypeDesc(logEntity.getTaskType() != null ? logEntity.getTaskType().getDesc() : null);
        vo.setContent(logEntity.getContent());
        vo.setDeviceId(logEntity.getDeviceId());
        vo.setDeviceName(logEntity.getDeviceName());
        vo.setDeviceGroup(logEntity.getDeviceGroup());
        vo.setPlayResult(logEntity.getPlayResult() != null ? logEntity.getPlayResult().getCode() : null);
        vo.setPlayResultDesc(logEntity.getPlayResult() != null ? logEntity.getPlayResult().getDesc() : null);
        vo.setPlayDuration(logEntity.getPlayDuration());
        vo.setErrorMessage(logEntity.getErrorMessage());
        vo.setPlayTime(logEntity.getPlayTime());
        vo.setOperatorName(logEntity.getOperatorName());
        vo.setCreateTime(logEntity.getCreateTime());
        return vo;
    }
}