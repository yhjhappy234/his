package com.yhj.his.module.hr.service.impl;

import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.ScheduleTemplate;
import com.yhj.his.module.hr.enums.ScheduleType;
import com.yhj.his.module.hr.repository.ScheduleTemplateRepository;
import com.yhj.his.module.hr.repository.HrDepartmentRepository;
import com.yhj.his.module.hr.service.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 排班模板服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {

    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final HrDepartmentRepository departmentRepository;

    @Override
    public ScheduleTemplate createScheduleTemplate(ScheduleTemplate scheduleTemplate) {
        // 获取科室信息
        if (scheduleTemplate.getDeptId() != null) {
            departmentRepository.findById(scheduleTemplate.getDeptId()).ifPresent(dept -> {
                scheduleTemplate.setDeptName(dept.getDeptName());
            });
        }

        // 默认状态为启用
        scheduleTemplate.setStatus("启用");

        return scheduleTemplateRepository.save(scheduleTemplate);
    }

    @Override
    public ScheduleTemplate updateScheduleTemplate(ScheduleTemplate scheduleTemplate) {
        ScheduleTemplate existing = scheduleTemplateRepository.findById(scheduleTemplate.getId())
                .orElseThrow(() -> new RuntimeException("排班模板不存在: " + scheduleTemplate.getId()));

        updateTemplateFromEntity(scheduleTemplate, existing);

        // 更新科室名称
        if (scheduleTemplate.getDeptId() != null) {
            departmentRepository.findById(scheduleTemplate.getDeptId()).ifPresent(dept -> {
                existing.setDeptName(dept.getDeptName());
            });
        }

        return scheduleTemplateRepository.save(existing);
    }

    @Override
    public void deleteScheduleTemplate(String id) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班模板不存在: " + id));
        template.setDeleted(true);
        scheduleTemplateRepository.save(template);
    }

    @Override
    public Optional<ScheduleTemplate> getScheduleTemplateById(String id) {
        return scheduleTemplateRepository.findById(id)
                .filter(t -> !t.getDeleted());
    }

    @Override
    public Optional<ScheduleTemplate> getScheduleTemplateByCode(String templateCode) {
        return scheduleTemplateRepository.findByTemplateCode(templateCode)
                .filter(t -> !t.getDeleted());
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplatesByDeptId(String deptId) {
        return scheduleTemplateRepository.findByDeptIdAndDeletedFalse(deptId);
    }

    @Override
    public List<ScheduleTemplate> getGlobalScheduleTemplates() {
        return scheduleTemplateRepository.findGlobalTemplates();
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplatesByWeekDay(Integer weekDay) {
        return scheduleTemplateRepository.findByWeekDayAndDeletedFalse(weekDay);
    }

    @Override
    public Optional<ScheduleTemplate> getScheduleTemplateByDeptAndWeekDay(String deptId, Integer weekDay) {
        return scheduleTemplateRepository.findByDeptIdAndWeekDayAndDeletedFalse(deptId, weekDay);
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplatesByStatus(String status) {
        return scheduleTemplateRepository.findByStatusAndDeletedFalse(status);
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplatesForDept(String deptId) {
        return scheduleTemplateRepository.findTemplatesForDept(deptId);
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        return scheduleTemplateRepository.existsByTemplateCode(templateCode);
    }

    @Override
    public ScheduleTemplate enableScheduleTemplate(String scheduleTemplateId) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(scheduleTemplateId)
                .orElseThrow(() -> new RuntimeException("排班模板不存在: " + scheduleTemplateId));

        template.setStatus("启用");
        ScheduleTemplate saved = scheduleTemplateRepository.save(template);
        return saved;
    }

    @Override
    public ScheduleTemplate disableScheduleTemplate(String scheduleTemplateId) {
        ScheduleTemplate template = scheduleTemplateRepository.findById(scheduleTemplateId)
                .orElseThrow(() -> new RuntimeException("排班模板不存在: " + scheduleTemplateId));

        template.setStatus("禁用");
        ScheduleTemplate saved = scheduleTemplateRepository.save(template);
        return saved;
    }

    @Override
    public List<ScheduleTemplate> createWeeklyTemplates(String deptId, List<ScheduleType> scheduleTypes) {
        List<ScheduleTemplate> templates = new ArrayList<>();

        // 获取科室名称
        String deptName = null;
        if (deptId != null) {
            deptName = departmentRepository.findById(deptId)
                    .map(d -> d.getDeptName())
                    .orElse(null);
        }

        // 为一周七天创建模板
        for (int weekDay = 1; weekDay <= 7; weekDay++) {
            ScheduleTemplate template = new ScheduleTemplate();
            template.setTemplateName("周" + weekDay + "排班模板");
            template.setTemplateCode(generateTemplateCode(deptId));
            template.setDeptId(deptId);
            template.setDeptName(deptName);
            template.setWeekDay(weekDay);

            // 设置班次类型
            if (weekDay <= 5 && scheduleTypes.size() > 0) {
                template.setScheduleType(scheduleTypes.get(0));
            } else if (scheduleTypes.size() > 1) {
                template.setScheduleType(scheduleTypes.get(1));
            } else if (!scheduleTypes.isEmpty()) {
                template.setScheduleType(scheduleTypes.get(0));
            }

            template.setStatus("启用");
            templates.add(template);
        }

        return scheduleTemplateRepository.saveAll(templates);
    }

    @Override
    public String generateTemplateCode(String deptId) {
        return "TM" + System.currentTimeMillis();
    }

    @Override
    public List<ScheduleTemplate> copyTemplatesToDept(String sourceDeptId, String targetDeptId) {
        List<ScheduleTemplate> sourceTemplates = scheduleTemplateRepository.findByDeptIdAndDeletedFalse(sourceDeptId);

        // 获取目标科室名称
        String targetDeptName = departmentRepository.findById(targetDeptId)
                .map(d -> d.getDeptName())
                .orElse(null);

        List<ScheduleTemplate> newTemplates = new ArrayList<>();
        for (ScheduleTemplate source : sourceTemplates) {
            ScheduleTemplate newTemplate = new ScheduleTemplate();
            newTemplate.setTemplateName(source.getTemplateName());
            newTemplate.setTemplateCode(generateTemplateCode(targetDeptId));
            newTemplate.setDeptId(targetDeptId);
            newTemplate.setDeptName(targetDeptName);
            newTemplate.setWeekDay(source.getWeekDay());
            newTemplate.setScheduleType(source.getScheduleType());
            newTemplate.setStartTime(source.getStartTime());
            newTemplate.setEndTime(source.getEndTime());
            newTemplate.setLocation(source.getLocation());
            newTemplate.setDescription(source.getDescription());
            newTemplate.setStatus("启用");
            newTemplates.add(newTemplate);
        }

        return scheduleTemplateRepository.saveAll(newTemplates);
    }

    @Override
    public List<ScheduleTemplate> getWeeklyTemplates(String deptId) {
        return scheduleTemplateRepository.findTemplatesForDept(deptId);
    }

    private void updateTemplateFromEntity(ScheduleTemplate source, ScheduleTemplate target) {
        if (source.getTemplateName() != null) target.setTemplateName(source.getTemplateName());
        if (source.getDeptId() != null) target.setDeptId(source.getDeptId());
        if (source.getWeekDay() != null) target.setWeekDay(source.getWeekDay());
        if (source.getScheduleType() != null) target.setScheduleType(source.getScheduleType());
        if (source.getStartTime() != null) target.setStartTime(source.getStartTime());
        if (source.getEndTime() != null) target.setEndTime(source.getEndTime());
        if (source.getLocation() != null) target.setLocation(source.getLocation());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getStatus() != null) target.setStatus(source.getStatus());
        if (source.getRemark() != null) target.setRemark(source.getRemark());
    }
}