package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.OutpatientRefundDTO;
import com.yhj.his.module.finance.dto.OutpatientSettleDTO;
import com.yhj.his.module.finance.service.OutpatientBillingService;
import com.yhj.his.module.finance.vo.OutpatientBillingVO;
import com.yhj.his.module.finance.vo.PendingBillingVO;
import com.yhj.his.module.finance.vo.SettlementResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门诊收费管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/outpatient")
@RequiredArgsConstructor
@Tag(name = "门诊收费管理", description = "待收费项目查询、收费结算、发票打印、退费处理等")
public class OutpatientBillingController {

    private final OutpatientBillingService outpatientBillingService;

    @GetMapping("/pending")
    @Operation(summary = "获取待收费项目")
    public Result<PendingBillingVO> getPendingItems(@Parameter(description = "就诊ID") @RequestParam String visitId) {
        return Result.success(outpatientBillingService.getPendingItems(visitId));
    }

    @PostMapping("/settle")
    @Operation(summary = "收费结算")
    public Result<SettlementResultVO> settle(@Valid @RequestBody OutpatientSettleDTO dto) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("收费成功", outpatientBillingService.settle(dto, operatorId, operatorName));
    }

    @PostMapping("/refund")
    @Operation(summary = "退费处理")
    public Result<SettlementResultVO> refund(@Valid @RequestBody OutpatientRefundDTO dto) {
        // TODO: 从上下文获取当前操作员信息
        String operatorId = "OPERATOR001";
        String operatorName = "收费员";
        return Result.success("退费成功", outpatientBillingService.refund(dto, operatorId, operatorName));
    }

    @GetMapping("/billing/{billingNo}")
    @Operation(summary = "根据收费单号查询")
    public Result<OutpatientBillingVO> getByBillingNo(@Parameter(description = "收费单号") @PathVariable String billingNo) {
        return Result.success(outpatientBillingService.getByBillingNo(billingNo));
    }

    @GetMapping("/invoice/{invoiceNo}")
    @Operation(summary = "根据发票号查询")
    public Result<OutpatientBillingVO> getByInvoiceNo(@Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return Result.success(outpatientBillingService.getByInvoiceNo(invoiceNo));
    }

    @GetMapping("/visit/{visitId}")
    @Operation(summary = "根据就诊ID查询收费记录")
    public Result<List<OutpatientBillingVO>> listByVisitId(@Parameter(description = "就诊ID") @PathVariable String visitId) {
        return Result.success(outpatientBillingService.listByVisitId(visitId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "根据患者ID查询收费记录")
    public Result<List<OutpatientBillingVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        return Result.success(outpatientBillingService.listByPatientId(patientId));
    }

    @GetMapping("/billing/page")
    @Operation(summary = "分页查询收费记录")
    public Result<PageResult<OutpatientBillingVO>> pageList(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "收费日期") @RequestParam(required = false) String billingDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(outpatientBillingService.pageList(patientId, billingDate, status, pageNum, pageSize));
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算费用")
    public Result<PendingBillingVO> calculateFee(
            @Parameter(description = "就诊ID") @RequestParam String visitId,
            @Parameter(description = "医保类型") @RequestParam(required = false) String insuranceType) {
        return Result.success(outpatientBillingService.calculateFee(visitId, insuranceType));
    }

    @PostMapping("/print-invoice/{billingNo}")
    @Operation(summary = "打印发票")
    public Result<OutpatientBillingVO> printInvoice(@Parameter(description = "收费单号") @PathVariable String billingNo) {
        return Result.success(outpatientBillingService.printInvoice(billingNo));
    }
}