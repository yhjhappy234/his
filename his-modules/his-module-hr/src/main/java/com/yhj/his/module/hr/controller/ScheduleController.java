package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.ScheduleType;
import com.yhj.his.module.hr.service.ScheduleService;
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
 * 排班管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/schedule")
@RequiredArgsConstructor
@Tag(name = "排班管理", description = "排班管理接口")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "创建排班", description = "创建排班")
    public Result<ScheduleVO> createSchedule(@Valid @RequestBody ScheduleCreateDTO dto) {
        ScheduleVO vo = scheduleService.createSchedule(dto);
        return Result.success(vo);
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "更新排班", description = "更新排班信息")
    public Result<ScheduleVO> updateSchedule(
            @Parameter(description = "排班ID") @PathVariable String scheduleId,
            @Valid @RequestBody ScheduleCreateDTO dto) {
        ScheduleVO vo = scheduleService.updateSchedule(scheduleId, dto);
        return Result.success(vo);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "删除排班", description = "删除排班")
    public Result<Void> deleteSchedule(@Parameter(description = "排班ID") @PathVariable String scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return Result.success();
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "获取排班详情", description = "根据ID获取排班详情")
    public Result<ScheduleVO> getSchedule(@Parameter(description = "排班ID") @PathVariable String scheduleId) {
        ScheduleVO vo = scheduleService.getScheduleById(scheduleId);
        return Result.success(vo);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建排班", description = "批量创建排班")
    public Result<List<ScheduleVO>> batchCreateSchedule(@Valid @RequestBody BatchScheduleDTO dto) {
        List<ScheduleVO> list = scheduleService.batchCreateSchedule(dto);
        return Result.success(list);
    }

    @GetMapping("/employee/{employeeId}/date/{scheduleDate}")
    @Operation(summary = "根据员工和日期获取排班", description = "根据员工和日期获取排班信息")
    public Result<ScheduleVO> getScheduleByEmployeeAndDate(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "排班日期") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scheduleDate) {
        ScheduleVO vo = scheduleService.getScheduleByEmployeeAndDate(employeeId, scheduleDate);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}/range")
    @Operation(summary = "查询员工排班列表", description = "查询员工指定日期范围的排班列表")
    public Result<List<ScheduleVO>> listSchedulesByEmployeeAndDateRange(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<ScheduleVO> list = scheduleService.listSchedulesByEmployeeAndDateRange(employeeId, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/dept/{deptId}/range")
    @Operation(summary = "查询科室排班列表", description = "查询科室指定日期范围的排班列表")
    public Result<List<ScheduleVO>> listSchedulesByDeptAndDateRange(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<ScheduleVO> list = scheduleService.listSchedulesByDeptAndDateRange(deptId, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询排班", description = "分页查询排班列表")
    public Result<PageResult<ScheduleVO>> listSchedules(
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "班次类型") @RequestParam(required = false) String scheduleType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        ScheduleType type = scheduleType != null ? ScheduleType.valueOf(scheduleType) : null;
        PageResult<ScheduleVO> result = scheduleService.listSchedules(deptId, employeeId, type, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping("/{scheduleId}/adjust")
    @Operation(summary = "调整排班", description = "调整排班（换班）")
    public Result<ScheduleVO> adjustSchedule(
            @Parameter(description = "排班ID") @PathVariable String scheduleId,
            @Parameter(description = "新员工ID") @RequestParam String newEmployeeId,
            @Parameter(description = "换班原因") @RequestParam String reason) {
        ScheduleVO vo = scheduleService.adjustSchedule(scheduleId, newEmployeeId, reason);
        return Result.success(vo);
    }
}