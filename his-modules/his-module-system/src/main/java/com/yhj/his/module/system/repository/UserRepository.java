package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.User;
import com.yhj.his.module.system.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据登录账号查询
     */
    Optional<User> findByLoginName(String loginName);

    /**
     * 根据登录账号查询(未删除)
     */
    Optional<User> findByLoginNameAndDeletedFalse(String loginName);

    /**
     * 根据员工ID查询
     */
    Optional<User> findByEmployeeId(String employeeId);

    /**
     * 根据科室ID查询用户列表
     */
    List<User> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 根据状态查询用户列表
     */
    List<User> findByStatusAndDeletedFalse(UserStatus status);

    /**
     * 分页查询用户
     */
    Page<User> findByDeletedFalse(Pageable pageable);

    /**
     * 根据条件分页查询用户
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false " +
           "AND (:userName IS NULL OR u.userName LIKE %:userName%) " +
           "AND (:loginName IS NULL OR u.loginName LIKE %:loginName%) " +
           "AND (:deptId IS NULL OR u.deptId = :deptId) " +
           "AND (:status IS NULL OR u.status = :status) " +
           "AND (:phone IS NULL OR u.phone LIKE %:phone%)")
    Page<User> findByCondition(
            @Param("userName") String userName,
            @Param("loginName") String loginName,
            @Param("deptId") String deptId,
            @Param("status") UserStatus status,
            @Param("phone") String phone,
            Pageable pageable);

    /**
     * 检查登录账号是否存在
     */
    boolean existsByLoginNameAndDeletedFalse(String loginName);

    /**
     * 检查员工ID是否存在
     */
    boolean existsByEmployeeIdAndDeletedFalse(String employeeId);

    /**
     * 统计科室用户数量
     */
    long countByDeptIdAndDeletedFalse(String deptId);
}