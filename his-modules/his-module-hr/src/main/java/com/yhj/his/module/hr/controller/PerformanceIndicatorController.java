package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.PerformanceIndicator;
import com.yhj.his.module.hr.enums.IndicatorType;
import com.yhj.his.module.hr.repository.PerformanceIndicatorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 绩效指标管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/performance-indicators")
@Tag(name = "绩效指标管理", description = "绩效指标管理相关接口")
@RequiredArgsConstructor
public class PerformanceIndicatorController {

    private final PerformanceIndicatorRepository performanceIndicatorRepository;

    @PostMapping
    @Operation(summary = "创建绩效指标", description = "新增绩效指标")
    public Result<PerformanceIndicatorVO> createIndicator(@Valid @RequestBody PerformanceIndicatorCreateDTO dto) {
        PerformanceIndicator indicator = new PerformanceIndicator();
        indicator.setIndicatorName(dto.getIndicatorName());
        indicator.setIndicatorCode(dto.getIndicatorCode());
        indicator.setIndicatorType(IndicatorType.valueOf(dto.getIndicatorType()));
        indicator.setDeptId(dto.getDeptId());
        indicator.setUnit(dto.getUnit());
        indicator.setMaxScore(dto.getMaxScore());
        indicator.setWeight(dto.getWeight());
        indicator.setTargetValue(dto.getTargetValue());
        indicator.setDescription(dto.getDescription());
        indicator.setCalcRule(dto.getCalcRule());
        indicator.setSortOrder(dto.getSortOrder());
        indicator.setStatus(dto.getStatus() != null ? dto.getStatus() : "启用");
        indicator.setRemark(dto.getRemark());

        PerformanceIndicator saved = performanceIndicatorRepository.save(indicator);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新绩效指标", description = "更新绩效指标信息")
    public Result<PerformanceIndicatorVO> updateIndicator(
            @Parameter(description = "指标ID") @PathVariable String id,
            @Valid @RequestBody PerformanceIndicatorCreateDTO dto) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在"));

        indicator.setIndicatorName(dto.getIndicatorName());
        if (dto.getIndicatorType() != null) {
            indicator.setIndicatorType(IndicatorType.valueOf(dto.getIndicatorType()));
        }
        indicator.setDeptId(dto.getDeptId());
        indicator.setUnit(dto.getUnit());
        indicator.setMaxScore(dto.getMaxScore());
        indicator.setWeight(dto.getWeight());
        indicator.setTargetValue(dto.getTargetValue());
        indicator.setDescription(dto.getDescription());
        indicator.setCalcRule(dto.getCalcRule());
        indicator.setSortOrder(dto.getSortOrder());
        indicator.setStatus(dto.getStatus());
        indicator.setRemark(dto.getRemark());

        PerformanceIndicator saved = performanceIndicatorRepository.save(indicator);
        return Result.success(convertToVO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除绩效指标", description = "删除绩效指标(逻辑删除)")
    public Result<Void> deleteIndicator(@Parameter(description = "指标ID") @PathVariable String id) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在"));
        indicator.setDeleted(true);
        performanceIndicatorRepository.save(indicator);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取绩效指标详情", description = "根据ID获取绩效指标详细信息")
    public Result<PerformanceIndicatorVO> getIndicator(@Parameter(description = "指标ID") @PathVariable String id) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(id)
                .filter(i -> !i.getDeleted())
                .orElseThrow(() -> new RuntimeException("绩效指标不存在"));
        return Result.success(convertToVO(indicator));
    }

    @GetMapping("/code/{indicatorCode}")
    @Operation(summary = "根据编码获取绩效指标", description = "根据指标编码获取绩效指标")
    public Result<PerformanceIndicatorVO> getIndicatorByCode(
            @Parameter(description = "指标编码") @PathVariable String indicatorCode) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findByIndicatorCode(indicatorCode)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在"));
        return Result.success(convertToVO(indicator));
    }

    @GetMapping
    @Operation(summary = "分页查询绩效指标", description = "分页查询绩效指标列表")
    public Result<PageResult<PerformanceIndicatorVO>> listIndicators(
            @Parameter(description = "指标类型") @RequestParam(required = false) String indicatorType,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("sortOrder").ascending());
        IndicatorType type = indicatorType != null ? IndicatorType.valueOf(indicatorType) : null;

        Page<PerformanceIndicator> page = performanceIndicatorRepository.findByConditions(type, deptId, status, keyword, pageable);

        List<PerformanceIndicatorVO> voList = page.getContent().stream()
                .filter(i -> !i.getDeleted())
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<PerformanceIndicatorVO> result = PageResult.of(voList, page.getTotalElements(), pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/type/{indicatorType}")
    @Operation(summary = "根据类型查询绩效指标", description = "查询指定类型的绩效指标列表")
    public Result<List<PerformanceIndicatorVO>> listIndicatorsByType(
            @Parameter(description = "指标类型") @PathVariable String indicatorType) {
        IndicatorType type = IndicatorType.valueOf(indicatorType);
        List<PerformanceIndicator> indicators = performanceIndicatorRepository.findByIndicatorTypeAndDeletedFalseOrderBySortOrderAsc(type);
        List<PerformanceIndicatorVO> voList = indicators.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "根据科室查询绩效指标", description = "查询指定科室的绩效指标列表")
    public Result<List<PerformanceIndicatorVO>> listIndicatorsByDept(
            @Parameter(description = "科室ID") @PathVariable String deptId) {
        List<PerformanceIndicator> indicators = performanceIndicatorRepository.findByDeptIdAndDeletedFalseOrderBySortOrderAsc(deptId);
        List<PerformanceIndicatorVO> voList = indicators.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/general")
    @Operation(summary = "获取通用绩效指标", description = "查询通用绩效指标列表(科室ID为空)")
    public Result<List<PerformanceIndicatorVO>> listGeneralIndicators() {
        List<PerformanceIndicator> indicators = performanceIndicatorRepository.findCommonIndicators();
        List<PerformanceIndicatorVO> voList = indicators.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private PerformanceIndicatorVO convertToVO(PerformanceIndicator indicator) {
        PerformanceIndicatorVO vo = new PerformanceIndicatorVO();
        vo.setId(indicator.getId());
        vo.setIndicatorName(indicator.getIndicatorName());
        vo.setIndicatorCode(indicator.getIndicatorCode());
        vo.setIndicatorType(indicator.getIndicatorType() != null ? indicator.getIndicatorType().name() : null);
        vo.setDeptId(indicator.getDeptId());
        vo.setDeptName(indicator.getDeptName());
        vo.setUnit(indicator.getUnit());
        vo.setMaxScore(indicator.getMaxScore());
        vo.setWeight(indicator.getWeight());
        vo.setTargetValue(indicator.getTargetValue());
        vo.setDescription(indicator.getDescription());
        vo.setCalcRule(indicator.getCalcRule());
        vo.setSortOrder(indicator.getSortOrder());
        vo.setStatus(indicator.getStatus());
        vo.setRemark(indicator.getRemark());
        vo.setCreateTime(indicator.getCreateTime());
        vo.setUpdateTime(indicator.getUpdateTime());
        return vo;
    }
}