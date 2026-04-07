package com.yhj.his.module.hr.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.PageUtils;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.Employee;
import com.yhj.his.module.hr.entity.LeaveRequest;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.LeaveType;
import com.yhj.his.module.hr.repository.EmployeeRepository;
import com.yhj.his.module.hr.repository.LeaveRequestRepository;
import com.yhj.his.module.hr.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请假服务实现
 */
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public LeaveRequestVO createLeaveRequest(LeaveRequestCreateDTO dto) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(dto.getEmployeeId())
                .orElseThrow(() -> new BusinessException("员工不存在"));

        // 验证日期
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        // 检查是否有重叠的请假申请
        List<LeaveRequest> overlapping = leaveRequestRepository.findApprovedLeaveInDateRange(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new BusinessException("该时间段已有请假申请");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        BeanUtil.copyProperties(dto, leaveRequest);
        leaveRequest.setId(IdUtil.fastSimpleUUID());
        leaveRequest.setRequestNo("LV" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + IdUtil.fastSimpleUUID().substring(0, 4));
        leaveRequest.setEmployeeNo(employee.getEmployeeNo());
        leaveRequest.setEmployeeName(employee.getEmployeeName());
        leaveRequest.setDeptId(employee.getDeptId());
        leaveRequest.setDeptName(employee.getDeptName());
        leaveRequest.setLeaveType(LeaveType.valueOf(dto.getLeaveType()));
        leaveRequest.setApplyTime(LocalDateTime.now());
        leaveRequest.setApproveStatus(ApprovalStatus.PENDING);
        leaveRequest.setStatus("待审批");

        // 计算请假天数
        BigDecimal leaveDays = dto.getLeaveDays() != null ? dto.getLeaveDays() : calculateLeaveDays(dto.getStartDate(), dto.getEndDate());
        leaveRequest.setLeaveDays(leaveDays);

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        return convertToVO(leaveRequest);
    }

    @Override
    @Transactional
    public void cancelLeaveRequest(String leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndDeletedFalse(leaveId)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));

        if (leaveRequest.getApproveStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("只有待审批的请假申请才能取消");
        }

        leaveRequest.setApproveStatus(ApprovalStatus.CANCELLED);
        leaveRequest.setStatus("已取消");
        leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestVO approveLeaveRequest(LeaveApprovalDTO dto, String approverId, String approverName) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndDeletedFalse(dto.getLeaveId())
                .orElseThrow(() -> new BusinessException("请假申请不存在"));

        if (leaveRequest.getApproveStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("请假申请已审批，不能重复审批");
        }

        ApprovalStatus approveResult = ApprovalStatus.valueOf(dto.getApproveResult());
        leaveRequest.setApproveStatus(approveResult);
        leaveRequest.setApproverId(approverId);
        leaveRequest.setApproverName(approverName);
        leaveRequest.setApproveTime(LocalDateTime.now());
        leaveRequest.setApproveRemark(dto.getApproveRemark());
        leaveRequest.setStatus(approveResult == ApprovalStatus.APPROVED ? "已通过" : "已拒绝");

        leaveRequest = leaveRequestRepository.save(leaveRequest);
        return convertToVO(leaveRequest);
    }

    @Override
    public LeaveRequestVO getLeaveRequestById(String leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findByIdAndDeletedFalse(leaveId)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));
        return convertToVO(leaveRequest);
    }

    @Override
    public List<LeaveRequestVO> listLeaveRequestsByEmployee(String employeeId) {
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndDeletedFalseOrderByApplyTimeDesc(employeeId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestVO> listPendingLeaveRequests(String approverId) {
        List<LeaveRequest> list = leaveRequestRepository.findPendingByApprover(approverId);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<LeaveRequestVO> listLeaveRequests(String employeeId, String deptId, LeaveType leaveType, ApprovalStatus approveStatus, LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize) {
        Page<LeaveRequest> page = leaveRequestRepository.findByConditions(employeeId, deptId, leaveType, approveStatus, startDate, endDate, PageUtils.of(pageNum, pageSize));
        List<LeaveRequestVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public BigDecimal calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        // 简化计算，不考虑节假日
        return BigDecimal.valueOf(days).setScale(1, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal sumLeaveDays(String employeeId, LocalDate startDate, LocalDate endDate) {
        BigDecimal sum = leaveRequestRepository.sumLeaveDays(employeeId, startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private LeaveRequestVO convertToVO(LeaveRequest leaveRequest) {
        LeaveRequestVO vo = new LeaveRequestVO();
        BeanUtil.copyProperties(leaveRequest, vo);

        if (leaveRequest.getLeaveType() != null) {
            vo.setLeaveType(leaveRequest.getLeaveType().name());
        }
        if (leaveRequest.getApproveStatus() != null) {
            vo.setApproveStatus(leaveRequest.getApproveStatus().name());
        }

        return vo;
    }
}