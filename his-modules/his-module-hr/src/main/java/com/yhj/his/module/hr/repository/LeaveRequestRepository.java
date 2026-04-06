package com.yhj.his.module.hr.repository;

import com.yhj.his.module.hr.entity.LeaveRequest;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.LeaveType;
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
 * 请假申请Repository
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, JpaSpecificationExecutor<LeaveRequest> {

    /**
     * 根据申请单号查找请假申请
     */
    Optional<LeaveRequest> findByRequestNo(String requestNo);

    /**
     * 根据员工ID查找请假申请列表
     */
    List<LeaveRequest> findByEmployeeIdAndDeletedFalseOrderByApplyTimeDesc(String employeeId);

    /**
     * 根据审批状态查找请假申请列表
     */
    List<LeaveRequest> findByApproveStatusAndDeletedFalseOrderByApplyTimeDesc(ApprovalStatus approveStatus);

    /**
     * 根据审批人ID查找待审批请假申请
     */
    @Query("SELECT l FROM LeaveRequest l WHERE l.deleted = false " +
           "AND l.approverId = :approverId " +
           "AND l.approveStatus = 'PENDING' " +
           "ORDER BY l.applyTime ASC")
    List<LeaveRequest> findPendingByApprover(@Param("approverId") String approverId);

    /**
     * 分页查询请假申请
     */
    @Query("SELECT l FROM LeaveRequest l WHERE l.deleted = false " +
           "AND (:employeeId IS NULL OR l.employeeId = :employeeId) " +
           "AND (:deptId IS NULL OR l.deptId = :deptId) " +
           "AND (:leaveType IS NULL OR l.leaveType = :leaveType) " +
           "AND (:approveStatus IS NULL OR l.approveStatus = :approveStatus) " +
           "AND (:startDate IS NULL OR l.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR l.endDate <= :endDate)")
    Page<LeaveRequest> findByConditions(@Param("employeeId") String employeeId,
                                         @Param("deptId") String deptId,
                                         @Param("leaveType") LeaveType leaveType,
                                         @Param("approveStatus") ApprovalStatus approveStatus,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         Pageable pageable);

    /**
     * 查询员工在某时间段内已批准的请假
     */
    @Query("SELECT l FROM LeaveRequest l WHERE l.deleted = false " +
           "AND l.employeeId = :employeeId " +
           "AND l.approveStatus = 'APPROVED' " +
           "AND l.startDate <= :endDate " +
           "AND l.endDate >= :startDate")
    List<LeaveRequest> findApprovedLeaveInDateRange(@Param("employeeId") String employeeId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 统计员工某时间段内的请假天数
     */
    @Query("SELECT SUM(l.leaveDays) FROM LeaveRequest l WHERE l.deleted = false " +
           "AND l.employeeId = :employeeId " +
           "AND l.approveStatus = 'APPROVED' " +
           "AND l.startDate >= :startDate " +
           "AND l.endDate <= :endDate")
    BigDecimal sumLeaveDays(@Param("employeeId") String employeeId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);

    /**
     * 统计员工某请假类型的总天数
     */
    @Query("SELECT SUM(l.leaveDays) FROM LeaveRequest l WHERE l.deleted = false " +
           "AND l.employeeId = :employeeId " +
           "AND l.leaveType = :leaveType " +
           "AND l.approveStatus = 'APPROVED'")
    BigDecimal sumLeaveDaysByType(@Param("employeeId") String employeeId,
                                  @Param("leaveType") LeaveType leaveType);

    /**
     * 检查申请单号是否存在
     */
    boolean existsByRequestNo(String requestNo);

    /**
     * 根据ID查找未删除的请假申请
     */
    Optional<LeaveRequest> findByIdAndDeletedFalse(String id);
}