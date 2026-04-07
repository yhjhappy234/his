package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * 根据角色编码查询
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 根据角色编码查询(未删除)
     */
    Optional<Role> findByRoleCodeAndDeletedFalse(String roleCode);

    /**
     * 查询所有未删除角色
     */
    List<Role> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 根据状态查询角色
     */
    List<Role> findByStatusAndDeletedFalseOrderBySortOrderAsc(String status);

    /**
     * 分页查询角色
     */
    Page<Role> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询角色
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false " +
           "AND (:roleName IS NULL OR r.roleName LIKE %:roleName%) " +
           "AND (:roleCode IS NULL OR r.roleCode LIKE %:roleCode%) " +
           "AND (:status IS NULL OR r.status = :status)")
    Page<Role> findByCondition(
            @Param("roleName") String roleName,
            @Param("roleCode") String roleCode,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCodeAndDeletedFalse(String roleCode);

    /**
     * 根据ID列表查询角色
     */
    List<Role> findByIdInAndDeletedFalse(List<String> ids);
}