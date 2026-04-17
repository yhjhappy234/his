package com.yhj.his.module.hr.service.impl;

import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.ScheduleRule;
import com.yhj.his.module.hr.repository.ScheduleRuleRepository;
import com.yhj.his.module.hr.repository.HrDepartmentRepository;
import com.yhj.his.module.hr.service.ScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 排班规则服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleRuleServiceImpl implements ScheduleRuleService {

    private final ScheduleRuleRepository scheduleRuleRepository;
    private final HrDepartmentRepository departmentRepository;

    @Override
    public ScheduleRule createScheduleRule(ScheduleRule scheduleRule) {
        // 获取科室信息
        if (scheduleRule.getDeptId() != null) {
            departmentRepository.findById(scheduleRule.getDeptId()).ifPresent(dept -> {
                scheduleRule.setDeptName(dept.getDeptName());
            });
        }

        // 默认状态为启用
        scheduleRule.setStatus("启用");

        return scheduleRuleRepository.save(scheduleRule);
    }

    @Override
    public ScheduleRule updateScheduleRule(ScheduleRule scheduleRule) {
        ScheduleRule existing = scheduleRuleRepository.findById(scheduleRule.getId())
                .orElseThrow(() -> new RuntimeException("排班规则不存在: " + scheduleRule.getId()));

        updateRuleFromEntity(scheduleRule, existing);

        // 更新科室名称
        if (scheduleRule.getDeptId() != null) {
            departmentRepository.findById(scheduleRule.getDeptId()).ifPresent(dept -> {
                existing.setDeptName(dept.getDeptName());
            });
        }

        return scheduleRuleRepository.save(existing);
    }

    @Override
    public void deleteScheduleRule(String id) {
        ScheduleRule rule = scheduleRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班规则不存在: " + id));
        rule.setDeleted(true);
        scheduleRuleRepository.save(rule);
    }

    @Override
    public Optional<ScheduleRule> getScheduleRuleById(String id) {
        return scheduleRuleRepository.findById(id)
                .filter(r -> !r.getDeleted());
    }

    @Override
    public Optional<ScheduleRule> getScheduleRuleByCode(String ruleCode) {
        return scheduleRuleRepository.findByRuleCode(ruleCode)
                .filter(r -> !r.getDeleted());
    }

    @Override
    public List<ScheduleRule> getScheduleRulesByDeptId(String deptId) {
        return scheduleRuleRepository.findByDeptIdAndDeletedFalse(deptId);
    }

    @Override
    public List<ScheduleRule> getGlobalScheduleRules() {
        return scheduleRuleRepository.findGlobalRules();
    }

    @Override
    public List<ScheduleRule> getScheduleRulesByType(String ruleType) {
        return scheduleRuleRepository.findByRuleTypeAndDeletedFalse(ruleType);
    }

    @Override
    public List<ScheduleRule> getScheduleRulesByStatus(String status) {
        return scheduleRuleRepository.findByStatusAndDeletedFalseOrderByPriorityAsc(status);
    }

    @Override
    public List<ScheduleRule> getScheduleRulesForDept(String deptId) {
        return scheduleRuleRepository.findRulesForDept(deptId);
    }

    @Override
    public boolean existsByRuleCode(String ruleCode) {
        return scheduleRuleRepository.existsByRuleCode(ruleCode);
    }

    @Override
    public ScheduleRule enableScheduleRule(String scheduleRuleId) {
        ScheduleRule rule = scheduleRuleRepository.findById(scheduleRuleId)
                .orElseThrow(() -> new RuntimeException("排班规则不存在: " + scheduleRuleId));

        rule.setStatus("启用");
        ScheduleRule saved = scheduleRuleRepository.save(rule);
        return saved;
    }

    @Override
    public ScheduleRule disableScheduleRule(String scheduleRuleId) {
        ScheduleRule rule = scheduleRuleRepository.findById(scheduleRuleId)
                .orElseThrow(() -> new RuntimeException("排班规则不存在: " + scheduleRuleId));

        rule.setStatus("禁用");
        ScheduleRule saved = scheduleRuleRepository.save(rule);
        return saved;
    }

    @Override
    public List<ScheduleRule> batchCreateScheduleRules(List<ScheduleRule> scheduleRules) {
        return scheduleRuleRepository.saveAll(scheduleRules);
    }

    @Override
    public String generateRuleCode(String ruleType) {
        return ruleType.substring(0, 2) + System.currentTimeMillis();
    }

    @Override
    public String getRuleValue(String deptId, String ruleType) {
        List<ScheduleRule> rules = scheduleRuleRepository.findRulesForDept(deptId);
        return rules.stream()
                .filter(r -> r.getRuleType().equals(ruleType))
                .findFirst()
                .map(ScheduleRule::getRuleValue)
                .orElse(null);
    }

    @Override
    public boolean validateRuleValue(String ruleType, String ruleValue) {
        // 根据规则类型验证规则值
        if (ruleValue == null || ruleValue.isEmpty()) {
            return false;
        }

        try {
            if ("工时规则".equals(ruleType) || "夜班规则".equals(ruleType)) {
                // 验证数值类型
                Integer.parseInt(ruleValue);
                return true;
            } else if ("休息规则".equals(ruleType)) {
                // 验证天数
                Integer.parseInt(ruleValue);
                return true;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateRuleFromEntity(ScheduleRule source, ScheduleRule target) {
        if (source.getRuleName() != null) target.setRuleName(source.getRuleName());
        if (source.getDeptId() != null) target.setDeptId(source.getDeptId());
        if (source.getRuleType() != null) target.setRuleType(source.getRuleType());
        if (source.getRuleValue() != null) target.setRuleValue(source.getRuleValue());
        if (source.getRuleUnit() != null) target.setRuleUnit(source.getRuleUnit());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getPriority() != null) target.setPriority(source.getPriority());
        if (source.getStatus() != null) target.setStatus(source.getStatus());
        if (source.getRemark() != null) target.setRemark(source.getRemark());
    }
}