package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联数据访问
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {

    /**
     * 根据角色ID查询权限关联
     */
    List<RolePermission> findByRoleId(String roleId);

    /**
     * 根据权限ID查询角色关联
     */
    List<RolePermission> findByPermId(String permId);

    /**
     * 根据角色ID查询权限ID列表
     */
    @Query("SELECT rp.permId FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.deleted = false")
    List<String> findPermIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 根据角色ID列表查询权限ID列表
     */
    @Query("SELECT DISTINCT rp.permId FROM RolePermission rp WHERE rp.roleId IN :roleIds AND rp.deleted = false")
    List<String> findPermIdsByRoleIds(@Param("roleIds") List<String> roleIds);

    /**
     * 删除角色的所有权限关联
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 删除权限的所有角色关联
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.permId = :permId")
    void deleteByPermId(@Param("permId") String permId);

    /**
     * 检查角色权限关联是否存在
     */
    boolean existsByRoleIdAndPermId(String roleId, String permId);
}