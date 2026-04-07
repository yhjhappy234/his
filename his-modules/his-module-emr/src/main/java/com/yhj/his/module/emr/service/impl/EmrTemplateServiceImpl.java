package com.yhj.his.module.emr.service.impl;

import com.yhj.his.module.emr.dto.EmrTemplateSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.TemplateType;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.service.EmrTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 病历模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmrTemplateServiceImpl implements EmrTemplateService {

    private final EmrTemplateRepository templateRepository;

    @Override
    @Transactional
    public EmrTemplate createTemplate(EmrTemplateSaveDTO dto) {
        EmrTemplate template = new EmrTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setTemplateType(dto.getTemplateType());
        template.setCategory(dto.getCategory());
        template.setTemplateContent(dto.getTemplateContent());
        template.setDeptId(dto.getDeptId());
        template.setDeptName(dto.getDeptName());
        template.setCreatorId(dto.getCreatorId());
        template.setCreatorName(dto.getCreatorName());
        template.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false);
        template.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        template.setUseCount(0);
        template.setRemark(dto.getRemark());
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public EmrTemplate updateTemplate(String id, EmrTemplateSaveDTO dto) {
        EmrTemplate template = getTemplateById(id);
        template.setTemplateName(dto.getTemplateName());
        if (dto.getTemplateType() != null) {
            template.setTemplateType(dto.getTemplateType());
        }
        template.setCategory(dto.getCategory());
        template.setTemplateContent(dto.getTemplateContent());
        template.setDeptId(dto.getDeptId());
        template.setDeptName(dto.getDeptName());
        if (dto.getIsPublic() != null) {
            template.setIsPublic(dto.getIsPublic());
        }
        if (dto.getIsEnabled() != null) {
            template.setIsEnabled(dto.getIsEnabled());
        }
        template.setRemark(dto.getRemark());
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(String id) {
        EmrTemplate template = getTemplateById(id);
        template.setDeleted(true);
        templateRepository.save(template);
    }

    @Override
    public EmrTemplate getTemplateById(String id) {
        return templateRepository.findById(id)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new RuntimeException("模板不存在: " + id));
    }

    @Override
    public Page<EmrTemplate> listTemplates(Pageable pageable) {
        return templateRepository.findByDeletedFalse(pageable);
    }

    @Override
    public Page<EmrTemplate> searchByTemplateName(String name, Pageable pageable) {
        return templateRepository.findByTemplateNameContainingAndDeletedFalse(name, pageable);
    }

    @Override
    public List<EmrTemplate> getAvailableTemplates(TemplateType type, String deptId, String creatorId) {
        return templateRepository.findAvailableTemplates(type, deptId, creatorId);
    }

    @Override
    public List<EmrTemplate> getTemplatesByType(TemplateType templateType) {
        return templateRepository.findByTemplateTypeAndIsEnabledTrueAndDeletedFalse(templateType);
    }

    @Override
    public List<EmrTemplate> getTemplatesByDeptId(String deptId) {
        return templateRepository.findByDeptIdAndIsEnabledTrueAndDeletedFalse(deptId);
    }

    @Override
    public List<EmrTemplate> getPublicTemplates() {
        return templateRepository.findByIsPublicTrueAndIsEnabledTrueAndDeletedFalse();
    }

    @Override
    public List<EmrTemplate> getTemplatesByCreatorId(String creatorId) {
        return templateRepository.findByCreatorIdAndDeletedFalse(creatorId);
    }

    @Override
    @Transactional
    public EmrTemplate toggleTemplateStatus(String id, boolean enabled) {
        EmrTemplate template = getTemplateById(id);
        template.setIsEnabled(enabled);
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public EmrTemplate toggleTemplatePublic(String id, boolean isPublic) {
        EmrTemplate template = getTemplateById(id);
        template.setIsPublic(isPublic);
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public void incrementUseCount(String id) {
        EmrTemplate template = getTemplateById(id);
        template.setUseCount(template.getUseCount() + 1);
        templateRepository.save(template);
    }
}