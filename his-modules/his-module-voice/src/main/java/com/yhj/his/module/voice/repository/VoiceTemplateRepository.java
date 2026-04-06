package com.yhj.his.module.voice.repository;

import com.yhj.his.module.voice.entity.VoiceTemplate;
import com.yhj.his.module.voice.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 语音模板Repository
 */
@Repository
public interface VoiceTemplateRepository extends JpaRepository<VoiceTemplate, String> {

    /**
     * 根据模板编码查询
     */
    Optional<VoiceTemplate> findByTemplateCode(String templateCode);

    /**
     * 根据模板类型查询
     */
    List<VoiceTemplate> findByTemplateTypeAndDeletedFalse(TemplateType templateType);

    /**
     * 根据模板类型分页查询
     */
    Page<VoiceTemplate> findByTemplateTypeAndDeletedFalse(TemplateType templateType, Pageable pageable);

    /**
     * 查询所有启用的模板
     */
    @Query("SELECT t FROM VoiceTemplate t WHERE t.isEnabled = true AND t.deleted = false ORDER BY t.sortOrder ASC")
    List<VoiceTemplate> findEnabledTemplates();

    /**
     * 查询系统模板
     */
    @Query("SELECT t FROM VoiceTemplate t WHERE t.isSystem = true AND t.deleted = false")
    List<VoiceTemplate> findSystemTemplates();

    /**
     * 检查模板编码是否存在
     */
    @Query("SELECT COUNT(t) > 0 FROM VoiceTemplate t WHERE t.templateCode = :templateCode AND t.deleted = false")
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 查询所有未删除的模板
     */
    List<VoiceTemplate> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据模板类型查询启用的模板
     */
    @Query("SELECT t FROM VoiceTemplate t WHERE t.templateType = :templateType AND t.isEnabled = true AND t.deleted = false")
    List<VoiceTemplate> findEnabledByType(@Param("templateType") TemplateType templateType);
}