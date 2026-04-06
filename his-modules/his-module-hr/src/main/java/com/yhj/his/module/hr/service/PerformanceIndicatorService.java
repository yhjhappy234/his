package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.PerformanceIndicator;
import com.yhj.his.module.hr.enums.IndicatorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 绩效指标服务接口
 */
public interface PerformanceIndicatorService {

    /**
     * 创建绩效指标
     */
    PerformanceIndicator createPerformanceIndicator(PerformanceIndicator indicator);

    /**
     * 更新绩效指标
     */
    PerformanceIndicator updatePerformanceIndicator(PerformanceIndicator indicator);

    /**
     * 根据ID删除绩效指标（逻辑删除）
     */
    void deletePerformanceIndicator(String id);

    /**
     * 根据ID获取绩效指标
     */
    Optional<PerformanceIndicator> getPerformanceIndicatorById(String id);

    /**
     * 根据指标编码获取绩效指标
     */
    Optional<PerformanceIndicator> getPerformanceIndicatorByCode(String indicatorCode);

    /**
     * 根据指标类型获取绩效指标列表
     */
    List<PerformanceIndicator> getPerformanceIndicatorsByType(IndicatorType indicatorType);

    /**
     * 根据科室ID获取绩效指标列表
     */
    List<PerformanceIndicator> getPerformanceIndicatorsByDeptId(String deptId);

    /**
     * 获取通用指标列表（科室ID为空）
     */
    List<PerformanceIndicator> getCommonPerformanceIndicators();

    /**
     * 根据状态获取绩效指标列表
     */
    List<PerformanceIndicator> getPerformanceIndicatorsByStatus(String status);

    /**
     * 分页查询绩效指标
     */
    Page<PerformanceIndicator> searchPerformanceIndicators(IndicatorType indicatorType, String deptId,
                                                            String status, String keyword, Pageable pageable);

    /**
     * 获取科室指标列表（包括通用指标）
     */
    List<PerformanceIndicator> getPerformanceIndicatorsForDept(String deptId);

    /**
     * 检查指标编码是否存在
     */
    boolean existsByIndicatorCode(String indicatorCode);

    /**
     * 启用指标
     */
    PerformanceIndicator enableIndicator(String indicatorId);

    /**
     * 禁用指标
     */
    PerformanceIndicator disableIndicator(String indicatorId);

    /**
     * 批量创建指标
     */
    List<PerformanceIndicator> batchCreateIndicators(List<PerformanceIndicator> indicators);

    /**
     * 生成指标编码
     */
    String generateIndicatorCode(IndicatorType indicatorType);
}