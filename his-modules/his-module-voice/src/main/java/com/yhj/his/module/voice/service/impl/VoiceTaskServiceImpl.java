package com.yhj.his.module.voice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.voice.dto.*;
import com.yhj.his.module.voice.entity.*;
import com.yhj.his.module.voice.enums.*;
import com.yhj.his.module.voice.repository.*;
import com.yhj.his.module.voice.service.VoiceTaskService;
import com.yhj.his.module.voice.service.VoiceTemplateService;
import com.yhj.his.module.voice.vo.VoiceTaskVO;
import com.yhj.his.module.voice.vo.VoiceTemplateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 语音任务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceTaskServiceImpl implements VoiceTaskService {

    private final VoiceTaskRepository voiceTaskRepository;
    private final VoiceTemplateRepository voiceTemplateRepository;
    private final VoiceTemplateService voiceTemplateService;
    private final AudioDeviceRepository audioDeviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final VoiceLogRepository voiceLogRepository;

    @Override
    @Transactional
    public Result<VoiceTaskVO> createTask(VoiceTaskCreateRequest request) {
        // 验证任务类型
        TaskType taskType = Arrays.stream(TaskType.values())
                .filter(t -> t.getCode().equals(request.getTaskType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAM_ERROR, "无效的任务类型"));

        // 生成内容
        String content;
        String templateId = null;
        String templateName = null;

        if (StrUtil.isNotBlank(request.getTemplateCode())) {
            // 使用模板生成内容
            Result<VoiceTemplateVO> templateResult = voiceTemplateService.getTemplateByCode(request.getTemplateCode());
            if (templateResult.getCode() != 0) {
                throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在: " + request.getTemplateCode());
            }
            VoiceTemplateVO template = templateResult.getData();
            templateId = template.getTemplateId();
            templateName = template.getTemplateName();

            Result<String> contentResult = voiceTemplateService.generateContent(request.getTemplateCode(), request.getParams());
            if (contentResult.getCode() != 0) {
                throw new BusinessException(ErrorCode.BIZ_ERROR, "内容生成失败");
            }
            content = contentResult.getData();
        } else if (StrUtil.isNotBlank(request.getContent())) {
            content = request.getContent();
        } else {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "请提供模板编码或播报内容");
        }

        // 创建任务
        VoiceTask task = new VoiceTask();
        task.setTaskNo(SequenceGenerator.generateWithTime("VT"));
        task.setTaskType(taskType);
        task.setContent(content);
        task.setTemplateId(templateId);
        if (request.getParams() != null) {
            task.setParams(JSONUtil.toJsonStr(request.getParams()));
        }
        task.setPriority(request.getPriority() != null ? request.getPriority() : 5);

        // 目标设备和分组
        if (request.getTargetDevices() != null && !request.getTargetDevices().isEmpty()) {
            task.setTargetDevices(JSONUtil.toJsonStr(request.getTargetDevices()));
        }
        if (request.getTargetGroups() != null && !request.getTargetGroups().isEmpty()) {
            task.setTargetGroups(JSONUtil.toJsonStr(request.getTargetGroups()));
        }

        task.setMaxPlayCount(request.getMaxPlayCount() != null ? request.getMaxPlayCount() : 1);
        task.setScheduledTime(request.getScheduledTime());
        task.setSpeed(request.getSpeed() != null ? request.getSpeed() : 1.0);
        task.setVolume(request.getVolume() != null ? request.getVolume() : 80);
        task.setVoiceEngine(request.getVoiceEngine());
        task.setVoiceName(request.getVoiceName());
        task.setCreatorId(request.getCreatorId());
        task.setCreatorName(request.getCreatorName());
        task.setBizId(request.getBizId());
        task.setBizType(request.getBizType());
        task.setStatus(TaskStatus.PENDING);

        VoiceTask saved = voiceTaskRepository.save(task);

        VoiceTaskVO vo = convertToVO(saved);
        vo.setTemplateName(templateName);
        // 设置预计播放时间
        vo.setEstimatedTime(LocalDateTime.now().plusSeconds(5));

        log.info("创建语音任务成功: taskNo={}, content={}", saved.getTaskNo(), content);
        return Result.success("任务创建成功", vo);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> immediateBroadcast(ImmediateBroadcastRequest request) {
        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(TaskType.ALL_NOTICE.getCode());
        taskRequest.setContent(request.getContent());
        taskRequest.setPriority(request.getPriority());
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setSpeed(request.getSpeed());
        taskRequest.setVolume(request.getVolume());
        taskRequest.setVoiceEngine(request.getVoiceEngine());
        taskRequest.setVoiceName(request.getVoiceName());

        if (request.getRepeat() != null && request.getRepeat()) {
            taskRequest.setMaxPlayCount(request.getRepeatCount() != null ? request.getRepeatCount() : 2);
        }

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> callNumber(CallNumberRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("queueNo", request.getQueueNo());
        // 患者姓名脱敏处理
        String patientName = request.getPatientName();
        if (request.getDesensitize() != null && request.getDesensitize()) {
            patientName = desensitizeName(patientName);
        }
        params.put("patientName", patientName);
        params.put("roomNo", request.getRoomNo());
        if (StrUtil.isNotBlank(request.getRoomName())) {
            params.put("roomName", request.getRoomName());
        }
        if (StrUtil.isNotBlank(request.getDeptName())) {
            params.put("deptName", request.getDeptName());
        }

        String templateCode;
        TaskType taskType;
        switch (request.getCallType()) {
            case "RETRY":
                templateCode = TemplateType.CALL_RETRY.getCode();
                taskType = TaskType.CALL_NUMBER;
                break;
            case "RECHECK":
                templateCode = TemplateType.CALL_RECHECK.getCode();
                taskType = TaskType.CALL_NUMBER;
                break;
            default:
                templateCode = TemplateType.CALL_STANDARD.getCode();
                taskType = TaskType.CALL_NUMBER;
        }

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(taskType.getCode());
        taskRequest.setTemplateCode(templateCode);
        taskRequest.setParams(params);
        taskRequest.setPriority(5);
        taskRequest.setMaxPlayCount(2);
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setBizId(request.getBizId());
        taskRequest.setBizType("OUTPATIENT_REGISTRATION");

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> retryCall(CallNumberRequest request) {
        request.setCallType("RETRY");
        return callNumber(request);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> findPerson(FindPersonRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("personName", request.getPersonName());
        params.put("targetLocation", request.getTargetLocation());
        params.put("reason", request.getReason());
        params.put("deptName", request.getDeptName());
        params.put("familyRelation", request.getFamilyRelation());
        params.put("doctorName", request.getDoctorName());

        String templateCode;
        TaskType taskType = TaskType.FIND_PERSON;
        switch (request.getPersonType()) {
            case "PATIENT":
                templateCode = TemplateType.FIND_PATIENT.getCode();
                break;
            case "FAMILY":
                templateCode = TemplateType.FIND_FAMILY.getCode();
                break;
            case "STAFF":
                templateCode = TemplateType.FIND_STAFF.getCode();
                break;
            default:
                templateCode = TemplateType.FIND_PATIENT.getCode();
        }

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(taskType.getCode());
        taskRequest.setTemplateCode(templateCode);
        taskRequest.setParams(params);
        taskRequest.setPriority(request.getUrgent() != null && request.getUrgent() ? 2 : 6);
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> reportNotice(ReportNoticeRequest request) {
        Map<String, Object> params = new HashMap<>();
        // 患者姓名脱敏
        params.put("patientName", desensitizeName(request.getPatientName()));
        params.put("reportType", request.getReportType());
        params.put("examType", request.getExamType());
        params.put("windowNo", request.getWindowNo());
        params.put("deptName", request.getDeptName());

        String templateCode;
        switch (request.getReportType()) {
            case "RADIOLOGY":
                templateCode = TemplateType.REPORT_RADIOLOGY.getCode();
                break;
            default:
                templateCode = TemplateType.REPORT_LAB.getCode();
        }

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(TaskType.REPORT_NOTICE.getCode());
        taskRequest.setTemplateCode(templateCode);
        taskRequest.setParams(params);
        taskRequest.setPriority(5);
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setBizId(request.getBizId());
        taskRequest.setBizType("REPORT");

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> medicationNotice(MedicationNoticeRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("patientName", desensitizeName(request.getPatientName()));
        params.put("windowNo", request.getWindowNo());
        params.put("pharmacyName", request.getPharmacyName());
        params.put("medicationCode", request.getMedicationCode());

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(TaskType.MEDICATION_NOTICE.getCode());
        taskRequest.setTemplateCode(TemplateType.MEDICATION.getCode());
        taskRequest.setParams(params);
        taskRequest.setPriority(5);
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setBizId(request.getBizId());
        taskRequest.setBizType("MEDICATION");

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> allNotice(AllNoticeRequest request) {
        TaskType taskType;
        switch (request.getNoticeType()) {
            case "EMERGENCY":
                taskType = TaskType.EMERGENCY;
                break;
            case "FIRE":
                taskType = TaskType.EMERGENCY;
                break;
            default:
                taskType = TaskType.ALL_NOTICE;
        }

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(taskType.getCode());
        taskRequest.setContent(request.getContent());
        taskRequest.setPriority(request.getUrgent() != null && request.getUrgent() ? 1 : request.getPriority());
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setMaxPlayCount(request.getRepeat() != null && request.getRepeat() ?
                (request.getRepeatCount() != null ? request.getRepeatCount() : 2) : 1);
        taskRequest.setCreatorId(request.getOperatorId());
        taskRequest.setCreatorName(request.getOperatorName());

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<VoiceTaskVO> criticalValueNotice(CriticalValueRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("deptName", request.getDeptName());
        params.put("bedNo", request.getBedNo());
        params.put("patientName", request.getPatientName());
        params.put("itemName", request.getItemName());
        params.put("value", request.getValue());

        VoiceTaskCreateRequest taskRequest = new VoiceTaskCreateRequest();
        taskRequest.setTaskType(TaskType.STAFF_NOTICE.getCode());
        taskRequest.setTemplateCode(TemplateType.CRITICAL_VALUE.getCode());
        taskRequest.setParams(params);
        taskRequest.setPriority(2); // 高优先级
        if (request.getTargetGroups() != null) {
            taskRequest.setTargetGroups(Arrays.asList(request.getTargetGroups()));
        }
        if (request.getTargetDevices() != null) {
            taskRequest.setTargetDevices(Arrays.asList(request.getTargetDevices()));
        }
        taskRequest.setBizId(request.getBizId());
        taskRequest.setBizType("CRITICAL_VALUE");

        return createTask(taskRequest);
    }

    @Override
    @Transactional
    public Result<Void> cancelTask(String taskId) {
        VoiceTask task = voiceTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "任务不存在"));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "已完成的任务无法取消");
        }

        task.setStatus(TaskStatus.CANCELLED);
        voiceTaskRepository.save(task);

        log.info("取消语音任务: taskId={}", taskId);
        return Result.successVoid();
    }

    @Override
    @Transactional
    public Result<Void> retryTask(String taskId) {
        VoiceTask task = voiceTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "任务不存在"));

        if (task.getStatus() != TaskStatus.FAILED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "只有失败的任务可以重试");
        }

        task.setStatus(TaskStatus.PENDING);
        task.setPlayCount(0);
        task.setErrorMessage(null);
        voiceTaskRepository.save(task);

        log.info("重试语音任务: taskId={}", taskId);
        return Result.successVoid();
    }

    @Override
    public Result<VoiceTaskVO> getTaskById(String taskId) {
        VoiceTask task = voiceTaskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "任务不存在"));
        return Result.success(convertToVO(task));
    }

    @Override
    public Result<VoiceTaskVO> getTaskByNo(String taskNo) {
        VoiceTask task = voiceTaskRepository.findByTaskNo(taskNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "任务不存在"));
        return Result.success(convertToVO(task));
    }

    @Override
    public Result<PageResult<VoiceTaskVO>> getTaskList(TaskType taskType, String status,
                                                       LocalDateTime startTime, LocalDateTime endTime,
                                                       String keyword, Integer pageNum, Integer pageSize) {
        TaskStatus taskStatus = null;
        if (StrUtil.isNotBlank(status)) {
            taskStatus = Arrays.stream(TaskStatus.values())
                    .filter(s -> s.getCode().equals(status))
                    .findFirst()
                    .orElse(null);
        }

        String kw = keyword != null ? "%" + keyword + "%" : null;

        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<VoiceTask> page = voiceTaskRepository.searchTasks(taskType, taskStatus, startTime, endTime, kw, pageRequest);

        List<VoiceTaskVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @Override
    public Result<List<VoiceTaskVO>> getPendingTasks() {
        List<VoiceTask> tasks = voiceTaskRepository.findPendingTasks(TaskStatus.PENDING);
        List<VoiceTaskVO> list = tasks.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<VoiceTaskVO> getTaskByBizId(String bizId) {
        VoiceTask task = voiceTaskRepository.findByBizIdAndDeletedFalse(bizId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "任务不存在"));
        return Result.success(convertToVO(task));
    }

    /**
     * 转换为VO
     */
    private VoiceTaskVO convertToVO(VoiceTask task) {
        VoiceTaskVO vo = new VoiceTaskVO();
        vo.setTaskId(task.getId());
        vo.setTaskNo(task.getTaskNo());
        vo.setTaskType(task.getTaskType().getCode());
        vo.setTaskTypeDesc(task.getTaskType().getDesc());
        vo.setContent(task.getContent());
        vo.setTemplateId(task.getTemplateId());
        vo.setPriority(task.getPriority());
        vo.setStatus(task.getStatus().getCode());
        vo.setStatusDesc(task.getStatus().getDesc());
        vo.setPlayCount(task.getPlayCount());
        vo.setMaxPlayCount(task.getMaxPlayCount());
        vo.setScheduledTime(task.getScheduledTime());
        vo.setPlayStartTime(task.getPlayStartTime());
        vo.setPlayEndTime(task.getPlayEndTime());
        vo.setDuration(task.getDuration());
        vo.setErrorMessage(task.getErrorMessage());
        vo.setSpeed(task.getSpeed());
        vo.setVolume(task.getVolume());
        vo.setVoiceEngine(task.getVoiceEngine());
        vo.setVoiceName(task.getVoiceName());
        vo.setCreatorName(task.getCreatorName());
        vo.setCreateTime(task.getCreateTime());
        vo.setUpdateTime(task.getUpdateTime());
        vo.setBizId(task.getBizId());
        vo.setBizType(task.getBizType());
        return vo;
    }

    /**
     * 姓名脱敏
     */
    private String desensitizeName(String name) {
        if (StrUtil.isBlank(name) || name.length() <= 1) {
            return name;
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "*" + name.substring(name.length() - 1);
    }
}