package com.yhj.his.module.voice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.voice.dto.VoiceTemplateRequest;
import com.yhj.his.module.voice.entity.VoiceTemplate;
import com.yhj.his.module.voice.enums.TemplateType;
import com.yhj.his.module.voice.repository.VoiceTemplateRepository;
import com.yhj.his.module.voice.service.VoiceTemplateService;
import com.yhj.his.module.voice.vo.VoiceTemplateVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 语音模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceTemplateServiceImpl implements VoiceTemplateService {

    private final VoiceTemplateRepository voiceTemplateRepository;

    @Override
    @Transactional
    public Result<VoiceTemplateVO> createTemplate(VoiceTemplateRequest request) {
        // 检查编码是否已存在
        if (voiceTemplateRepository.existsByTemplateCode(request.getTemplateCode())) {
            throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "模板编码已存在: " + request.getTemplateCode());
        }

        // 验证模板类型
        TemplateType templateType = Arrays.stream(TemplateType.values())
                .filter(t -> t.getCode().equals(request.getTemplateType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAM_ERROR, "无效的模板类型"));

        VoiceTemplate template = new VoiceTemplate();
        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(templateType);
        template.setContentTemplate(request.getContentTemplate());
        if (request.getParamsDefine() != null) {
            template.setParamsDefine(JSONUtil.toJsonStr(request.getParamsDefine()));
        }
        template.setVoiceEngine(request.getVoiceEngine());
        template.setVoiceName(request.getVoiceName());
        template.setSpeed(request.getSpeed() != null ? request.getSpeed() : 1.0);
        template.setVolume(request.getVolume() != null ? request.getVolume() : 80);
        template.setPitch(request.getPitch() != null ? request.getPitch() : 50);
        template.setPreAudio(request.getPreAudio());
        template.setPostAudio(request.getPostAudio());
        template.setIsSystem(false);
        template.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        template.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        template.setRemark(request.getRemark());

        VoiceTemplate saved = voiceTemplateRepository.save(template);
        log.info("创建语音模板成功: templateCode={}", saved.getTemplateCode());

        return Result.success("模板创建成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<VoiceTemplateVO> updateTemplate(String templateId, VoiceTemplateRequest request) {
        VoiceTemplate template = voiceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在"));

        // 系统模板不允许修改
        if (template.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统模板不允许修改");
        }

        // 如果修改编码，检查新编码是否已存在
        if (!template.getTemplateCode().equals(request.getTemplateCode())) {
            if (voiceTemplateRepository.existsByTemplateCode(request.getTemplateCode())) {
                throw new BusinessException(ErrorCode.DATA_ALREADY_EXISTS, "模板编码已存在: " + request.getTemplateCode());
            }
        }

        // 验证模板类型
        TemplateType templateType = Arrays.stream(TemplateType.values())
                .filter(t -> t.getCode().equals(request.getTemplateType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAM_ERROR, "无效的模板类型"));

        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(templateType);
        template.setContentTemplate(request.getContentTemplate());
        if (request.getParamsDefine() != null) {
            template.setParamsDefine(JSONUtil.toJsonStr(request.getParamsDefine()));
        }
        template.setVoiceEngine(request.getVoiceEngine());
        template.setVoiceName(request.getVoiceName());
        template.setSpeed(request.getSpeed());
        template.setVolume(request.getVolume());
        template.setPitch(request.getPitch());
        template.setPreAudio(request.getPreAudio());
        template.setPostAudio(request.getPostAudio());
        template.setIsEnabled(request.getIsEnabled());
        template.setSortOrder(request.getSortOrder());
        template.setRemark(request.getRemark());

        VoiceTemplate saved = voiceTemplateRepository.save(template);
        log.info("更新语音模板成功: templateId={}", templateId);

        return Result.success("模板更新成功", convertToVO(saved));
    }

    @Override
    @Transactional
    public Result<Void> deleteTemplate(String templateId) {
        VoiceTemplate template = voiceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在"));

        // 系统模板不允许删除
        if (template.getIsSystem()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "系统模板不允许删除");
        }

        template.setDeleted(true);
        voiceTemplateRepository.save(template);

        log.info("删除语音模板成功: templateId={}", templateId);
        return Result.successVoid();
    }

    @Override
    public Result<VoiceTemplateVO> getTemplateById(String templateId) {
        VoiceTemplate template = voiceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在"));
        return Result.success(convertToVO(template));
    }

    @Override
    public Result<VoiceTemplateVO> getTemplateByCode(String templateCode) {
        VoiceTemplate template = voiceTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在: " + templateCode));
        return Result.success(convertToVO(template));
    }

    @Override
    public Result<PageResult<VoiceTemplateVO>> getTemplateList(TemplateType templateType, Boolean isEnabled,
                                                               Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.ASC, "sortOrder"));

        List<VoiceTemplateVO> list;
        if (templateType != null) {
            List<VoiceTemplate> templates = voiceTemplateRepository.findByTemplateTypeAndDeletedFalse(templateType);
            list = templates.stream().map(this::convertToVO).collect(Collectors.toList());
        } else if (isEnabled != null && isEnabled) {
            List<VoiceTemplate> templates = voiceTemplateRepository.findEnabledTemplates();
            list = templates.stream().map(this::convertToVO).collect(Collectors.toList());
        } else {
            List<VoiceTemplate> templates = voiceTemplateRepository.findByDeletedFalseOrderBySortOrderAsc();
            list = templates.stream().map(this::convertToVO).collect(Collectors.toList());
        }

        return Result.success(PageResult.of(list, (long) list.size(), pageNum, pageSize));
    }

    @Override
    public Result<List<VoiceTemplateVO>> getEnabledTemplates() {
        List<VoiceTemplate> templates = voiceTemplateRepository.findEnabledTemplates();
        List<VoiceTemplateVO> list = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<List<VoiceTemplateVO>> getSystemTemplates() {
        List<VoiceTemplate> templates = voiceTemplateRepository.findSystemTemplates();
        List<VoiceTemplateVO> list = templates.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<String> generateContent(String templateCode, Map<String, Object> params) {
        VoiceTemplate template = voiceTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在: " + templateCode));

        String contentTemplate = template.getContentTemplate();
        String content = contentTemplate;

        // 替换模板参数
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                content = content.replace(placeholder, value);
            }
        }

        // 检查是否还有未替换的占位符
        if (content.contains("{") && content.contains("}")) {
            log.warn("模板参数未完全替换: templateCode={}, content={}", templateCode, content);
        }

        return Result.success(content);
    }

    @Override
    @Transactional
    public Result<Void> toggleTemplate(String templateId, Boolean enabled) {
        VoiceTemplate template = voiceTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "模板不存在"));

        template.setIsEnabled(enabled);
        voiceTemplateRepository.save(template);

        log.info("切换模板启用状态: templateId={}, enabled={}", templateId, enabled);
        return Result.successVoid();
    }

    /**
     * 转换为VO
     */
    private VoiceTemplateVO convertToVO(VoiceTemplate template) {
        VoiceTemplateVO vo = new VoiceTemplateVO();
        vo.setTemplateId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateType(template.getTemplateType().getCode());
        vo.setTemplateTypeDesc(template.getTemplateType().getDesc());
        vo.setContentTemplate(template.getContentTemplate());
        if (StrUtil.isNotBlank(template.getParamsDefine())) {
            cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(template.getParamsDefine());
            List<Map<String, Object>> paramsList = new java.util.ArrayList<>();
            for (Object item : jsonArray) {
                if (item instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    paramsList.add(map);
                }
            }
            vo.setParamsDefine(paramsList);
        }
        vo.setVoiceEngine(template.getVoiceEngine());
        vo.setVoiceName(template.getVoiceName());
        vo.setSpeed(template.getSpeed());
        vo.setVolume(template.getVolume());
        vo.setPitch(template.getPitch());
        vo.setPreAudio(template.getPreAudio());
        vo.setPostAudio(template.getPostAudio());
        vo.setIsSystem(template.getIsSystem());
        vo.setIsEnabled(template.getIsEnabled());
        vo.setSortOrder(template.getSortOrder());
        vo.setRemark(template.getRemark());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}