package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.enums.AttendanceStatus;
import com.yhj.his.module.hr.service.AttendanceService;
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
 * 考勤管理Controller
 */
@RestController
@RequestMapping("/api/hr/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "考勤管理", description = "考勤管理接口")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/clock-in")
    @Operation(summary = "签到打卡", description = "员工签到打卡")
    public Result<AttendanceVO> clockIn(@Valid @RequestBody AttendanceClockDTO dto) {
        dto.setClockType("签到");
        AttendanceVO vo = attendanceService.clock(dto);
        return Result.success(vo);
    }

    @PostMapping("/clock-out")
    @Operation(summary = "签退打卡", description = "员工签退打卡")
    public Result<AttendanceVO> clockOut(@Valid @RequestBody AttendanceClockDTO dto) {
        dto.setClockType("签退");
        AttendanceVO vo = attendanceService.clockOut(dto);
        return Result.success(vo);
    }

    @GetMapping("/{attendanceId}")
    @Operation(summary = "获取考勤详情", description = "根据ID获取考勤详情")
    public Result<AttendanceVO> getAttendance(@Parameter(description = "考勤ID") @PathVariable String attendanceId) {
        AttendanceVO vo = attendanceService.getAttendanceById(attendanceId);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}/date/{attendanceDate}")
    @Operation(summary = "根据员工和日期获取考勤", description = "根据员工和日期获取考勤记录")
    public Result<AttendanceVO> getAttendanceByEmployeeAndDate(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "考勤日期") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate attendanceDate) {
        AttendanceVO vo = attendanceService.getAttendanceByEmployeeAndDate(employeeId, attendanceDate);
        return Result.success(vo);
    }

    @GetMapping("/employee/{employeeId}/range")
    @Operation(summary = "查询员工考勤列表", description = "查询员工指定日期范围的考勤列表")
    public Result<List<AttendanceVO>> listAttendancesByEmployeeAndDateRange(
            @Parameter(description = "员工ID") @PathVariable String employeeId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<AttendanceVO> list = attendanceService.listAttendancesByEmployeeAndDateRange(employeeId, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询考勤", description = "分页查询考勤列表")
    public Result<PageResult<AttendanceVO>> listAttendances(
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "员工ID") @RequestParam(required = false) String employeeId,
            @Parameter(description = "考勤状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        AttendanceStatus attendanceStatus = status != null ? AttendanceStatus.valueOf(status) : null;
        PageResult<AttendanceVO> result = attendanceService.listAttendances(deptId, employeeId, attendanceStatus, startDate, endDate, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "考勤统计", description = "获取员工考勤统计")
    public Result<AttendanceStatisticsVO> getAttendanceStatistics(
            @Parameter(description = "员工ID") @RequestParam String employeeId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        AttendanceStatisticsVO vo = attendanceService.getAttendanceStatistics(employeeId, startDate, endDate);
        return Result.success(vo);
    }

    @PostMapping("/generate")
    @Operation(summary = "生成考勤记录", description = "根据排班生成考勤记录")
    public Result<List<AttendanceVO>> generateAttendances(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId) {
        List<AttendanceVO> list = attendanceService.generateAttendances(startDate, endDate, deptId);
        return Result.success(list);
    }
}