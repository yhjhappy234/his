package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.ExaminationRequestDto;
import com.yhj.his.module.outpatient.entity.ExaminationRequest;
import com.yhj.his.module.outpatient.service.ExaminationRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 检查检验申请控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/examination-requests")
@RequiredArgsConstructor
@Tag(name = "检查检验申请管理", description = "检查检验申请的增删改查接口")
public class ExaminationRequestController {

    private final ExaminationRequestService examinationRequestService;

    @PostMapping
    @Operation(summary = "创建检查检验申请", description = "创建检查检验申请")
    public Result<ExaminationRequest> createRequest(@Valid @RequestBody ExaminationRequestDto request) {
        ExaminationRequest examRequest = examinationRequestService.createRequest(request);
        return Result.success("创建检查检验申请成功", examRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取申请详情", description = "根据ID获取检查检验申请详细信息")
    public Result<ExaminationRequest> getRequest(@Parameter(description = "申请ID") @PathVariable String id) {
        ExaminationRequest examRequest = examinationRequestService.getRequestDetail(id);
        return Result.success(examRequest);
    }

    @GetMapping("/request-no/{requestNo}")
    @Operation(summary = "根据申请单号查询", description = "根据申请单号获取检查检验申请详情")
    public Result<ExaminationRequest> findByRequestNo(
            @Parameter(description = "申请单号") @PathVariable String requestNo) {
        return examinationRequestService.findByRequestNo(requestNo)
                .map(Result::success)
                .orElse(Result.success(null));
    }

    @GetMapping
    @Operation(summary = "查询申请列表", description = "分页查询检查检验申请列表")
    public Result<PageResult<ExaminationRequest>> listRequests(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "申请类型") @RequestParam(required = false) String requestType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("requestDate").descending());
        PageResult<ExaminationRequest> result = examinationRequestService.listRequests(patientId, doctorId, requestType, status, payStatus, startDate, endDate, pageable);
        return Result.success(result);
    }

    @GetMapping("/registration/{registrationId}")
    @Operation(summary = "查询挂号关联申请", description = "查询挂号关联的检查检验申请列表")
    public Result<List<ExaminationRequest>> listRequestsByRegistration(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        List<ExaminationRequest> requests = examinationRequestService.listRequestsByRegistration(registrationId);
        return Result.success(requests);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "查询患者申请", description = "查询患者的所有检查检验申请")
    public Result<List<ExaminationRequest>> listPatientRequests(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<ExaminationRequest> requests = examinationRequestService.listPatientRequests(patientId);
        return Result.success(requests);
    }

    @GetMapping("/patient/{patientId}/pending")
    @Operation(summary = "查询待检查申请", description = "查询患者待检查申请")
    public Result<List<ExaminationRequest>> listPendingRequests(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<ExaminationRequest> requests = examinationRequestService.listPendingRequests(patientId);
        return Result.success(requests);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新申请", description = "更新检查检验申请")
    public Result<ExaminationRequest> updateRequest(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Valid @RequestBody ExaminationRequestDto request) {
        ExaminationRequest examRequest = examinationRequestService.updateRequest(id, request);
        return Result.success("更新申请成功", examRequest);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消申请", description = "取消检查检验申请")
    public Result<Void> cancelRequest(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        examinationRequestService.cancelRequest(id, reason);
        return Result.successVoid();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成检查", description = "完成检查检验")
    public Result<ExaminationRequest> completeRequest(@Parameter(description = "申请ID") @PathVariable String id) {
        ExaminationRequest examRequest = examinationRequestService.completeRequest(id);
        return Result.success("完成检查成功", examRequest);
    }

    @PutMapping("/{id}/pay-status")
    @Operation(summary = "更新收费状态", description = "更新申请收费状态")
    public Result<ExaminationRequest> updatePayStatus(
            @Parameter(description = "申请ID") @PathVariable String id,
            @Parameter(description = "收费状态") @RequestParam String payStatus) {
        ExaminationRequest examRequest = examinationRequestService.updatePayStatus(id, payStatus);
        return Result.success("更新收费状态成功", examRequest);
    }
}