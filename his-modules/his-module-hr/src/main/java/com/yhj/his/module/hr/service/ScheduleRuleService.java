package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.ScheduleRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * 排班规则服务接口
 */
public interface ScheduleRuleService {

    /**
     * 创建排班规则
     */
    ScheduleRule createScheduleRule(ScheduleRule scheduleRule);

    /**
     * 更新排班规则
     */
    ScheduleRule updateScheduleRule(ScheduleRule scheduleRule);

    /**
     * 根据ID删除排班规则（逻辑删除）
     */
    void deleteScheduleRule(String id);

    /**
     * 根据ID获取排班规则
     */
    Optional<ScheduleRule> getScheduleRuleById(String id);

    /**
     * 根据规则编码获取排班规则
     */
    Optional<ScheduleRule> getScheduleRuleByCode(String ruleCode);

    /**
     * 根据科室ID获取排班规则列表
     */
    List<ScheduleRule> getScheduleRulesByDeptId(String deptId);

    /**
     * 获取全局排班规则列表（科室ID为空）
     */
    List<ScheduleRule> getGlobalScheduleRules();

    /**
     * 根据规则类型获取排班规则列表
     */
    List<ScheduleRule> getScheduleRulesByType(String ruleType);

    /**
     * 根据状态获取排班规则列表
     */
    List<ScheduleRule> getScheduleRulesByStatus(String status);

    /**
     * 获取科室排班规则（包括全局规则）
     */
    List<ScheduleRule> getScheduleRulesForDept(String deptId);

    /**
     * 检查规则编码是否存在
     */
    boolean existsByRuleCode(String ruleCode);

    /**
     * 启用排班规则
     */
    ScheduleRule enableScheduleRule(String scheduleRuleId);

    /**
     * 禁用排班规则
     */
    ScheduleRule disableScheduleRule(String scheduleRuleId);

    /**
     * 批量创建排班规则
     */
    List<ScheduleRule> batchCreateScheduleRules(List<ScheduleRule> scheduleRules);

    /**
     * 生成规则编码
     */
    String generateRuleCode(String ruleType);

    /**
     * 获取规则值（根据规则编码）
     */
    String getRuleValue(String deptId, String ruleType);

    /**
     * 验证规则值是否合法
     */
    boolean validateRuleValue(String ruleType, String ruleValue);
}