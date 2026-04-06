package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.AuditLog;
import com.yhj.his.module.system.enums.AuditLevel;
import com.yhj.his.module.system.enums.AuditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志数据访问
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    /**
     * 根据用户ID查询审计日志
     */
    Page<AuditLog> findByUserId(String userId, Pageable pageable);

    /**
     * 根据审计类型查询
     */
    Page<AuditLog> findByAuditType(AuditType auditType, Pageable pageable);

    /**
     * 根据审计级别查询
     */
    Page<AuditLog> findByAuditLevel(AuditLevel auditLevel, Pageable pageable);

    /**
     * 根据审计时间范围查询
     */
    Page<AuditLog> findByAuditTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据条件分页查询审计日志
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) " +
           "AND (:loginName IS NULL OR a.loginName LIKE %:loginName%) " +
           "AND (:auditType IS NULL OR a.auditType = :auditType) " +
           "AND (:auditLevel IS NULL OR a.auditLevel = :auditLevel) " +
           "AND (:auditEvent IS NULL OR a.auditEvent LIKE %:auditEvent%) " +
           "AND (:startTime IS NULL OR a.auditTime >= :startTime) " +
           "AND (:endTime IS NULL OR a.auditTime <= :endTime)")
    Page<AuditLog> findByCondition(
            @Param("userId") String userId,
            @Param("loginName") String loginName,
            @Param("auditType") AuditType auditType,
            @Param("auditLevel") AuditLevel auditLevel,
            @Param("auditEvent") String auditEvent,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计用户审计记录数
     */
    long countByUserId(String userId);

    /**
     * 统计指定级别的审计记录
     */
    long countByAuditLevel(AuditLevel auditLevel);

    /**
     * 统计时间段内的审计记录
     */
    long countByAuditTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询未告警的严重级别审计日志
     */
    List<AuditLog> findByAuditLevelAndIsAlertedFalse(AuditLevel auditLevel);

    /**
     * 查询用户最近的审计日志
     */
    List<AuditLog> findTop10ByUserIdOrderByAuditTimeDesc(String userId);

    /**
     * 删除指定时间之前的日志
     */
    void deleteByAuditTimeBefore(LocalDateTime time);
}