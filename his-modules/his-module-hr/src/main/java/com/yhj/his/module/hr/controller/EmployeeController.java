package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.EmployeeStatus;
import com.yhj.his.module.hr.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/employee")
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "员工信息管理接口")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "创建员工", description = "创建新员工信息")
    public Result<EmployeeVO> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
        EmployeeVO vo = employeeService.createEmployee(dto);
        return Result.success(vo);
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "更新员工", description = "更新员工信息")
    public Result<EmployeeVO> updateEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Valid @RequestBody EmployeeUpdateDTO dto) {
        EmployeeVO vo = employeeService.updateEmployee(employeeId, dto);
        return Result.success(vo);
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "删除员工", description = "删除员工信息")
    public Result<Void> deleteEmployee(@Parameter(description = "员工ID") @PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return Result.success();
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "获取员工详情", description = "根据ID获取员工详情")
    public Result<EmployeeVO> getEmployee(@Parameter(description = "员工ID") @PathVariable String employeeId) {
        EmployeeVO vo = employeeService.getEmployeeById(employeeId);
        return Result.success(vo);
    }

    @GetMapping("/no/{employeeNo}")
    @Operation(summary = "根据工号获取员工", description = "根据工号获取员工信息")
    public Result<EmployeeVO> getEmployeeByNo(@Parameter(description = "员工工号") @PathVariable String employeeNo) {
        EmployeeVO vo = employeeService.getEmployeeByNo(employeeNo);
        return Result.success(vo);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询员工", description = "分页查询员工列表")
    public Result<PageResult<EmployeeVO>> listEmployees(
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        EmployeeStatus employeeStatus = status != null ? EmployeeStatus.valueOf(status) : null;
        PageResult<EmployeeVO> result = employeeService.listEmployees(deptId, employeeStatus, keyword, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/dept/{deptId}")
    @Operation(summary = "查询科室员工", description = "查询指定科室的员工列表")
    public Result<List<EmployeeVO>> listEmployeesByDept(@Parameter(description = "科室ID") @PathVariable String deptId) {
        List<EmployeeVO> list = employeeService.listEmployeesByDept(deptId);
        return Result.success(list);
    }

    @PostMapping("/{employeeId}/resign")
    @Operation(summary = "员工离职", description = "处理员工离职")
    public Result<EmployeeVO> resignEmployee(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @RequestBody EmployeeResignDTO dto) {
        EmployeeVO vo = employeeService.resignEmployee(employeeId, dto);
        return Result.success(vo);
    }
}