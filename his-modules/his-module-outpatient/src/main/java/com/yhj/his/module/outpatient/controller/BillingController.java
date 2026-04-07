package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.BillingSettleRequest;
import com.yhj.his.module.outpatient.dto.ExaminationRequestDto;
import com.yhj.his.module.outpatient.entity.BillingItem;
import com.yhj.his.module.outpatient.entity.BillingSettlement;
import com.yhj.his.module.outpatient.entity.ExaminationRequest;
import com.yhj.his.module.outpatient.service.BillingItemService;
import com.yhj.his.module.outpatient.service.BillingSettlementService;
import com.yhj.his.module.outpatient.service.ExaminationRequestService;
import com.yhj.his.module.outpatient.vo.BillingResultVO;
import com.yhj.his.module.outpatient.vo.PendingBillingVO;
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
 * 收费管理Controller
 */
@Tag(name = "收费管理", description = "收费结算、退费、发票等接口")
@RestController
@RequestMapping("/api/outpatient/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingItemService billingItemService;
    private final BillingSettlementService billingSettlementService;
    private final ExaminationRequestService examinationRequestService;

    @Operation(summary = "获取待收费项目", description = "获取挂号关联的待收费项目")
    @GetMapping("/pending")
    public Result<PendingBillingVO> getPendingBilling(
            @Parameter(description = "挂号ID") @RequestParam String registrationId) {
        PendingBillingVO result = billingItemService.getPendingBilling(registrationId);
        return Result.success(result);
    }

    @Operation(summary = "查询收费项目列表", description = "查询收费项目列表")
    @GetMapping("/items")
    public Result<PageResult<BillingItem>> listBillingItems(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "挂号ID") @RequestParam(required = false) String registrationId,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BillingItem> result = billingItemService.listBillingItems(patientId, registrationId, payStatus, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "收费结算", description = "收费结算")
    @PostMapping("/settle")
    public Result<BillingResultVO> settleBilling(@Valid @RequestBody BillingSettleRequest request) {
        BillingResultVO result = billingSettlementService.settle(request);
        return Result.success("收费成功", result);
    }

    @Operation(summary = "退费", description = "退费处理")
    @PostMapping("/refund/{settlementId}")
    public Result<BillingResultVO> refundBilling(
            @Parameter(description = "结算ID") @PathVariable String settlementId,
            @Parameter(description = "退费原因") @RequestParam(required = false) String reason) {
        BillingResultVO result = billingSettlementService.refund(settlementId, reason);
        return Result.success("退费成功", result);
    }

    @Operation(summary = "查询结算记录", description = "查询结算记录")
    @GetMapping("/settlement/{id}")
    public Result<BillingSettlement> getSettlement(
            @Parameter(description = "结算ID") @PathVariable String id) {
        BillingSettlement settlement = billingSettlementService.getSettlementDetail(id);
        return Result.success(settlement);
    }

    @Operation(summary = "根据发票号查询结算记录", description = "根据发票号查询结算记录")
    @GetMapping("/settlement/invoice/{invoiceNo}")
    public Result<BillingSettlement> getSettlementByInvoiceNo(
            @Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return billingSettlementService.findByInvoiceNo(invoiceNo)
                .map(Result::success)
                .orElse(Result.error("结算记录不存在"));
    }

    @Operation(summary = "分页查询结算记录列表", description = "分页查询结算记录列表")
    @GetMapping("/settlements")
    public Result<PageResult<BillingSettlement>> listSettlements(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "挂号ID") @RequestParam(required = false) String registrationId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BillingSettlement> result = billingSettlementService.listSettlements(patientId, registrationId, status, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "开立检查检验申请", description = "开立检查检验申请单")
    @PostMapping("/examination/create")
    public Result<ExaminationRequest> createExaminationRequest(@Valid @RequestBody ExaminationRequestDto request) {
        ExaminationRequest result = examinationRequestService.createRequest(request);
        return Result.success("申请开立成功", result);
    }

    @Operation(summary = "查询检查检验申请", description = "查询检查检验申请")
    @GetMapping("/examination/{id}")
    public Result<ExaminationRequest> getExaminationRequest(
            @Parameter(description = "申请ID") @PathVariable String id) {
        ExaminationRequest request = examinationRequestService.getRequestDetail(id);
        return Result.success(request);
    }

    @Operation(summary = "取消检查检验申请", description = "取消检查检验申请")
    @PostMapping("/examination/cancel/{id}")
    public Result<Void> cancelExaminationRequest(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        examinationRequestService.cancelRequest(id, reason);
        return Result.successVoid();
    }

    @Operation(summary = "完成检查检验", description = "完成检查检验")
    @PostMapping("/examination/complete/{id}")
    public Result<ExaminationRequest> completeExaminationRequest(
            @Parameter(description = "申请ID") @PathVariable String id) {
        ExaminationRequest request = examinationRequestService.completeRequest(id);
        return Result.success("完成成功", request);
    }

    @Operation(summary = "查询检查检验申请列表", description = "查询检查检验申请列表")
    @GetMapping("/examination/list")
    public Result<PageResult<ExaminationRequest>> listExaminationRequests(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "申请类型") @RequestParam(required = false) String requestType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ExaminationRequest> result = examinationRequestService.listRequests(patientId, doctorId, requestType, status, payStatus, startDate, endDate, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "获取发票", description = "根据发票号获取发票信息")
    @GetMapping("/invoice/{invoiceNo}")
    public Result<BillingSettlement> getInvoice(
            @Parameter(description = "发票号") @PathVariable String invoiceNo) {
        return billingSettlementService.findByInvoiceNo(invoiceNo)
                .map(Result::success)
                .orElse(Result.error("发票不存在"));
    }
}