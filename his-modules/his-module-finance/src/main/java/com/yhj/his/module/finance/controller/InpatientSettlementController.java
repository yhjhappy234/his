package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.InpatientSettleDTO;
import com.yhj.his.module.finance.service.InpatientSettlementService;
import com.yhj.his.module.finance.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.finance.vo.InpatientSettlementVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 住院收费管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/inpatient")
@RequiredArgsConstructor
@Tag(name = "住院收费管理", description = "费用查询、出院结算等")
public class InpatientSettlementController {

    private final InpatientSettlementService inpatientSettlementService;

    @GetMapping("/fee-summary/{admissionId}")
    @Operation(summary = "查询住院费用汇总")
    public Result<InpatientFeeSummaryVO> getFeeSummary(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(inpatientSettlementService.getFeeSummary(admissionId));
    }

    @PostMapping("/settle")
    @Operation(summary = "出院结算")
    public Result<SettlementResultVO> settle(@Valid @RequestBody InpatientSettleDTO dto) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "结算员";
        return Result.success("结算成功", inpatientSettlementService.settle(dto, operatorId, operatorName));
    }

    @GetMapping("/settlement/{settlementNo}")
    @Operation(summary = "根据结算单号查询")
    public Result<InpatientSettlementVO> getBySettlementNo(@Parameter(description = "结算单号") @PathVariable String settlementNo) {
        return Result.success(inpatientSettlementService.getBySettlementNo(settlementNo));
    }

    @GetMapping("/invoice/{invoiceNo}")
    @Operation(summary = "根据发票号查询")
    public Result<InpatientSettlementVO> getByInvoiceNo(@Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return Result.success(inpatientSettlementService.getByInvoiceNo(invoiceNo));
    }

    @GetMapping("/admission/{admissionId}")
    @Operation(summary = "根据住院ID查询结算记录")
    public Result<InpatientSettlementVO> getByAdmissionId(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(inpatientSettlementService.getByAdmissionId(admissionId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "根据患者ID查询结算记录")
    public Result<List<InpatientSettlementVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        return Result.success(inpatientSettlementService.listByPatientId(patientId));
    }

    @GetMapping("/settlement/page")
    @Operation(summary = "分页查询结算记录")
    public Result<PageResult<InpatientSettlementVO>> pageList(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "结算日期") @RequestParam(required = false) String settlementDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(inpatientSettlementService.pageList(patientId, settlementDate, status, pageNum, pageSize));
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算结算金额")
    public Result<InpatientFeeSummaryVO> calculateSettlement(
            @Parameter(description = "住院ID") @RequestParam String admissionId,
            @Parameter(description = "医保类型") @RequestParam(required = false) String insuranceType) {
        return Result.success(inpatientSettlementService.calculateSettlement(admissionId, insuranceType));
    }

    @GetMapping("/is-settled/{admissionId}")
    @Operation(summary = "检查住院是否已结算")
    public Result<Boolean> isSettled(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(inpatientSettlementService.isSettled(admissionId));
    }

    @PostMapping("/void/{settlementNo}")
    @Operation(summary = "作废结算")
    public Result<Void> voidSettlement(
            @Parameter(description = "结算单号") @PathVariable String settlementNo,
            @Parameter(description = "作废原因") @RequestParam String reason) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        inpatientSettlementService.voidSettlement(settlementNo, reason, operatorId);
        return Result.successVoid();
    }
}