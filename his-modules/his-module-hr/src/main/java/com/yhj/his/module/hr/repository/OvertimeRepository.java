package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.Overtime;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 加班记录Repository
 */
@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, String>, JpaSpecificationExecutor<Overtime> {

    /**
     * 根据加班单号查找加班记录
     */
    Optional<Overtime> findByOvertimeNo(String overtimeNo);

    /**
     * 根据员工ID查找加班记录列表
     */
    List<Overtime> findByEmployeeIdAndDeletedFalseOrderByApplyTimeDesc(String employeeId);

    /**
     * 根据审批状态查找加班记录列表
     */
    List<Overtime> findByApproveStatusAndDeletedFalseOrderByApplyTimeDesc(ApprovalStatus approveStatus);

    /**
     * 根据审批人ID查找待审批加班记录
     */
    @Query("SELECT o FROM Overtime o WHERE o.deleted = false " +
           "AND o.approverId = :approverId " +
           "AND o.approveStatus = 'PENDING' " +
           "ORDER BY o.applyTime ASC")
    List<Overtime> findPendingByApprover(@Param("approverId") String approverId);

    /**
     * 分页查询加班记录
     */
    @Query("SELECT o FROM Overtime o WHERE o.deleted = false " +
           "AND (:employeeId IS NULL OR o.employeeId = :employeeId) " +
           "AND (:deptId IS NULL OR o.deptId = :deptId) " +
           "AND (:approveStatus IS NULL OR o.approveStatus = :approveStatus) " +
           "AND (:startDate IS NULL OR o.overtimeDate >= :startDate) " +
           "AND (:endDate IS NULL OR o.overtimeDate <= :endDate)")
    Page<Overtime> findByConditions(@Param("employeeId") String employeeId,
                                     @Param("deptId") String deptId,
                                     @Param("approveStatus") ApprovalStatus approveStatus,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     Pageable pageable);

    /**
     * 统计员工某时间段内的加班时长
     */
    @Query("SELECT SUM(o.overtimeHours) FROM Overtime o WHERE o.deleted = false " +
           "AND o.employeeId = :employeeId " +
           "AND o.approveStatus = 'APPROVED' " +
           "AND o.overtimeDate >= :startDate " +
           "AND o.overtimeDate <= :endDate")
    BigDecimal sumOvertimeHours(@Param("employeeId") String employeeId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    /**
     * 根据补偿类型统计加班时长
     */
    @Query("SELECT SUM(o.overtimeHours) FROM Overtime o WHERE o.deleted = false " +
           "AND o.employeeId = :employeeId " +
           "AND o.approveStatus = 'APPROVED' " +
           "AND o.compensateType = :compensateType")
    BigDecimal sumOvertimeHoursByCompensateType(@Param("employeeId") String employeeId,
                                                @Param("compensateType") String compensateType);

    /**
     * 检查加班单号是否存在
     */
    boolean existsByOvertimeNo(String overtimeNo);

    /**
     * 根据ID查找未删除的加班记录
     */
    Optional<Overtime> findByIdAndDeletedFalse(String id);
}