package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排班信息Repository
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String>, JpaSpecificationExecutor<Schedule> {

    /**
     * 根据医生ID、排班日期、时间段查询
     */
    Optional<Schedule> findByDoctorIdAndScheduleDateAndTimePeriod(String doctorId, LocalDate scheduleDate, String timePeriod);

    /**
     * 根据科室ID和日期范围查询排班列表
     */
    List<Schedule> findByDeptIdAndScheduleDateBetween(String deptId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据医生ID和日期范围查询排班列表
     */
    List<Schedule> findByDoctorIdAndScheduleDateBetween(String doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据日期查询排班列表
     */
    List<Schedule> findByScheduleDate(LocalDate scheduleDate);

    /**
     * 根据日期和状态查询排班列表
     */
    List<Schedule> findByScheduleDateAndStatus(LocalDate scheduleDate, String status);

    /**
     * 更新号源数量
     */
    @Modifying
    @Query("UPDATE Schedule s SET s.bookedQuota = s.bookedQuota + 1, s.availableQuota = s.availableQuota - 1 WHERE s.id = :scheduleId AND s.availableQuota > 0")
    int incrementBookedQuota(@Param("scheduleId") String scheduleId);

    /**
     * 释放号源
     */
    @Modifying
    @Query("UPDATE Schedule s SET s.bookedQuota = s.bookedQuota - 1, s.availableQuota = s.availableQuota + 1 WHERE s.id = :scheduleId AND s.bookedQuota > 0")
    int decrementBookedQuota(@Param("scheduleId") String scheduleId);
}