package com.yhj.his.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 基础Repository接口
 * <p>
 * 提供通用的CRUD操作和软删除功能
 * </p>
 *
 * @param <T>  实体类型
 * @param <ID> 主键类型
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<T> {

    /**
     * 软删除实体
     *
     * @param id 实体ID
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true WHERE e.id = :id")
    int softDelete(@Param("id") ID id);

    /**
     * 根据ID查找未删除的实体
     *
     * @param id 实体ID
     * @return 未删除的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = false")
    Optional<T> findByIdAndNotDeleted(@Param("id") ID id);

    /**
     * 查找所有未删除的实体
     *
     * @return 未删除的实体列表
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    java.util.List<T> findAllNotDeleted();

    /**
     * 永久删除（物理删除）
     *
     * @param id 实体ID
     * @return 更新的记录数
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    int permanentDelete(@Param("id") ID id);
}