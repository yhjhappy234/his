package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.ApprovalStatus;
import com.yhj.his.module.hr.service.PerformanceService;
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
 * 绩效管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/performance")
@RequiredArgsConstructor
@Tag(name = "绩效管理", description = "绩效考核管理接口")
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping
    @Operation(summary = "创建绩效评分", description = "创建绩效评分")
    public Result<PerformanceEvaluationVO> createEvaluation(@Valid @RequestBody PerformanceEvaluationCreateDTO dto) {
        // 考评人信息从上下文获取，这里简化处理
        PerformanceEvaluationVO vo = performanceService.createEvaluation(dto, "SYSTEM", "系统管理员");
        return Result.success(vo);
    }

    @PostMapping("/approve")
    @Operation(summary = "审核绩效评分", description = "审核绩效评分")
    public Result<PerformanceEvaluationVO> approveEvaluation(@Valid @RequestBody PerformanceApprovalDTO dto) {
        // 审核人信息从上下文获取，这里简化处理
        PerformanceEvaluationVO vo = performanceService.approveEvaluation(dto, "SYSTEM", "系统管理员");
        return Result.success(vo);
    }

    @GetMapping("/{evaluationId}")
    @Operation(summary = "获取绩效详情", description = "根据ID获取绩效评分详情")
    public Result<PerformanceEvaluationVO> getEvaluation(@Parameter(description = "绩效评分ID") @PathVariable String evaluationId) {
        PerformanceEvaluationVO vo = performanceService.getEvaluationById(evaluationId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查询员工绩效列表", description = "查询指定员工的绩效评分列表")
    public Result<List<PerformanceEvaluationVO>> listEvaluationsByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<PerformanceEvaluationVO> list = performanceService.listEvaluationsByEmployee(employeeId);
        return Result.success(list);
    }

    @GetMapping("/pending/{approverId}")
    @Operation(summary = "查询待审核绩效", description = "查询指定审核人的待审核绩效评分")
    public Result<List<PerformanceEvaluationVO>> listPendingEvaluations(
            @Parameter(description = "审核人ID") @PathVariable String approverId) {
        List<PerformanceEvaluationVO> list = performanceService.listPendingEvaluations(approverId);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询绩效", description = "分页查询绩效评分列表")
    public Result<PageResult<PerformanceEvaluationVO>> listEvaluations(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "审核状态") @RequestParam(required = false) String approveStatus,
            @Parameter(description = "考核类型") @RequestParam(required = false) String evaluationType,
            @Parameter(description = "周期开始") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate periodStart,
            @Parameter(description = "周期结束") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate periodEnd,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        ApprovalStatus status = approveStatus != null ? ApprovalStatus.valueOf(approveStatus) : null;
        PageResult<PerformanceEvaluationVO> result = performanceService.listEvaluations(employeeId, deptId, status, evaluationType, periodStart, periodEnd, pageNum, pageSize);
        return Result.success(result);
    }
}