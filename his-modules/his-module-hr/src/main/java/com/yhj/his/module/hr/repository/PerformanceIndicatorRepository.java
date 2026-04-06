package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.PerformanceIndicator;
import com.yhj.his.module.hr.enums.IndicatorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 绩效指标Repository
 */
@Repository
public interface PerformanceIndicatorRepository extends JpaRepository<PerformanceIndicator, String>, JpaSpecificationExecutor<PerformanceIndicator> {

    /**
     * 根据指标编码查找指标
     */
    Optional<PerformanceIndicator> findByIndicatorCode(String indicatorCode);

    /**
     * 根据指标类型查找指标列表
     */
    List<PerformanceIndicator> findByIndicatorTypeAndDeletedFalseOrderBySortOrderAsc(IndicatorType indicatorType);

    /**
     * 根据科室ID查找指标列表
     */
    List<PerformanceIndicator> findByDeptIdAndDeletedFalseOrderBySortOrderAsc(String deptId);

    /**
     * 查找通用指标（科室ID为空）
     */
    @Query("SELECT p FROM PerformanceIndicator p WHERE p.deleted = false AND p.deptId IS NULL ORDER BY p.sortOrder ASC")
    List<PerformanceIndicator> findCommonIndicators();

    /**
     * 根据状态查找指标列表
     */
    List<PerformanceIndicator> findByStatusAndDeletedFalseOrderBySortOrderAsc(String status);

    /**
     * 分页查询指标
     */
    @Query("SELECT p FROM PerformanceIndicator p WHERE p.deleted = false " +
           "AND (:indicatorType IS NULL OR p.indicatorType = :indicatorType) " +
           "AND (:deptId IS NULL OR p.deptId = :deptId) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (:keyword IS NULL OR p.indicatorName LIKE %:keyword%)")
    Page<PerformanceIndicator> findByConditions(@Param("indicatorType") IndicatorType indicatorType,
                                                 @Param("deptId") String deptId,
                                                 @Param("status") String status,
                                                 @Param("keyword") String keyword,
                                                 Pageable pageable);

    /**
     * 查找科室指标（包括通用指标）
     */
    @Query("SELECT p FROM PerformanceIndicator p WHERE p.deleted = false " +
           "AND (p.deptId IS NULL OR p.deptId = :deptId) " +
           "AND p.status = '启用' " +
           "ORDER BY p.sortOrder ASC")
    List<PerformanceIndicator> findIndicatorsForDept(@Param("deptId") String deptId);

    /**
     * 检查指标编码是否存在
     */
    boolean existsByIndicatorCode(String indicatorCode);
}