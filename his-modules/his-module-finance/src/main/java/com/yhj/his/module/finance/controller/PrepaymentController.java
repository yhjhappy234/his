package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.PrepaymentDTO;
import com.yhj.his.module.finance.service.PrepaymentService;
import com.yhj.his.module.finance.vo.PrepaymentBalanceVO;
import com.yhj.his.module.finance.vo.PrepaymentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预交金管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/prepayment")
@RequiredArgsConstructor
@Tag(name = "预交金管理", description = "预交金缴纳、退还、余额查询等")
public class PrepaymentController {

    private final PrepaymentService prepaymentService;

    @PostMapping("/deposit")
    @Operation(summary = "缴纳预交金")
    public Result<PrepaymentVO> deposit(@Valid @RequestBody PrepaymentDTO dto) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("缴纳成功", prepaymentService.deposit(dto, operatorId, operatorName));
    }

    @PostMapping("/refund")
    @Operation(summary = "退还预交金")
    public Result<PrepaymentVO> refund(
            @Parameter(description = "住院ID") @RequestParam String admissionId,
            @Parameter(description = "退还金额") @RequestParam BigDecimal refundAmount,
            @Parameter(description = "退还方式") @RequestParam String refundMethod,
            @Parameter(description = "退还原因") @RequestParam(required = false) String reason) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("退还成功", prepaymentService.refund(admissionId, refundAmount, refundMethod, reason, operatorId, operatorName));
    }

    @GetMapping("/balance/{admissionId}")
    @Operation(summary = "查询预交金余额")
    public Result<PrepaymentBalanceVO> getBalance(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(prepaymentService.getBalance(admissionId));
    }

    @GetMapping("/list/admission/{admissionId}")
    @Operation(summary = "根据住院ID查询预交金记录")
    public Result<List<PrepaymentVO>> listByAdmissionId(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(prepaymentService.listByAdmissionId(admissionId));
    }

    @GetMapping("/list/patient/{patientId}")
    @Operation(summary = "根据患者ID查询预交金记录")
    public Result<List<PrepaymentVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        return Result.success(prepaymentService.listByPatientId(patientId));
    }

    @GetMapping("/{prepaymentNo}")
    @Operation(summary = "根据预交金单号查询")
    public Result<PrepaymentVO> getByPrepaymentNo(@Parameter(description = "预交金单号") @PathVariable String prepaymentNo) {
        return Result.success(prepaymentService.getByPrepaymentNo(prepaymentNo));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预交金记录")
    public Result<PageResult<PrepaymentVO>> pageList(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "住院ID") @RequestParam(required = false) String admissionId,
            @Parameter(description = "类型") @RequestParam(required = false) String depositType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(prepaymentService.pageList(patientId, admissionId, depositType, pageNum, pageSize));
    }

    @GetMapping("/total/{admissionId}")
    @Operation(summary = "计算住院预交金总额")
    public Result<BigDecimal> calculateTotalDeposit(@Parameter(description = "住院ID") @PathVariable String admissionId) {
        return Result.success(prepaymentService.calculateTotalDeposit(admissionId));
    }

    @GetMapping("/warning/{admissionId}")
    @Operation(summary = "检查预交金不足预警")
    public Result<Boolean> checkDepositWarning(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "预计费用") @RequestParam BigDecimal estimatedCost) {
        return Result.success(prepaymentService.checkDepositWarning(admissionId, estimatedCost));
    }
}