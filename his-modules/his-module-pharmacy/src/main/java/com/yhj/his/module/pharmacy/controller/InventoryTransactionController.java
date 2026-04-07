package com.yhj.his.module.pharmacy.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.entity.InventoryTransaction;
import com.yhj.his.module.pharmacy.enums.InventoryOperationType;
import com.yhj.his.module.pharmacy.service.InventoryTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存流水控制器
 */
@Tag(name = "库存流水", description = "库存流水记录管理接口")
@RestController
@RequestMapping("/api/pharmacy/v1/transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    @Operation(summary = "获取流水详情", description = "根据ID查询库存流水记录")
    @GetMapping("/{transactionId}")
    public Result<InventoryTransaction> getTransaction(
            @Parameter(description = "流水ID") @PathVariable String transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @Operation(summary = "根据流水号查询", description = "根据流水号查询库存流水记录")
    @GetMapping("/no/{transactionNo}")
    public Result<InventoryTransaction> getTransactionByNo(
            @Parameter(description = "流水号") @PathVariable String transactionNo) {
        return transactionService.getTransactionByNo(transactionNo);
    }

    @Operation(summary = "分页查询流水列表", description = "支持多条件分页查询库存流水")
    @GetMapping("/query")
    public Result<PageResult<InventoryTransaction>> queryTransactions(
            @Parameter(description = "药房ID") @RequestParam(required = false) String pharmacyId,
            @Parameter(description = "药品ID") @RequestParam(required = false) String drugId,
            @Parameter(description = "操作类型") @RequestParam(required = false) InventoryOperationType operationType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return transactionService.queryTransactions(pharmacyId, drugId, operationType, startDate, endDate, pageNum, pageSize);
    }

    @Operation(summary = "查询药品流水", description = "查询指定药品的所有库存流水记录")
    @GetMapping("/drug/{drugId}")
    public Result<List<InventoryTransaction>> getDrugTransactions(
            @Parameter(description = "药品ID") @PathVariable String drugId) {
        return transactionService.getDrugTransactions(drugId);
    }

    @Operation(summary = "查询药房流水", description = "分页查询指定药房的库存流水记录")
    @GetMapping("/pharmacy/{pharmacyId}")
    public Result<PageResult<InventoryTransaction>> getPharmacyTransactions(
            @Parameter(description = "药房ID") @PathVariable String pharmacyId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return transactionService.getPharmacyTransactions(pharmacyId, startDate, endDate, pageNum, pageSize);
    }

    @Operation(summary = "查询关联单据流水", description = "查询指定关联单据的库存流水记录")
    @GetMapping("/related/{relatedId}")
    public Result<List<InventoryTransaction>> getTransactionsByRelatedId(
            @Parameter(description = "关联单据ID") @PathVariable String relatedId) {
        return transactionService.getTransactionsByRelatedId(relatedId);
    }

    @Operation(summary = "查询入库流水", description = "分页查询入库流水记录")
    @GetMapping("/inbound")
    public Result<PageResult<InventoryTransaction>> getInboundTransactions(
            @Parameter(description = "药房ID") @RequestParam(required = false) String pharmacyId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return transactionService.getInboundTransactions(pharmacyId, startDate, endDate, pageNum, pageSize);
    }

    @Operation(summary = "查询出库流水", description = "分页查询出库流水记录")
    @GetMapping("/outbound")
    public Result<PageResult<InventoryTransaction>> getOutboundTransactions(
            @Parameter(description = "药房ID") @RequestParam(required = false) String pharmacyId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return transactionService.getOutboundTransactions(pharmacyId, startDate, endDate, pageNum, pageSize);
    }

    @Operation(summary = "库存流水汇总", description = "统计药房出入库汇总数据")
    @GetMapping("/summary")
    public Result<Object> getTransactionSummary(
            @Parameter(description = "药房ID") @RequestParam String pharmacyId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return transactionService.getTransactionSummary(pharmacyId, startDate, endDate);
    }
}