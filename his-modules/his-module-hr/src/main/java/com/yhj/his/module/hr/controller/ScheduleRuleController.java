package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.ScheduleRule;
import com.yhj.his.module.hr.repository.ScheduleRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 排班规则管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/schedule-rules")
@Tag(name = "排班规则管理", description = "排班规则管理相关接口")
@RequiredArgsConstructor
public class ScheduleRuleController {

    private final ScheduleRuleRepository scheduleRuleRepository;

    @PostMapping
    @Operation(summary = "创建排班规则", description = "新增排班规则")
    public Result<ScheduleRuleVO> createRule(@Valid @RequestBody ScheduleRuleCreateDTO dto) {
        ScheduleRule rule = new ScheduleRule();
        rule.setRuleName(dto.getRuleName());
        rule.setRuleCode(dto.getRuleCode());
        rule.setDeptId(dto.getDeptId());
        rule.setRuleType(dto.getRuleType());
        rule.setRuleValue(dto.getRuleValue());
        rule.setRuleUnit(dto.getRuleUnit());
        rule.setDescription(dto.getDescription());
        rule.setPriority(dto.getPriority());
        rule.setStatus(dto.getStatus() != null ? dto.getStatus() : "启用");
        rule.setRemark(dto.getRemark());

        ScheduleRule saved = scheduleRuleRepository.save(rule);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新排班规则", description = "更新排班规则信息")
    public Result<ScheduleRuleVO> updateRule(
            @Parameter(description = "规则ID") @PathVariable String id,
            @Valid @RequestBody ScheduleRuleCreateDTO dto) {
        ScheduleRule rule = scheduleRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班规则不存在"));

        rule.setRuleName(dto.getRuleName());
        rule.setDeptId(dto.getDeptId());
        rule.setRuleType(dto.getRuleType());
        rule.setRuleValue(dto.getRuleValue());
        rule.setRuleUnit(dto.getRuleUnit());
        rule.setDescription(dto.getDescription());
        rule.setPriority(dto.getPriority());
        rule.setStatus(dto.getStatus());
        rule.setRemark(dto.getRemark());

        ScheduleRule saved = scheduleRuleRepository.save(rule);
        return Result.success(convertToVO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除排班规则", description = "删除排班规则(逻辑删除)")
    public Result<Void> deleteRule(@Parameter(description = "规则ID") @PathVariable String id) {
        ScheduleRule rule = scheduleRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班规则不存在"));
        rule.setDeleted(true);
        scheduleRuleRepository.save(rule);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取排班规则详情", description = "根据ID获取排班规则详细信息")
    public Result<ScheduleRuleVO> getRule(@Parameter(description = "规则ID") @PathVariable String id) {
        ScheduleRule rule = scheduleRuleRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new RuntimeException("排班规则不存在"));
        return Result.success(convertToVO(rule));
    }

    @GetMapping("/code/{ruleCode}")
    @Operation(summary = "根据编码获取排班规则", description = "根据规则编码获取排班规则")
    public Result<ScheduleRuleVO> getRuleByCode(
            @Parameter(description = "规则编码") @PathVariable String ruleCode) {
        ScheduleRule rule = scheduleRuleRepository.findByRuleCode(ruleCode)
                .orElseThrow(() -> new RuntimeException("排班规则不存在"));
        return Result.success(convertToVO(rule));
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据科室查询排班规则", description = "查询指定科室的排班规则列表")
    public Result<List<ScheduleRuleVO>> listRulesByDept(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        List<ScheduleRule> rules = scheduleRuleRepository.findByDeptIdAndDeletedFalse(deptId);
        List<ScheduleRuleVO> voList = rules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/global")
    @Operation(summary = "获取全局排班规则", description = "查询全局排班规则列表")
    public Result<List<ScheduleRuleVO>> listGlobalRules() {
        List<ScheduleRule> rules = scheduleRuleRepository.findGlobalRules();
        List<ScheduleRuleVO> voList = rules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/type/{ruleType}")
    @Operation(summary = "根据类型查询排班规则", description = "查询指定类型的排班规则列表")
    public Result<List<ScheduleRuleVO>> listRulesByType(
            @Parameter(description = "规则类型") @PathVariable String ruleType) {
        List<ScheduleRule> rules = scheduleRuleRepository.findByRuleTypeAndDeletedFalse(ruleType);
        List<ScheduleRuleVO> voList = rules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private ScheduleRuleVO convertToVO(ScheduleRule rule) {
        ScheduleRuleVO vo = new ScheduleRuleVO();
        vo.setId(rule.getId());
        vo.setRuleName(rule.getRuleName());
        vo.setRuleCode(rule.getRuleCode());
        vo.setDeptId(rule.getDeptId());
        vo.setDeptName(rule.getDeptName());
        vo.setRuleType(rule.getRuleType());
        vo.setRuleValue(rule.getRuleValue());
        vo.setRuleUnit(rule.getRuleUnit());
        vo.setDescription(rule.getDescription());
        vo.setPriority(rule.getPriority());
        vo.setStatus(rule.getStatus());
        vo.setRemark(rule.getRemark());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateTime(rule.getUpdateTime());
        return vo;
    }
}