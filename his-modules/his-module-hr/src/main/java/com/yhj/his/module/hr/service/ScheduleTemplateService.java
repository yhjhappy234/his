package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.ScheduleTemplate;
import com.yhj.his.module.hr.enums.ScheduleType;

import java.util.List;
import java.util.Optional;

/**
 * 排班模板服务接口
 */
public interface ScheduleTemplateService {

    /**
     * 创建排班模板
     */
    ScheduleTemplate createScheduleTemplate(ScheduleTemplate scheduleTemplate);

    /**
     * 更新排班模板
     */
    ScheduleTemplate updateScheduleTemplate(ScheduleTemplate scheduleTemplate);

    /**
     * 根据ID删除排班模板（逻辑删除）
     */
    void deleteScheduleTemplate(String id);

    /**
     * 根据ID获取排班模板
     */
    Optional<ScheduleTemplate> getScheduleTemplateById(String id);

    /**
     * 根据模板编码获取排班模板
     */
    Optional<ScheduleTemplate> getScheduleTemplateByCode(String templateCode);

    /**
     * 根据科室ID获取排班模板列表
     */
    List<ScheduleTemplate> getScheduleTemplatesByDeptId(String deptId);

    /**
     * 获取全局排班模板列表（科室ID为空）
     */
    List<ScheduleTemplate> getGlobalScheduleTemplates();

    /**
     * 根据星期获取排班模板列表
     */
    List<ScheduleTemplate> getScheduleTemplatesByWeekDay(Integer weekDay);

    /**
     * 根据科室和星期获取排班模板
     */
    Optional<ScheduleTemplate> getScheduleTemplateByDeptAndWeekDay(String deptId, Integer weekDay);

    /**
     * 根据状态获取排班模板列表
     */
    List<ScheduleTemplate> getScheduleTemplatesByStatus(String status);

    /**
     * 获取科室排班模板（包括全局模板）
     */
    List<ScheduleTemplate> getScheduleTemplatesForDept(String deptId);

    /**
     * 检查模板编码是否存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 启用排班模板
     */
    ScheduleTemplate enableScheduleTemplate(String scheduleTemplateId);

    /**
     * 禁用排班模板
     */
    ScheduleTemplate disableScheduleTemplate(String scheduleTemplateId);

    /**
     * 批量创建排班模板（一周七天）
     */
    List<ScheduleTemplate> createWeeklyTemplates(String deptId, List<ScheduleType> scheduleTypes);

    /**
     * 生成模板编码
     */
    String generateTemplateCode(String deptId);

    /**
     * 复制模板到其他科室
     */
    List<ScheduleTemplate> copyTemplatesToDept(String sourceDeptId, String targetDeptId);

    /**
     * 获取某周的排班模板（按星期排序）
     */
    List<ScheduleTemplate> getWeeklyTemplates(String deptId);
}