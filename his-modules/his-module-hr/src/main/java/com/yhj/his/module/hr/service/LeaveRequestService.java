package com.yhj.his.module.hr.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.LeaveRequest;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.LeaveType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 请假服务接口
 */
public interface LeaveRequestService {

    /**
     * 创建请假申请
     */
    LeaveRequestVO createLeaveRequest(LeaveRequestCreateDTO dto);

    /**
     * 取消请假申请
     */
    void cancelLeaveRequest(String leaveId);

    /**
     * 审批请假申请
     */
    LeaveRequestVO approveLeaveRequest(LeaveApprovalDTO dto, String approverId, String approverName);

    /**
     * 获取请假申请详情
     */
    LeaveRequestVO getLeaveRequestById(String leaveId);

    /**
     * 根据员工获取请假申请列表
     */
    List<LeaveRequestVO> listLeaveRequestsByEmployee(String employeeId);

    /**
     * 获取待审批请假申请列表
     */
    List<LeaveRequestVO> listPendingLeaveRequests(String approverId);

    /**
     * 分页查询请假申请
     */
    PageResult<LeaveRequestVO> listLeaveRequests(String employeeId, String deptId, LeaveType leaveType, ApprovalStatus approveStatus, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);

    /**
     * 计算请假天数
     */
    BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate);

    /**
     * 查询员工某时间段内的请假天数
     */
    BigDecimal sumLeaveDays(String employeeId, LocalDate startDate, LocalDate endDate);
}