package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.service.DailySettlementService;
import com.yhj.his.module.finance.vo.DailySettlementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 日结管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/daily-settlement")
@RequiredArgsConstructor
@Tag(name = "日结管理", description = "收款日结、日结报表等")
public class DailySettlementController {

    private final DailySettlementService dailySettlementService;

    @PostMapping("/perform")
    @Operation(summary = "执行收款日结")
    public Result<DailySettlementVO> performDailySettlement(
            @Parameter(description = "日结日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate settlementDate) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("日结成功", dailySettlementService.performDailySettlement(operatorId, operatorName, settlementDate));
    }

    @PostMapping("/confirm/{settlementNo}")
    @Operation(summary = "确认日结")
    public Result<DailySettlementVO> confirmSettlement(@Parameter(description = "日结单号") @PathVariable String settlementNo) {
        // TODO: 从上下文获取当前确认人信息
        String confirmerId = "CONFIRMER001";
        String confirmerName = "财务主管";
        return Result.success("确认成功", dailySettlementService.confirmSettlement(settlementNo, confirmerId, confirmerName));
    }

    @GetMapping("/{settlementNo}")
    @Operation(summary = "根据日结单号查询")
    public Result<DailySettlementVO> getBySettlementNo(@Parameter(description = "日结单号") @PathVariable String settlementNo) {
        return Result.success(dailySettlementService.getBySettlementNo(settlementNo));
    }

    @GetMapping("/date")
    @Operation(summary = "根据日期和操作员查询")
    public Result<DailySettlementVO> getByDateAndOperator(
            @Parameter(description = "日结日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate settlementDate,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return Result.success(dailySettlementService.getByDateAndOperator(settlementDate, operatorId));
    }

    @GetMapping("/list/date")
    @Operation(summary = "根据日期查询日结记录列表")
    public Result<List<DailySettlementVO>> listByDate(
            @Parameter(description = "日结日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate settlementDate) {
        return Result.success(dailySettlementService.listByDate(settlementDate));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询日结记录")
    public Result<PageResult<DailySettlementVO>> pageList(
            @Parameter(description = "操作员ID") @RequestParam(required = false) String operatorId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(dailySettlementService.pageList(operatorId, startDate, endDate, status, pageNum, pageSize));
    }

    @GetMapping("/is-settled")
    @Operation(summary = "检查指定日期是否已日结")
    public Result<Boolean> isSettled(
            @Parameter(description = "日结日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate settlementDate,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return Result.success(dailySettlementService.isSettled(settlementDate, operatorId));
    }

    @GetMapping("/report")
    @Operation(summary = "获取日结报表数据")
    public Result<DailySettlementVO> getDailyReport(
            @Parameter(description = "日结日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate settlementDate,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return Result.success(dailySettlementService.getDailyReport(settlementDate, operatorId));
    }
}