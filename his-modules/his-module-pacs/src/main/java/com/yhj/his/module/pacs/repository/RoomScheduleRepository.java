package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.RoomSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, String>, JpaSpecificationExecutor<RoomSchedule> {

    List<RoomSchedule> findByRoomNo(String roomNo);
    List<RoomSchedule> findByScheduleDate(LocalDate scheduleDate);
    List<RoomSchedule> findByRoomNoAndScheduleDate(String roomNo, LocalDate scheduleDate);

    @Query("SELECT r FROM RoomSchedule r WHERE r.scheduleDate = :date AND r.status = '开放' AND r.availableQuota > 0")
    List<RoomSchedule> findAvailableByDate(@Param("date") LocalDate date);

    @Query("SELECT r FROM RoomSchedule r WHERE r.scheduleDate = :date AND r.status = '开放' AND r.availableQuota > 0 AND (:examType IS NULL OR r.examTypeLimit = :examType)")
    List<RoomSchedule> findAvailableByDateAndExamType(@Param("date") LocalDate date, @Param("examType") String examType);

    @Modifying
    @Query("UPDATE RoomSchedule r SET r.scheduledCount = r.scheduledCount + 1, r.availableQuota = r.availableQuota - 1 WHERE r.id = :id AND r.availableQuota > 0")
    int incrementScheduledCount(@Param("id") String id);

    @Modifying
    @Query("UPDATE RoomSchedule r SET r.scheduledCount = r.scheduledCount - 1, r.availableQuota = r.availableQuota + 1 WHERE r.id = :id AND r.scheduledCount > 0")
    int decrementScheduledCount(@Param("id") String id);

    @Query("SELECT r FROM RoomSchedule r WHERE " +
           "(:roomNo IS NULL OR r.roomNo = :roomNo) AND " +
           "(:scheduleDate IS NULL OR r.scheduleDate = :scheduleDate) AND " +
           "(:shift IS NULL OR r.shift = :shift) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<RoomSchedule> findByConditions(
            @Param("roomNo") String roomNo,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("shift") String shift,
            @Param("status") String status,
            Pageable pageable);
}