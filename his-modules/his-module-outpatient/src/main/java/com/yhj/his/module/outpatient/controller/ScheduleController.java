package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.ScheduleCreateRequest;
import com.yhj.his.module.outpatient.service.ScheduleService;
import com.yhj.his.module.outpatient.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班管理Controller
 */
@Tag(name = "排班管理", description = "医生排班设置、号源管理等接口")
@RestController("outpatientScheduleController")
@RequestMapping("/api/outpatient/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "创建排班", description = "创建医生排班信息")
    @PostMapping("/create")
    public Result<ScheduleVO> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        ScheduleVO schedule = scheduleService.createSchedule(request);
        return Result.success("排班创建成功", schedule);
    }

    @Operation(summary = "更新排班", description = "更新排班信息")
    @PutMapping("/update/{id}")
    public Result<ScheduleVO> updateSchedule(
            @Parameter(description = "排班ID") @PathVariable String id,
            @Valid @RequestBody ScheduleCreateRequest request) {
        ScheduleVO schedule = scheduleService.updateSchedule(id, request);
        return Result.success("排班更新成功", schedule);
    }

    @Operation(summary = "停诊", description = "设置排班停诊")
    @PostMapping("/stop/{id}")
    public Result<ScheduleVO> stopSchedule(
            @Parameter(description = "排班ID") @PathVariable String id,
            @Parameter(description = "停诊原因") @RequestParam(required = false) String reason) {
        ScheduleVO schedule = scheduleService.stopSchedule(id, reason);
        return Result.success("停诊成功", schedule);
    }

    @Operation(summary = "恢复排班", description = "恢复停诊的排班")
    @PostMapping("/restore/{id}")
    public Result<ScheduleVO> restoreSchedule(
            @Parameter(description = "排班ID") @PathVariable String id) {
        ScheduleVO schedule = scheduleService.restoreSchedule(id);
        return Result.success("排班恢复成功", schedule);
    }

    @Operation(summary = "根据ID查询排班", description = "根据排班ID查询排班信息")
    @GetMapping("/get/{id}")
    public Result<ScheduleVO> getScheduleById(
            @Parameter(description = "排班ID") @PathVariable String id) {
        ScheduleVO schedule = scheduleService.getScheduleDetail(id);
        return Result.success(schedule);
    }

    @Operation(summary = "分页查询排班列表", description = "分页查询排班列表")
    @GetMapping("/list")
    public Result<PageResult<ScheduleVO>> listSchedules(
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ScheduleVO> result = scheduleService.listSchedules(deptId, doctorId, startDate, endDate, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "根据日期查询排班列表", description = "查询某日排班列表")
    @GetMapping("/listByDate")
    public Result<List<ScheduleVO>> listSchedulesByDate(
            @Parameter(description = "日期") @RequestParam LocalDate date) {
        List<ScheduleVO> schedules = scheduleService.listSchedulesByDate(date);
        return Result.success(schedules);
    }

    @Operation(summary = "查询可用排班列表", description = "查询当天可预约的排班列表")
    @GetMapping("/available")
    public Result<List<ScheduleVO>> listAvailableSchedule(
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<ScheduleVO> schedules = scheduleService.listAvailableSchedules(deptId, date);
        return Result.success(schedules);
    }

    @Operation(summary = "更新号源数量", description = "更新排班号源数量")
    @PostMapping("/quota/{id}")
    public Result<Boolean> updateQuota(
            @Parameter(description = "排班ID") @PathVariable String id,
            @Parameter(description = "总号源数") @RequestParam Integer totalQuota) {
        boolean result = scheduleService.updateQuota(id, totalQuota);
        return Result.success("更新成功", result);
    }

    @Operation(summary = "删除排班", description = "删除排班信息")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteSchedule(
            @Parameter(description = "排班ID") @PathVariable String id) {
        scheduleService.deleteSchedule(id);
        return Result.successVoid();
    }
}