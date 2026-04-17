package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.Schedule;
import com.yhj.his.module.hr.enums.ScheduleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排班Repository (HR模块)
 */
@Repository
public interface HrScheduleRepository extends JpaRepository<Schedule, String>, JpaSpecificationExecutor<Schedule> {

    /**
     * 根据员工ID和日期查找排班
     */
    Optional<Schedule> findByEmployeeIdAndScheduleDateAndDeletedFalse(String employeeId, LocalDate scheduleDate);

    /**
     * 根据员工ID和日期范围查找排班列表
     */
    @Query("SELECT s FROM Schedule s WHERE s.deleted = false " +
           "AND s.employeeId = :employeeId " +
           "AND s.scheduleDate >= :startDate " +
           "AND s.scheduleDate <= :endDate " +
           "ORDER BY s.scheduleDate ASC")
    List<Schedule> findByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 根据科室ID和日期范围查找排班列表
     */
    @Query("SELECT s FROM Schedule s WHERE s.deleted = false " +
           "AND s.deptId = :deptId " +
           "AND s.scheduleDate >= :startDate " +
           "AND s.scheduleDate <= :endDate " +
           "ORDER BY s.scheduleDate ASC, s.startTime ASC")
    List<Schedule> findByDeptIdAndDateRange(@Param("deptId") String deptId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    /**
     * 根据日期查找排班列表
     */
    List<Schedule> findByScheduleDateAndDeletedFalse(LocalDate scheduleDate);

    /**
     * 根据班次类型和日期查找排班列表
     */
    List<Schedule> findByScheduleTypeAndScheduleDateAndDeletedFalse(ScheduleType scheduleType, LocalDate scheduleDate);

    /**
     * 分页查询排班
     */
    @Query("SELECT s FROM Schedule s WHERE s.deleted = false " +
           "AND (:deptId IS NULL OR s.deptId = :deptId) " +
           "AND (:employeeId IS NULL OR s.employeeId = :employeeId) " +
           "AND (:scheduleType IS NULL OR s.scheduleType = :scheduleType) " +
           "AND (:startDate IS NULL OR s.scheduleDate >= :startDate) " +
           "AND (:endDate IS NULL OR s.scheduleDate <= :endDate)")
    Page<Schedule> findByConditions(@Param("deptId") String deptId,
                                     @Param("employeeId") String employeeId,
                                     @Param("scheduleType") ScheduleType scheduleType,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     Pageable pageable);

    /**
     * 查询员工某日期范围内的班次统计
     */
    @Query("SELECT s.scheduleType, COUNT(s) FROM Schedule s WHERE s.deleted = false " +
           "AND s.employeeId = :employeeId " +
           "AND s.scheduleDate >= :startDate " +
           "AND s.scheduleDate <= :endDate " +
           "GROUP BY s.scheduleType")
    List<Object[]> countByEmployeeAndDateRangeGroupByType(@Param("employeeId") String employeeId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    /**
     * 删除员工某日期范围内的排班
     */
    void deleteByEmployeeIdAndScheduleDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据ID查找未删除的排班
     */
    Optional<Schedule> findByIdAndDeletedFalse(String id);
}