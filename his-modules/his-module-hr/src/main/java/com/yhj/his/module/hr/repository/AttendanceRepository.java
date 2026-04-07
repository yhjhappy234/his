package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.Attendance;
import com.yhj.his.module.hr.enums.AttendanceStatus;
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
 * 考勤记录Repository
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String>, JpaSpecificationExecutor<Attendance> {

    /**
     * 根据员工ID和日期查找考勤记录
     */
    Optional<Attendance> findByEmployeeIdAndAttendanceDateAndDeletedFalse(String employeeId, LocalDate attendanceDate);

    /**
     * 根据员工ID和日期范围查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.deleted = false " +
           "AND a.employeeId = :employeeId " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate " +
           "ORDER BY a.attendanceDate ASC")
    List<Attendance> findByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    /**
     * 根据科室ID和日期范围查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.deleted = false " +
           "AND a.deptId = :deptId " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate " +
           "ORDER BY a.attendanceDate ASC")
    List<Attendance> findByDeptIdAndDateRange(@Param("deptId") String deptId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 根据日期查找考勤记录
     */
    List<Attendance> findByAttendanceDateAndDeletedFalse(LocalDate attendanceDate);

    /**
     * 分页查询考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.deleted = false " +
           "AND (:deptId IS NULL OR a.deptId = :deptId) " +
           "AND (:employeeId IS NULL OR a.employeeId = :employeeId) " +
           "AND (:status IS NULL OR a.attendanceStatus = :status) " +
           "AND (:startDate IS NULL OR a.attendanceDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.attendanceDate <= :endDate)")
    Page<Attendance> findByConditions(@Param("deptId") String deptId,
                                       @Param("employeeId") String employeeId,
                                       @Param("status") AttendanceStatus status,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);

    /**
     * 统计员工某时间段内的考勤状态
     */
    @Query("SELECT a.attendanceStatus, COUNT(a) FROM Attendance a WHERE a.deleted = false " +
           "AND a.employeeId = :employeeId " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate " +
           "GROUP BY a.attendanceStatus")
    List<Object[]> countByEmployeeAndDateRangeGroupByStatus(@Param("employeeId") String employeeId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 统计员工迟到次数
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.deleted = false " +
           "AND a.employeeId = :employeeId " +
           "AND a.lateMinutes > 0 " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate")
    long countLateTimes(@Param("employeeId") String employeeId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

    /**
     * 统计员工早退次数
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.deleted = false " +
           "AND a.employeeId = :employeeId " +
           "AND a.earlyMinutes > 0 " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate")
    long countEarlyLeaveTimes(@Param("employeeId") String employeeId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    /**
     * 统计员工总加班时长
     */
    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.deleted = false " +
           "AND a.employeeId = :employeeId " +
           "AND a.attendanceDate >= :startDate " +
           "AND a.attendanceDate <= :endDate")
    Double sumOvertimeHours(@Param("employeeId") String employeeId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);

    /**
     * 根据ID查找未删除的考勤记录
     */
    Optional<Attendance> findByIdAndDeletedFalse(String id);
}