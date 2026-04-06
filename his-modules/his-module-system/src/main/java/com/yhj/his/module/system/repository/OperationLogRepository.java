package com.yhj.his.module.system.repository;

import com.yhj.his.module.system.entity.OperationLog;
import com.yhj.his.module.system.enums.OperationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, String> {

    /**
     * 根据用户ID查询日志
     */
    Page<OperationLog> findByUserId(String userId, Pageable pageable);

    /**
     * 根据操作模块查询日志
     */
    Page<OperationLog> findByOperationModule(String operationModule, Pageable pageable);

    /**
     * 根据操作时间范围查询日志
     */
    Page<OperationLog> findByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据条件分页查询日志
     */
    @Query("SELECT o FROM OperationLog o WHERE " +
           "(:userId IS NULL OR o.userId = :userId) " +
           "AND (:loginName IS NULL OR o.loginName LIKE %:loginName%) " +
           "AND (:operationType IS NULL OR o.operationType = :operationType) " +
           "AND (:operationModule IS NULL OR o.operationModule = :operationModule) " +
           "AND (:operationResult IS NULL OR o.operationResult = :operationResult) " +
           "AND (:startTime IS NULL OR o.operationTime >= :startTime) " +
           "AND (:endTime IS NULL OR o.operationTime <= :endTime)")
    Page<OperationLog> findByCondition(
            @Param("userId") String userId,
            @Param("loginName") String loginName,
            @Param("operationType") String operationType,
            @Param("operationModule") String operationModule,
            @Param("operationResult") OperationResult operationResult,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    /**
     * 统计用户操作次数
     */
    long countByUserId(String userId);

    /**
     * 统计时间段内操作次数
     */
    long countByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计时间段内失败操作次数
     */
    long countByOperationTimeBetweenAndOperationResult(LocalDateTime startTime, LocalDateTime endTime, OperationResult result);

    /**
     * 查询用户最近的操作日志
     */
    List<OperationLog> findTop10ByUserIdOrderByOperationTimeDesc(String userId);

    /**
     * 删除指定时间之前的日志
     */
    void deleteByOperationTimeBefore(LocalDateTime time);
}