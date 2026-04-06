package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 薪资管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/salary")
@RequiredArgsConstructor
@Tag(name = "薪资管理", description = "薪资管理接口")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping("/calculate")
    @Operation(summary = "计算薪资", description = "计算薪资")
    public Result<List<SalaryRecordVO>> calculateSalary(@Valid @RequestBody SalaryCalculateDTO dto) {
        // 计算人信息从上下文获取，这里简化处理
        List<SalaryRecordVO> list = salaryService.calculateSalary(dto, "SYSTEM", "系统管理员");
        return Result.success(list);
    }

    @PostMapping("/approve")
    @Operation(summary = "审核薪资", description = "审核薪资")
    public Result<SalaryRecordVO> approveSalary(@Valid @RequestBody SalaryApprovalDTO dto) {
        // 审核人信息从上下文获取，这里简化处理
        SalaryRecordVO vo = salaryService.approveSalary(dto, "SYSTEM", "系统管理员");
        return Result.success(vo);
    }

    @PostMapping("/{salaryId}/pay")
    @Operation(summary = "发放薪资", description = "发放薪资")
    public Result<SalaryRecordVO> paySalary(@Parameter(description = "薪资记录ID") @PathVariable String salaryId) {
        SalaryRecordVO vo = salaryService.paySalary(salaryId);
        return Result.success(vo);
    }

    @GetMapping("/{salaryId}")
    @Operation(summary = "获取薪资详情", description = "根据ID获取薪资记录详情")
    public Result<SalaryRecordVO> getSalaryRecord(@Parameter(description = "薪资记录ID") @PathVariable String salaryId) {
        SalaryRecordVO vo = salaryService.getSalaryRecordById(salaryId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}/month/{salaryMonth}")
    @Operation(summary = "根据员工和月份获取薪资", description = "根据员工和月份获取薪资记录")
    public Result<SalaryRecordVO> getSalaryRecordByEmployeeAndMonth(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "薪资月份(yyyy-MM)") @PathVariable String salaryMonth) {
        SalaryRecordVO vo = salaryService.getSalaryRecordByEmployeeAndMonth(employeeId, salaryMonth);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "查询员工薪资列表", description = "查询指定员工的薪资记录列表")
    public Result<List<SalaryRecordVO>> listSalaryRecordsByEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId) {
        List<SalaryRecordVO> list = salaryService.listSalaryRecordsByEmployee(employeeId);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询薪资", description = "分页查询薪资记录列表")
    public Result<PageResult<SalaryRecordVO>> listSalaryRecords(
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "薪资月份") @RequestParam(required = false) String salaryMonth,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<SalaryRecordVO> result = salaryService.listSalaryRecords(employeeId, deptId, salaryMonth, status, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping("/calculate/{employeeId}/{salaryMonth}")
    @Operation(summary = "计算单个员工薪资", description = "计算单个员工某月份薪资")
    public Result<SalaryRecordVO> calculateEmployeeSalary(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "薪资月份(yyyy-MM)") @PathVariable String salaryMonth) {
        SalaryRecordVO vo = salaryService.calculateEmployeeSalary(employeeId, salaryMonth);
        return Result.success(vo);
    }

    @GetMapping("/sum/{salaryMonth}")
    @Operation(summary = "统计月份薪资总额", description = "统计某月份薪资总额")
    public Result<BigDecimal> sumSalaryByMonth(
            @Parameter(description = "薪资月份(yyyy-MM)") @PathVariable String salaryMonth) {
        BigDecimal sum = salaryService.sumSalaryByMonth(salaryMonth);
        return Result.success(sum);
    }
}