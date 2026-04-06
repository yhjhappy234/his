package com.yhj.his.module.voice.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.voice.dto.VoiceTemplateRequest;
import com.yhj.his.module.voice.enums.TemplateType;
import com.yhj.his.module.voice.vo.VoiceTemplateVO;

import java.util.List;
import java.util.Map;

/**
 * 语音模板服务接口
 */
public interface VoiceTemplateService {

    /**
     * 创建模板
     */
    Result<VoiceTemplateVO> createTemplate(VoiceTemplateRequest request);

    /**
     * 更新模板
     */
    Result<VoiceTemplateVO> updateTemplate(String templateId, VoiceTemplateRequest request);

    /**
     * 删除模板
     */
    Result<Void> deleteTemplate(String templateId);

    /**
     * 根据ID查询模板
     */
    Result<VoiceTemplateVO> getTemplateById(String templateId);

    /**
     * 根据编码查询模板
     */
    Result<VoiceTemplateVO> getTemplateByCode(String templateCode);

    /**
     * 分页查询模板列表
     */
    Result<PageResult<VoiceTemplateVO>> getTemplateList(TemplateType templateType, Boolean isEnabled,
                                                        Integer pageNum, Integer pageSize);

    /**
     * 查询所有启用的模板
     */
    Result<List<VoiceTemplateVO>> getEnabledTemplates();

    /**
     * 查询系统预置模板
     */
    Result<List<VoiceTemplateVO>> getSystemTemplates();

    /**
     * 根据模板生成播报内容
     */
    Result<String> generateContent(String templateCode, Map<String, Object> params);

    /**
     * 启用/禁用模板
     */
    Result<Void> toggleTemplate(String templateId, Boolean enabled);
}