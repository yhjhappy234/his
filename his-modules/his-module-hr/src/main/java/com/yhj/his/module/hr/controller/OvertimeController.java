package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.service.OvertimeService;
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
 * 加班管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/overtime")
@RequiredArgsConstructor
@Tag(name = "加班管理", description = "加班管理接口")
public class OvertimeController {

    private final OvertimeService overtimeService;

    @PostMapping
    @Operation(summary = "创建加班申请", description = "提交加班申请")
    public Result<OvertimeVO> createOvertime(@Valid @RequestBody OvertimeCreateDTO dto) {
        OvertimeVO vo = overtimeService.createOvertime(dto);
        return Result.success(vo);
    }

    @PostMapping("/{overtimeId}/cancel")
    @Operation(summary = "取消加班申请", description = "取消加班申请")
    public Result<Void> cancelOvertime(@Parameter(description = "加班记录ID") @PathVariable String overtimeId) {
        overtimeService.cancelOvertime(overtimeId);
        return Result.success();
    }

    @PostMapping("/approve")
    @Operation(summary = "审批加班申请", description = "审批加班申请")
    public Result<OvertimeVO> approveOvertime(@Valid @RequestBody OvertimeApprovalDTO dto) {
        // 审批人信息从上下文获取，这里简化处理
        OvertimeVO vo = overtimeService.approveOvertime(dto, "SYSTEM", "系统管理员");
        return Result.success(vo);
    }

    @GetMapping("/{overtimeId}")
    @Operation(summary = "获取加班详情", description = "根据ID获取加班记录详情")
    public Result<OvertimeVO> getOvertime(@Parameter(description = "加班记录ID") @PathVariable String overtimeId) {
        OvertimeVO vo = overtimeService.getOvertimeById(overtimeId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查询员工加班列表", description = "查询指定员工的加班记录列表")
    public Result<List<OvertimeVO>> listOvertimesByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<OvertimeVO> list = overtimeService.listOvertimesByEmployee(employeeId);
        return Result.success(list);
    }

    @GetMapping("/pending/{approverId}")
    @Operation(summary = "查询待审批加班", description = "查询指定审批人的待审批加班记录")
    public Result<List<OvertimeVO>> listPendingOvertimes(
            @Parameter(description = "审批人ID") @PathVariable String approverId) {
        List<OvertimeVO> list = overtimeService.listPendingOvertimes(approverId);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询加班", description = "分页查询加班记录列表")
    public Result<PageResult<OvertimeVO>> listOvertimes(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approveStatus,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        ApprovalStatus status = approveStatus != null ? ApprovalStatus.valueOf(approveStatus) : null;
        PageResult<OvertimeVO> result = overtimeService.listOvertimes(employeeId, deptId, status, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }
}