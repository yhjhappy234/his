package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.DataDictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据字典数据访问
 */
@Repository
public interface DataDictionaryRepository extends JpaRepository<DataDictionary, String> {

    /**
     * 根据字典类型和编码查询
     */
    Optional<DataDictionary> findByDictTypeAndDictCode(String dictType, String dictCode);

    /**
     * 根据字典类型和编码查询(未删除)
     */
    Optional<DataDictionary> findByDictTypeAndDictCodeAndDeletedFalse(String dictType, String dictCode);

    /**
     * 根据字典类型查询字典项列表
     */
    List<DataDictionary> findByDictTypeAndDeletedFalseOrderBySortOrderAsc(String dictType);

    /**
     * 根据字典类型查询启用的字典项
     */
    List<DataDictionary> findByDictTypeAndIsEnabledTrueAndDeletedFalseOrderBySortOrderAsc(String dictType);

    /**
     * 根据父级编码查询子字典项
     */
    List<DataDictionary> findByDictTypeAndParentCodeAndDeletedFalseOrderBySortOrderAsc(String dictType, String parentCode);

    /**
     * 查询所有未删除字典项
     */
    List<DataDictionary> findByDeletedFalseOrderByDictTypeAscSortOrderAsc();

    /**
     * 分页查询字典项
     */
    Page<DataDictionary> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询字典项
     */
    @Query("SELECT d FROM DataDictionary d WHERE d.deleted = false " +
           "AND (:dictType IS NULL OR d.dictType = :dictType) " +
           "AND (:dictName IS NULL OR d.dictName LIKE %:dictName%) " +
           "AND (:dictCode IS NULL OR d.dictCode LIKE %:dictCode%) " +
           "AND (:isEnabled IS NULL OR d.isEnabled = :isEnabled)")
    Page<DataDictionary> findByCondition(
            @Param("dictType") String dictType,
            @Param("dictName") String dictName,
            @Param("dictCode") String dictCode,
            @Param("isEnabled") Boolean isEnabled,
            Pageable pageable);

    /**
     * 检查字典类型和编码是否存在
     */
    boolean existsByDictTypeAndDictCodeAndDeletedFalse(String dictType, String dictCode);

    /**
     * 获取所有字典类型
     */
    @Query("SELECT DISTINCT d.dictType FROM DataDictionary d WHERE d.deleted = false ORDER BY d.dictType")
    List<String> findAllDictTypes();

    /**
     * 获取字典类型的默认值
     */
    Optional<DataDictionary> findByDictTypeAndIsEnabledTrueAndIsDefaultTrueAndDeletedFalse(String dictType);
}