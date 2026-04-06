package com.yhj.his.module.voice.repository;

import com.yhj.his.module.voice.entity.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备分组Repository
 */
@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, String> {

    /**
     * 根据分组编码查询
     */
    Optional<DeviceGroup> findByGroupCode(String groupCode);

    /**
     * 根据父分组ID查询子分组
     */
    List<DeviceGroup> findByParentIdAndDeletedFalse(String parentId);

    /**
     * 查询顶级分组(无父分组)
     */
    @Query("SELECT g FROM DeviceGroup g WHERE g.parentId IS NULL AND g.deleted = false ORDER BY g.sortOrder ASC")
    List<DeviceGroup> findTopGroups();

    /**
     * 查询所有启用的分组
     */
    @Query("SELECT g FROM DeviceGroup g WHERE g.isEnabled = true AND g.deleted = false ORDER BY g.sortOrder ASC")
    List<DeviceGroup> findEnabledGroups();

    /**
     * 检查分组编码是否存在
     */
    @Query("SELECT COUNT(g) > 0 FROM DeviceGroup g WHERE g.groupCode = :groupCode AND g.deleted = false")
    boolean existsByGroupCode(@Param("groupCode") String groupCode);

    /**
     * 查询所有未删除的分组
     */
    List<DeviceGroup> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据分组类型查询
     */
    List<DeviceGroup> findByGroupTypeAndDeletedFalse(String groupType);

    /**
     * 统计分组总数
     */
    @Query("SELECT COUNT(g) FROM DeviceGroup g WHERE g.deleted = false")
    Long countAllGroups();
}