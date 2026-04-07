package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.ScheduleRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 排班规则Repository
 */
@Repository
public interface ScheduleRuleRepository extends JpaRepository<ScheduleRule, String>, JpaSpecificationExecutor<ScheduleRule> {

    /**
     * 根据规则编码查找规则
     */
    Optional<ScheduleRule> findByRuleCode(String ruleCode);

    /**
     * 根据科室ID查找规则列表
     */
    List<ScheduleRule> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 查找全局规则（科室ID为空）
     */
    @Query("SELECT r FROM ScheduleRule r WHERE r.deleted = false AND r.deptId IS NULL")
    List<ScheduleRule> findGlobalRules();

    /**
     * 根据规则类型查找规则列表
     */
    List<ScheduleRule> findByRuleTypeAndDeletedFalse(String ruleType);

    /**
     * 根据状态查找规则列表
     */
    List<ScheduleRule> findByStatusAndDeletedFalseOrderByPriorityAsc(String status);

    /**
     * 查找科室规则（包括全局规则）
     */
    @Query("SELECT r FROM ScheduleRule r WHERE r.deleted = false " +
           "AND (r.deptId IS NULL OR r.deptId = :deptId) " +
           "AND r.status = '启用' " +
           "ORDER BY r.priority ASC")
    List<ScheduleRule> findRulesForDept(@Param("deptId") String deptId);

    /**
     * 检查规则编码是否存在
     */
    boolean existsByRuleCode(String ruleCode);
}