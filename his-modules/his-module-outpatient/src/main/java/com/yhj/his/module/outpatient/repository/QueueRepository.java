package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排队信息Repository
 */
@Repository
public interface QueueRepository extends JpaRepository<Queue, String>, JpaSpecificationExecutor<Queue> {

    /**
     * 根据挂号ID查询排队信息
     */
    Optional<Queue> findByRegistrationId(String registrationId);

    /**
     * 根据医生ID和日期查询排队列表
     */
    List<Queue> findByDoctorIdAndScheduleDate(String doctorId, LocalDate scheduleDate);

    /**
     * 根据诊室和日期查询排队列表
     */
    List<Queue> findByClinicRoomAndScheduleDate(String clinicRoom, LocalDate scheduleDate);

    /**
     * 根据状态查询排队列表
     */
    List<Queue> findByStatus(String status);

    /**
     * 查询等候中的患者列表(按优先级和排队号排序)
     */
    @Query("SELECT q FROM Queue q WHERE q.doctorId = :doctorId AND q.scheduleDate = :scheduleDate AND q.status = '等候中' ORDER BY q.priority DESC, q.queueNo ASC")
    List<Queue> findWaitingQueue(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 查询当前就诊患者
     */
    @Query("SELECT q FROM Queue q WHERE q.doctorId = :doctorId AND q.scheduleDate = :scheduleDate AND q.status = '就诊中'")
    Optional<Queue> findCurrentPatient(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 统计等候人数
     */
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.doctorId = :doctorId AND q.scheduleDate = :scheduleDate AND q.status = '等候中'")
    int countWaiting(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 统计过号人数
     */
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.doctorId = :doctorId AND q.scheduleDate = :scheduleDate AND q.status = '过号'")
    int countPassed(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);
}