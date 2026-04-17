package com.yhj.his.module.hr.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.PerformanceIndicator;
import com.yhj.his.module.hr.enums.IndicatorType;
import com.yhj.his.module.hr.repository.PerformanceIndicatorRepository;
import com.yhj.his.module.hr.repository.HrDepartmentRepository;
import com.yhj.his.module.hr.service.PerformanceIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 绩效指标服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceIndicatorServiceImpl implements PerformanceIndicatorService {

    private final PerformanceIndicatorRepository performanceIndicatorRepository;
    private final HrDepartmentRepository departmentRepository;

    @Override
    public PerformanceIndicator createPerformanceIndicator(PerformanceIndicator indicator) {
        // 获取科室信息
        if (indicator.getDeptId() != null) {
            departmentRepository.findById(indicator.getDeptId()).ifPresent(dept -> {
                indicator.setDeptName(dept.getDeptName());
            });
        }

        // 默认状态为启用
        indicator.setStatus("启用");

        return performanceIndicatorRepository.save(indicator);
    }

    @Override
    public PerformanceIndicator updatePerformanceIndicator(PerformanceIndicator indicator) {
        PerformanceIndicator existing = performanceIndicatorRepository.findById(indicator.getId())
                .orElseThrow(() -> new RuntimeException("绩效指标不存在: " + indicator.getId()));

        updateIndicatorFromEntity(indicator, existing);

        // 更新科室名称
        if (indicator.getDeptId() != null) {
            departmentRepository.findById(indicator.getDeptId()).ifPresent(dept -> {
                existing.setDeptName(dept.getDeptName());
            });
        }

        return performanceIndicatorRepository.save(existing);
    }

    @Override
    public void deletePerformanceIndicator(String id) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在: " + id));
        indicator.setDeleted(true);
        performanceIndicatorRepository.save(indicator);
    }

    @Override
    public Optional<PerformanceIndicator> getPerformanceIndicatorById(String id) {
        return performanceIndicatorRepository.findById(id)
                .filter(p -> !p.getDeleted());
    }

    @Override
    public Optional<PerformanceIndicator> getPerformanceIndicatorByCode(String indicatorCode) {
        return performanceIndicatorRepository.findByIndicatorCode(indicatorCode)
                .filter(p -> !p.getDeleted());
    }

    @Override
    public List<PerformanceIndicator> getPerformanceIndicatorsByType(IndicatorType indicatorType) {
        return performanceIndicatorRepository.findByIndicatorTypeAndDeletedFalseOrderBySortOrderAsc(indicatorType);
    }

    @Override
    public List<PerformanceIndicator> getPerformanceIndicatorsByDeptId(String deptId) {
        return performanceIndicatorRepository.findByDeptIdAndDeletedFalseOrderBySortOrderAsc(deptId);
    }

    @Override
    public List<PerformanceIndicator> getCommonPerformanceIndicators() {
        return performanceIndicatorRepository.findCommonIndicators();
    }

    @Override
    public List<PerformanceIndicator> getPerformanceIndicatorsByStatus(String status) {
        return performanceIndicatorRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(status);
    }

    @Override
    public Page<PerformanceIndicator> searchPerformanceIndicators(IndicatorType indicatorType, String deptId,
                                                                   String status, String keyword, Pageable pageable) {
        return performanceIndicatorRepository.findByConditions(
                indicatorType, deptId, status, keyword, pageable);
    }

    @Override
    public List<PerformanceIndicator> getPerformanceIndicatorsForDept(String deptId) {
        return performanceIndicatorRepository.findIndicatorsForDept(deptId);
    }

    @Override
    public boolean existsByIndicatorCode(String indicatorCode) {
        return performanceIndicatorRepository.existsByIndicatorCode(indicatorCode);
    }

    @Override
    public PerformanceIndicator enableIndicator(String indicatorId) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在: " + indicatorId));

        indicator.setStatus("启用");
        return performanceIndicatorRepository.save(indicator);
    }

    @Override
    public PerformanceIndicator disableIndicator(String indicatorId) {
        PerformanceIndicator indicator = performanceIndicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new RuntimeException("绩效指标不存在: " + indicatorId));

        indicator.setStatus("禁用");
        return performanceIndicatorRepository.save(indicator);
    }

    @Override
    public List<PerformanceIndicator> batchCreateIndicators(List<PerformanceIndicator> indicators) {
        return performanceIndicatorRepository.saveAll(indicators);
    }

    @Override
    public String generateIndicatorCode(IndicatorType indicatorType) {
        return indicatorType.name().substring(0, 2) + System.currentTimeMillis();
    }

    private void updateIndicatorFromEntity(PerformanceIndicator source, PerformanceIndicator target) {
        if (source.getIndicatorName() != null) target.setIndicatorName(source.getIndicatorName());
        if (source.getIndicatorType() != null) target.setIndicatorType(source.getIndicatorType());
        if (source.getDeptId() != null) target.setDeptId(source.getDeptId());
        if (source.getUnit() != null) target.setUnit(source.getUnit());
        if (source.getMaxScore() != null) target.setMaxScore(source.getMaxScore());
        if (source.getWeight() != null) target.setWeight(source.getWeight());
        if (source.getTargetValue() != null) target.setTargetValue(source.getTargetValue());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getCalcRule() != null) target.setCalcRule(source.getCalcRule());
        if (source.getSortOrder() != null) target.setSortOrder(source.getSortOrder());
        if (source.getStatus() != null) target.setStatus(source.getStatus());
        if (source.getRemark() != null) target.setRemark(source.getRemark());
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