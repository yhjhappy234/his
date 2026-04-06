package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.*;
import com.yhj.his.module.voice.enums.TaskType;
import com.yhj.his.module.voice.vo.VoiceTaskVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 语音任务服务接口
 */
public interface VoiceTaskService {

    /**
     * 创建语音任务
     */
    Result<VoiceTaskVO> createTask(VoiceTaskCreateRequest request);

    /**
     * 立即播报
     */
    Result<VoiceTaskVO> immediateBroadcast(ImmediateBroadcastRequest request);

    /**
     * 叫号播报
     */
    Result<VoiceTaskVO> callNumber(CallNumberRequest request);

    /**
     * 过号重呼
     */
    Result<VoiceTaskVO> retryCall(CallNumberRequest request);

    /**
     * 寻人播报
     */
    Result<VoiceTaskVO> findPerson(FindPersonRequest request);

    /**
     * 报告通知播报
     */
    Result<VoiceTaskVO> reportNotice(ReportNoticeRequest request);

    /**
     * 取药提醒播报
     */
    Result<VoiceTaskVO> medicationNotice(MedicationNoticeRequest request);

    /**
     * 全员通知播报
     */
    Result<VoiceTaskVO> allNotice(AllNoticeRequest request);

    /**
     * 危急值通知播报
     */
    Result<VoiceTaskVO> criticalValueNotice(CriticalValueRequest request);

    /**
     * 取消任务
     */
    Result<Void> cancelTask(String taskId);

    /**
     * 重新执行失败的任务
     */
    Result<Void> retryTask(String taskId);

    /**
     * 根据ID查询任务
     */
    Result<VoiceTaskVO> getTaskById(String taskId);

    /**
     * 根据编号查询任务
     */
    Result<VoiceTaskVO> getTaskByNo(String taskNo);

    /**
     * 分页查询任务列表
     */
    Result<PageResult<VoiceTaskVO>> getTaskList(TaskType taskType, String status,
                                                 LocalDateTime startTime, LocalDateTime endTime,
                                                 String keyword, Integer pageNum, Integer pageSize);

    /**
     * 查询待播报任务列表
     */
    Result<List<VoiceTaskVO>> getPendingTasks();

    /**
     * 根据业务ID查询任务
     */
    Result<VoiceTaskVO> getTaskByBizId(String bizId);
}