package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.Permission;
import com.yhj.his.module.system.enums.PermissionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    /**
     * 根据权限编码查询
     */
    Optional<Permission> findByPermCode(String permCode);

    /**
     * 根据权限编码查询(未删除)
     */
    Optional<Permission> findByPermCodeAndDeletedFalse(String permCode);

    /**
     * 查询所有未删除权限
     */
    List<Permission> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据权限类型查询
     */
    List<Permission> findByPermTypeAndDeletedFalseOrderBySortOrderAsc(PermissionType permType);

    /**
     * 根据父级ID查询子权限
     */
    List<Permission> findByParentIdAndDeletedFalseOrderBySortOrderAsc(String parentId);

    /**
     * 查询顶级权限(无父级)
     */
    List<Permission> findByParentIdIsNullAndDeletedFalseOrderBySortOrderAsc();

    /**
     * 分页查询权限
     */
    Page<Permission> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询权限
     */
    @Query("SELECT p FROM Permission p WHERE p.deleted = false " +
           "AND (:permName IS NULL OR p.permName LIKE %:permName%) " +
           "AND (:permCode IS NULL OR p.permCode LIKE %:permCode%) " +
           "AND (:permType IS NULL OR p.permType = :permType) " +
           "AND (:status IS NULL OR p.status = :status)")
    Page<Permission> findByCondition(
            @Param("permName") String permName,
            @Param("permCode") String permCode,
            @Param("permType") PermissionType permType,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 检查权限编码是否存在
     */
    boolean existsByPermCodeAndDeletedFalse(String permCode);

    /**
     * 根据ID列表查询权限
     */
    List<Permission> findByIdInAndDeletedFalse(List<String> ids);
}