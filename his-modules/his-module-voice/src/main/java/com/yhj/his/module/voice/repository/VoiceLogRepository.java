package com.yhj.his.module.voice.repository;

import com.yhj.his.module.voice.entity.VoiceLog;
import com.yhj.his.module.voice.enums.PlayResult;
import com.yhj.his.module.voice.enums.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 语音播报日志Repository
 */
@Repository
public interface VoiceLogRepository extends JpaRepository<VoiceLog, String> {

    /**
     * 根据任务ID查询
     */
    List<VoiceLog> findByTaskId(String taskId);

    /**
     * 根据任务编号查询
     */
    List<VoiceLog> findByTaskNo(String taskNo);

    /**
     * 根据设备ID查询
     */
    List<VoiceLog> findByDeviceId(String deviceId);

    /**
     * 根据播放结果查询
     */
    List<VoiceLog> findByPlayResultAndDeletedFalse(PlayResult playResult);

    /**
     * 查询指定时间范围内的日志
     */
    @Query("SELECT l FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false ORDER BY l.playTime DESC")
    List<VoiceLog> findByPlayTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询指定时间范围内的日志
     */
    Page<VoiceLog> findByPlayTimeBetweenAndDeletedFalse(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据任务类型查询
     */
    List<VoiceLog> findByTaskTypeAndDeletedFalseOrderByPlayTimeDesc(TaskType taskType);

    /**
     * 统计指定时间范围内的播报总数
     */
    @Query("SELECT COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false")
    Long countByPlayTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内成功的播报数
     */
    @Query("SELECT COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.playResult = 'SUCCESS' AND l.deleted = false")
    Long countSuccessByPlayTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内失败的播报数
     */
    @Query("SELECT COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.playResult = 'FAILED' AND l.deleted = false")
    Long countFailedByPlayTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 计算指定时间范围内的平均播放时长
     */
    @Query("SELECT AVG(l.playDuration) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.playResult = 'SUCCESS' AND l.deleted = false")
    Double avgDurationByPlayTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按任务类型统计数量
     */
    @Query("SELECT l.taskType, COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false GROUP BY l.taskType")
    List<Object[]> countByTaskType(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按设备分组统计数量
     */
    @Query("SELECT l.deviceGroup, COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false GROUP BY l.deviceGroup")
    List<Object[]> countByDeviceGroup(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 按日期统计数量
     */
    @Query("SELECT DATE(l.playTime), COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false GROUP BY DATE(l.playTime) ORDER BY DATE(l.playTime)")
    List<Object[]> countByDate(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 找出高峰时段
     */
    @Query("SELECT HOUR(l.playTime), COUNT(l) FROM VoiceLog l WHERE l.playTime BETWEEN :startTime AND :endTime AND l.deleted = false GROUP BY HOUR(l.playTime) ORDER BY COUNT(l) DESC LIMIT 1")
    Object[] findPeakHour(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询所有未删除的日志
     */
    Page<VoiceLog> findByDeletedFalseOrderByPlayTimeDesc(Pageable pageable);

    /**
     * 多条件分页查询
     */
    @Query("SELECT l FROM VoiceLog l WHERE l.deleted = false AND " +
           "(COALESCE(:taskType, NULL) IS NULL OR l.taskType = :taskType) AND " +
           "(COALESCE(:playResult, NULL) IS NULL OR l.playResult = :playResult) AND " +
           "(COALESCE(:deviceId, NULL) IS NULL OR l.deviceId = :deviceId) AND " +
           "(COALESCE(:startTime, NULL) IS NULL OR l.playTime >= :startTime) AND " +
           "(COALESCE(:endTime, NULL) IS NULL OR l.playTime <= :endTime) AND " +
           "(COALESCE(:keyword, NULL) IS NULL OR l.content LIKE :keyword OR l.taskNo LIKE :keyword)")
    Page<VoiceLog> searchLogs(@Param("taskType") TaskType taskType,
                             @Param("playResult") PlayResult playResult,
                             @Param("deviceId") String deviceId,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime,
                             @Param("keyword") String keyword,
                             Pageable pageable);
}