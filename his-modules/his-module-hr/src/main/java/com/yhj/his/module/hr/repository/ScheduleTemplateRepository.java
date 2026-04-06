package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.ScheduleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 排班模板Repository
 */
@Repository
public interface ScheduleTemplateRepository extends JpaRepository<ScheduleTemplate, String>, JpaSpecificationExecutor<ScheduleTemplate> {

    /**
     * 根据模板编码查找模板
     */
    Optional<ScheduleTemplate> findByTemplateCode(String templateCode);

    /**
     * 根据科室ID查找模板列表
     */
    List<ScheduleTemplate> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 查找全局模板（科室ID为空）
     */
    @Query("SELECT t FROM ScheduleTemplate t WHERE t.deleted = false AND t.deptId IS NULL")
    List<ScheduleTemplate> findGlobalTemplates();

    /**
     * 根据星期查找模板列表
     */
    List<ScheduleTemplate> findByWeekDayAndDeletedFalse(Integer weekDay);

    /**
     * 根据科室和星期查找模板
     */
    Optional<ScheduleTemplate> findByDeptIdAndWeekDayAndDeletedFalse(String deptId, Integer weekDay);

    /**
     * 根据状态查找模板列表
     */
    List<ScheduleTemplate> findByStatusAndDeletedFalse(String status);

    /**
     * 查找科室模板（包括全局模板）
     */
    @Query("SELECT t FROM ScheduleTemplate t WHERE t.deleted = false " +
           "AND (t.deptId IS NULL OR t.deptId = :deptId) " +
           "AND t.status = '启用' " +
           "ORDER BY t.weekDay ASC")
    List<ScheduleTemplate> findTemplatesForDept(@Param("deptId") String deptId);

    /**
     * 检查模板编码是否存在
     */
    boolean existsByTemplateCode(String templateCode);
}