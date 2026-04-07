package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    /**
     * 根据用户ID查询角色关联
     */
    List<UserRole> findByUserId(String userId);

    /**
     * 根据角色ID查询用户关联
     */
    List<UserRole> findByRoleId(String roleId);

    /**
     * 根据用户ID查询角色ID列表
     */
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = false")
    List<String> findRoleIdsByUserId(@Param("userId") String userId);

    /**
     * 根据角色ID查询用户ID列表
     */
    @Query("SELECT ur.userId FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = false")
    List<String> findUserIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 删除用户的所有角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") String userId);

    /**
     * 删除角色的所有用户关联
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 检查用户角色关联是否存在
     */
    boolean existsByUserIdAndRoleId(String userId, String roleId);
}