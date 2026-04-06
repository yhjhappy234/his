package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.EmrTemplateSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 病历模板服务接口
 */
public interface EmrTemplateService {

    /**
     * 创建模板
     */
    EmrTemplate createTemplate(EmrTemplateSaveDTO dto);

    /**
     * 更新模板
     */
    EmrTemplate updateTemplate(String id, EmrTemplateSaveDTO dto);

    /**
     * 删除模板
     */
    void deleteTemplate(String id);

    /**
     * 根据ID获取模板
     */
    EmrTemplate getTemplateById(String id);

    /**
     * 分页查询模板
     */
    Page<EmrTemplate> listTemplates(Pageable pageable);

    /**
     * 根据模板名称模糊查询
     */
    Page<EmrTemplate> searchByTemplateName(String name, Pageable pageable);

    /**
     * 根据模板类型查询可用模板
     */
    List<EmrTemplate> getAvailableTemplates(TemplateType type, String deptId, String creatorId);

    /**
     * 根据模板类型查询启用模板
     */
    List<EmrTemplate> getTemplatesByType(TemplateType templateType);

    /**
     * 根据科室查询模板
     */
    List<EmrTemplate> getTemplatesByDeptId(String deptId);

    /**
     * 查询公开模板
     */
    List<EmrTemplate> getPublicTemplates();

    /**
     * 根据创建人查询模板
     */
    List<EmrTemplate> getTemplatesByCreatorId(String creatorId);

    /**
     * 启用/禁用模板
     */
    EmrTemplate toggleTemplateStatus(String id, boolean enabled);

    /**
     * 设置模板公开状态
     */
    EmrTemplate toggleTemplatePublic(String id, boolean isPublic);

    /**
     * 增加使用次数
     */
    void incrementUseCount(String id);
}