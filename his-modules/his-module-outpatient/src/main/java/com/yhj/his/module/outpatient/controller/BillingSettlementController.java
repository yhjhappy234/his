package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.BillingSettleRequest;
import com.yhj.his.module.outpatient.entity.BillingSettlement;
import com.yhj.his.module.outpatient.service.BillingSettlementService;
import com.yhj.his.module.outpatient.vo.BillingResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收费结算控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/billing-settlements")
@RequiredArgsConstructor
@Tag(name = "收费结算管理", description = "收费结算和退费接口")
public class BillingSettlementController {

    private final BillingSettlementService billingSettlementService;

    @PostMapping("/settle")
    @Operation(summary = "收费结算", description = "收费结算处理")
    public Result<BillingResultVO> settle(@Valid @RequestBody BillingSettleRequest request) {
        BillingResultVO result = billingSettlementService.settle(request);
        return Result.success("收费结算成功", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取结算详情", description = "根据ID获取结算详细信息")
    public Result<BillingSettlement> getSettlement(@Parameter(description = "结算ID") @PathVariable String id) {
        BillingSettlement settlement = billingSettlementService.getSettlementDetail(id);
        return Result.success(settlement);
    }

    @GetMapping("/settlement-no/{settlementNo}")
    @Operation(summary = "根据结算单号查询", description = "根据结算单号获取结算详情")
    public Result<BillingSettlement> findBySettlementNo(
            @Parameter(description = "结算单号") @PathVariable String settlementNo) {
        return billingSettlementService.findBySettlementNo(settlementNo)
                .map(Result::success)
                .orElse(Result.success(null));
    }

    @GetMapping("/invoice-no/{invoiceNo}")
    @Operation(summary = "根据发票号查询", description = "根据发票号获取结算详情")
    public Result<BillingSettlement> findByInvoiceNo(
            @Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return billingSettlementService.findByInvoiceNo(invoiceNo)
                .map(Result::success)
                .orElse(Result.success(null));
    }

    @GetMapping
    @Operation(summary = "查询结算列表", description = "分页查询结算列表")
    public Result<PageResult<BillingSettlement>> listSettlements(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "挂号ID") @RequestParam(required = false) String registrationId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("settleTime").descending());
        PageResult<BillingSettlement> result = billingSettlementService.listSettlements(patientId, registrationId, status, pageable);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "查询患者结算记录", description = "查询患者的所有结算记录")
    public Result<List<BillingSettlement>> listPatientSettlements(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<BillingSettlement> settlements = billingSettlementService.listPatientSettlements(patientId);
        return Result.success(settlements);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "退费", description = "退费处理")
    public Result<BillingResultVO> refund(
            @Parameter(description = "结算ID") @PathVariable String id,
            @Parameter(description = "退费原因") @RequestParam(required = false) String reason) {
        BillingResultVO result = billingSettlementService.refund(id, reason);
        return Result.success("退费成功", result);
    }
}