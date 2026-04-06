package com.yhj.his.module.voice.repository;

import com.yhj.his.module.voice.entity.VoiceTask;
import com.yhj.his.module.voice.enums.TaskStatus;
import com.yhj.his.module.voice.enums.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 语音任务Repository
 */
@Repository
public interface VoiceTaskRepository extends JpaRepository<VoiceTask, String> {

    /**
     * 根据任务编号查询
     */
    Optional<VoiceTask> findByTaskNo(String taskNo);

    /**
     * 根据状态查询
     */
    List<VoiceTask> findByStatus(TaskStatus status);

    /**
     * 查询待播报的任务(按优先级和时间排序)
     */
    @Query("SELECT t FROM VoiceTask t WHERE t.status = :status AND t.deleted = false ORDER BY t.priority ASC, t.createTime ASC")
    List<VoiceTask> findPendingTasks(@Param("status") TaskStatus status);

    /**
     * 查询指定时间范围内的任务
     */
    @Query("SELECT t FROM VoiceTask t WHERE t.createTime BETWEEN :startTime AND :endTime AND t.deleted = false")
    List<VoiceTask> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据任务类型分页查询
     */
    Page<VoiceTask> findByTaskTypeAndDeletedFalse(TaskType taskType, Pageable pageable);

    /**
     * 根据状态分页查询
     */
    Page<VoiceTask> findByStatusAndDeletedFalse(TaskStatus status, Pageable pageable);

    /**
     * 根据创建人查询
     */
    List<VoiceTask> findByCreatorIdAndDeletedFalse(String creatorId);

    /**
     * 根据业务ID查询
     */
    Optional<VoiceTask> findByBizIdAndDeletedFalse(String bizId);

    /**
     * 统计指定状态的任务数量
     */
    @Query("SELECT COUNT(t) FROM VoiceTask t WHERE t.status = :status AND t.deleted = false")
    Long countByStatus(@Param("status") TaskStatus status);

    /**
     * 查询高优先级待播报任务
     */
    @Query("SELECT t FROM VoiceTask t WHERE t.status = 'PENDING' AND t.priority <= :maxPriority AND t.deleted = false ORDER BY t.priority ASC, t.createTime ASC")
    List<VoiceTask> findHighPriorityTasks(@Param("maxPriority") Integer maxPriority);

    /**
     * 查询指定设备分组的待播报任务
     */
    @Query("SELECT t FROM VoiceTask t WHERE t.status = 'PENDING' AND t.targetGroups LIKE :groupCode AND t.deleted = false ORDER BY t.priority ASC, t.createTime ASC")
    List<VoiceTask> findPendingTasksByGroup(@Param("groupCode") String groupCode);

    /**
     * 分页查询所有未删除的任务
     */
    Page<VoiceTask> findByDeletedFalse(Pageable pageable);

    /**
     * 多条件分页查询
     */
    @Query("SELECT t FROM VoiceTask t WHERE t.deleted = false AND " +
           "(COALESCE(:taskType, NULL) IS NULL OR t.taskType = :taskType) AND " +
           "(COALESCE(:status, NULL) IS NULL OR t.status = :status) AND " +
           "(COALESCE(:startTime, NULL) IS NULL OR t.createTime >= :startTime) AND " +
           "(COALESCE(:endTime, NULL) IS NULL OR t.createTime <= :endTime) AND " +
           "(COALESCE(:keyword, NULL) IS NULL OR t.content LIKE :keyword OR t.taskNo LIKE :keyword)")
    Page<VoiceTask> searchTasks(@Param("taskType") TaskType taskType,
                               @Param("status") TaskStatus status,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime,
                               @Param("keyword") String keyword,
                               Pageable pageable);
}