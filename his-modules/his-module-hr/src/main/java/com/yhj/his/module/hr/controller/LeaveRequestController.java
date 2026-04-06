package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.enums.LeaveType;
import com.yhj.his.module.hr.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 请假管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/leave")
@RequiredArgsConstructor
@Tag(name = "请假管理", description = "请假申请管理接口")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    @Operation(summary = "创建请假申请", description = "提交请假申请")
    public Result<LeaveRequestVO> createLeaveRequest(@Valid @RequestBody LeaveRequestCreateDTO dto) {
        LeaveRequestVO vo = leaveRequestService.createLeaveRequest(dto);
        return Result.success(vo);
    }

    @PostMapping("/{leaveId}/cancel")
    @Operation(summary = "取消请假申请", description = "取消请假申请")
    public Result<Void> cancelLeaveRequest(@Parameter(description = "请假申请ID") @PathVariable String leaveId) {
        leaveRequestService.cancelLeaveRequest(leaveId);
        return Result.success();
    }

    @PostMapping("/approve")
    @Operation(summary = "审批请假申请", description = "审批请假申请")
    public Result<LeaveRequestVO> approveLeaveRequest(@Valid @RequestBody LeaveApprovalDTO dto) {
        // 审批人信息从上下文获取，这里简化处理
        LeaveRequestVO vo = leaveRequestService.approveLeaveRequest(dto, "SYSTEM", "系统管理员");
        return Result.success(vo);
    }

    @GetMapping("/{leaveId}")
    @Operation(summary = "获取请假详情", description = "根据ID获取请假申请详情")
    public Result<LeaveRequestVO> getLeaveRequest(@Parameter(description = "请假申请ID") @PathVariable String leaveId) {
        LeaveRequestVO vo = leaveRequestService.getLeaveRequestById(leaveId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查询员工请假列表", description = "查询指定员工的请假申请列表")
    public Result<List<LeaveRequestVO>> listLeaveRequestsByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<LeaveRequestVO> list = leaveRequestService.listLeaveRequestsByEmployee(employeeId);
        return Result.success(list);
    }

    @GetMapping("/pending/{approverId}")
    @Operation(summary = "查询待审批请假", description = "查询指定审批人的待审批请假申请")
    public Result<List<LeaveRequestVO>> listPendingLeaveRequests(
            @Parameter(description = "审批人ID") @PathVariable String approverId) {
        List<LeaveRequestVO> list = leaveRequestService.listPendingLeaveRequests(approverId);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询请假", description = "分页查询请假申请列表")
    public Result<PageResult<LeaveRequestVO>> listLeaveRequests(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "请假类型") @RequestParam(required = false) String leaveType,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approveStatus,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        LeaveType type = leaveType != null ? LeaveType.valueOf(leaveType) : null;
        ApprovalStatus status = approveStatus != null ? ApprovalStatus.valueOf(approveStatus) : null;
        PageResult<LeaveRequestVO> result = leaveRequestService.listLeaveRequests(employeeId, deptId, type, status, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }
}