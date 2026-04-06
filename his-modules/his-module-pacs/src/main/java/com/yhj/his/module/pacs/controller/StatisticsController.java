package com.yhj.his.module.pacs.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pacs.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计报表Controller
 */
@Tag(name = "统计报表", description = "检查工作量、设备使用率、报告时限等统计接口")
@RestController
@RequestMapping("/api/pacs/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "检查工作量统计(按日期)", description = "按日期统计检查工作量")
    @GetMapping("/workload/date")
    public Result<Map<String, Object>> getExamWorkloadByDate(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getExamWorkloadByDate(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "检查工作量统计(按类型)", description = "按检查类型统计工作量")
    @GetMapping("/workload/type")
    public Result<Map<String, Object>> getExamWorkloadByType(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getExamWorkloadByType(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "检查工作量统计(按机房)", description = "按机房统计工作量")
    @GetMapping("/workload/room")
    public Result<Map<String, Object>> getExamWorkloadByRoom(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getExamWorkloadByRoom(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "设备使用率统计", description = "统计设备使用率")
    @GetMapping("/equipment/usage")
    public Result<Map<String, Object>> getEquipmentUsageRate(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getEquipmentUsageRate(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "报告时限统计", description = "统计报告完成时限")
    @GetMapping("/report/turnaround")
    public Result<Map<String, Object>> getReportTurnaroundStats(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getReportTurnaroundStats(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "阳性率统计", description = "统计检查阳性率")
    @GetMapping("/positive-rate")
    public Result<Map<String, Object>> getPositiveRateStats(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getPositiveRateStats(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }

    @Operation(summary = "今日工作量概览", description = "今日工作量概览")
    @GetMapping("/today-overview")
    public Result<Map<String, Object>> getTodayOverview() {
        Map<String, Object> result = statisticsService.getTodayOverview();
        return Result.success(result);
    }

    @Operation(summary = "待处理任务统计", description = "统计待处理任务")
    @GetMapping("/pending-tasks")
    public Result<Map<String, Object>> getPendingTaskStats() {
        Map<String, Object> result = statisticsService.getPendingTaskStats();
        return Result.success(result);
    }

    @Operation(summary = "申请状态分布", description = "申请状态分布统计")
    @GetMapping("/request-status")
    public Result<List<Map<String, Object>>> getRequestStatusDistribution() {
        List<Map<String, Object>> result = statisticsService.getRequestStatusDistribution();
        return Result.success(result);
    }

    @Operation(summary = "报告状态分布", description = "报告状态分布统计")
    @GetMapping("/report-status")
    public Result<List<Map<String, Object>>> getReportStatusDistribution() {
        List<Map<String, Object>> result = statisticsService.getReportStatusDistribution();
        return Result.success(result);
    }

    @Operation(summary = "急诊检查统计", description = "急诊检查统计")
    @GetMapping("/emergency")
    public Result<Map<String, Object>> getEmergencyExamStats(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        Map<String, Object> result = statisticsService.getEmergencyExamStats(
                LocalDate.parse(startDate), LocalDate.parse(endDate));
        return Result.success(result);
    }
}