package com.yhj.his.module.emr.repository;

import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 病历模板Repository
 */
@Repository
public interface EmrTemplateRepository extends JpaRepository<EmrTemplate, String>, JpaSpecificationExecutor<EmrTemplate> {

    /**
     * 根据模板类型查询
     */
    List<EmrTemplate> findByTemplateTypeAndIsEnabledTrueAndDeletedFalse(TemplateType templateType);

    /**
     * 根据科室查询模板
     */
    List<EmrTemplate> findByDeptIdAndIsEnabledTrueAndDeletedFalse(String deptId);

    /**
     * 查询公开模板
     */
    List<EmrTemplate> findByIsPublicTrueAndIsEnabledTrueAndDeletedFalse();

    /**
     * 根据创建人查询模板
     */
    List<EmrTemplate> findByCreatorIdAndDeletedFalse(String creatorId);

    /**
     * 根据模板类型和科室查询可用模板
     */
    @Query("SELECT t FROM EmrTemplate t WHERE t.templateType = :type " +
           "AND t.isEnabled = true AND t.deleted = false " +
           "AND (t.isPublic = true OR t.deptId = :deptId OR t.creatorId = :creatorId)")
    List<EmrTemplate> findAvailableTemplates(@Param("type") TemplateType type,
                                              @Param("deptId") String deptId,
                                              @Param("creatorId") String creatorId);

    /**
     * 分页查询模板
     */
    Page<EmrTemplate> findByDeletedFalse(Pageable pageable);

    /**
     * 根据模板名称模糊查询
     */
    Page<EmrTemplate> findByTemplateNameContainingAndDeletedFalse(String name, Pageable pageable);

    /**
     * 根据类型和名称查询
     */
    Page<EmrTemplate> findByTemplateTypeAndTemplateNameContainingAndDeletedFalse(
            TemplateType templateType, String name, Pageable pageable);
}