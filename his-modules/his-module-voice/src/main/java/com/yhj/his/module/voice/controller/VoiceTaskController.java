package com.yhj.his.module.voice.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.*;
import com.yhj.his.module.voice.enums.TaskType;
import com.yhj.his.module.voice.service.VoiceTaskService;
import com.yhj.his.module.voice.vo.VoiceTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 语音任务控制器
 */
@Tag(name = "语音任务管理", description = "语音播报任务相关接口")
@RestController
@RequestMapping("/api/voice/v1/task")
@RequiredArgsConstructor
public class VoiceTaskController {

    private final VoiceTaskService voiceTaskService;

    @Operation(summary = "创建语音任务", description = "创建语音播报任务")
    @PostMapping("/create")
    public Result<VoiceTaskVO> createTask(@Valid @RequestBody VoiceTaskCreateRequest request) {
        return voiceTaskService.createTask(request);
    }

    @Operation(summary = "立即播报", description = "立即执行播报(用于紧急通知等场景)")
    @PostMapping("/broadcast/immediate")
    public Result<VoiceTaskVO> immediateBroadcast(@Valid @RequestBody ImmediateBroadcastRequest request) {
        return voiceTaskService.immediateBroadcast(request);
    }

    @Operation(summary = "叫号播报", description = "门诊叫号播报")
    @PostMapping("/call-number")
    public Result<VoiceTaskVO> callNumber(@Valid @RequestBody CallNumberRequest request) {
        return voiceTaskService.callNumber(request);
    }

    @Operation(summary = "过号重呼", description = "过号患者重新呼叫")
    @PostMapping("/call-retry")
    public Result<VoiceTaskVO> retryCall(@Valid @RequestBody CallNumberRequest request) {
        return voiceTaskService.retryCall(request);
    }

    @Operation(summary = "寻人播报", description = "语音寻人播报")
    @PostMapping("/find-person")
    public Result<VoiceTaskVO> findPerson(@Valid @RequestBody FindPersonRequest request) {
        return voiceTaskService.findPerson(request);
    }

    @Operation(summary = "报告通知播报", description = "检验/影像报告完成通知")
    @PostMapping("/report-notice")
    public Result<VoiceTaskVO> reportNotice(@Valid @RequestBody ReportNoticeRequest request) {
        return voiceTaskService.reportNotice(request);
    }

    @Operation(summary = "取药提醒播报", description = "药房取药语音提醒")
    @PostMapping("/medication-notice")
    public Result<VoiceTaskVO> medicationNotice(@Valid @RequestBody MedicationNoticeRequest request) {
        return voiceTaskService.medicationNotice(request);
    }

    @Operation(summary = "全员通知播报", description = "全院广播通知")
    @PostMapping("/all-notice")
    public Result<VoiceTaskVO> allNotice(@Valid @RequestBody AllNoticeRequest request) {
        return voiceTaskService.allNotice(request);
    }

    @Operation(summary = "危急值通知播报", description = "危急值语音通知")
    @PostMapping("/critical-value")
    public Result<VoiceTaskVO> criticalValueNotice(@Valid @RequestBody CriticalValueRequest request) {
        return voiceTaskService.criticalValueNotice(request);
    }

    @Operation(summary = "取消任务", description = "取消待播报的任务")
    @PostMapping("/cancel/{taskId}")
    public Result<Void> cancelTask(@Parameter(description = "任务ID") @PathVariable String taskId) {
        return voiceTaskService.cancelTask(taskId);
    }

    @Operation(summary = "重试任务", description = "重新执行失败的任务")
    @PostMapping("/retry/{taskId}")
    public Result<Void> retryTask(@Parameter(description = "任务ID") @PathVariable String taskId) {
        return voiceTaskService.retryTask(taskId);
    }

    @Operation(summary = "查询任务详情", description = "根据ID查询任务详情")
    @GetMapping("/detail/{taskId}")
    public Result<VoiceTaskVO> getTaskById(@Parameter(description = "任务ID") @PathVariable String taskId) {
        return voiceTaskService.getTaskById(taskId);
    }

    @Operation(summary = "根据编号查询任务", description = "根据任务编号查询任务详情")
    @GetMapping("/by-no/{taskNo}")
    public Result<VoiceTaskVO> getTaskByNo(@Parameter(description = "任务编号") @PathVariable String taskNo) {
        return voiceTaskService.getTaskByNo(taskNo);
    }

    @Operation(summary = "分页查询任务列表", description = "分页查询语音任务列表")
    @GetMapping("/list")
    public Result<PageResult<VoiceTaskVO>> getTaskList(
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
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

        return voiceTaskService.getTaskList(type, status, startTime, endTime, keyword, pageNum, pageSize);
    }

    @Operation(summary = "查询待播报任务", description = "查询所有待播报的任务列表")
    @GetMapping("/pending")
    public Result<List<VoiceTaskVO>> getPendingTasks() {
        return voiceTaskService.getPendingTasks();
    }

    @Operation(summary = "根据业务ID查询任务", description = "根据关联业务ID查询任务")
    @GetMapping("/by-biz/{bizId}")
    public Result<VoiceTaskVO> getTaskByBizId(@Parameter(description = "业务ID") @PathVariable String bizId) {
        return voiceTaskService.getTaskByBizId(bizId);
    }
}