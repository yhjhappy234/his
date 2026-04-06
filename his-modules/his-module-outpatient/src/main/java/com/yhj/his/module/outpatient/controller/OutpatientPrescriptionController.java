package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.PrescriptionCreateRequest;
import com.yhj.his.module.outpatient.entity.OutpatientPrescription;
import com.yhj.his.module.outpatient.service.OutpatientPrescriptionService;
import com.yhj.his.module.outpatient.vo.PrescriptionResultVO;

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
 * 门诊处方控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/prescriptions")
@RequiredArgsConstructor
@Tag(name = "门诊处方管理", description = "门诊处方的增删改查和审核接口")
public class OutpatientPrescriptionController {

    private final OutpatientPrescriptionService prescriptionService;

    @PostMapping
    @Operation(summary = "开立处方", description = "开立门诊处方")
    public Result<PrescriptionResultVO> createPrescription(@Valid @RequestBody PrescriptionCreateRequest request) {
        PrescriptionResultVO result = prescriptionService.createPrescription(request);
        return Result.success("开立处方成功", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取处方详情", description = "根据ID获取处方详细信息")
    public Result<PrescriptionResultVO> getPrescription(@Parameter(description = "处方ID") @PathVariable String id) {
        PrescriptionResultVO prescription = prescriptionService.getPrescriptionDetail(id);
        return Result.success(prescription);
    }

    @GetMapping
    @Operation(summary = "查询处方列表", description = "分页查询处方列表")
    public Result<PageResult<OutpatientPrescription>> listPrescriptions(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("prescriptionDate").descending());
        PageResult<OutpatientPrescription> result = prescriptionService.listPrescriptions(patientId, doctorId, payStatus, status, startDate, endDate, pageable);
        return Result.success(result);
    }

    @GetMapping("/registration/{registrationId}")
    @Operation(summary = "查询挂号关联处方", description = "查询挂号关联的处方列表")
    public Result<List<OutpatientPrescription>> listPrescriptionsByRegistration(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        List<OutpatientPrescription> prescriptions = prescriptionService.listPrescriptionsByRegistration(registrationId);
        return Result.success(prescriptions);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "查询患者处方", description = "查询患者的所有处方记录")
    public Result<List<OutpatientPrescription>> listPatientPrescriptions(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<OutpatientPrescription> prescriptions = prescriptionService.listPatientPrescriptions(patientId);
        return Result.success(prescriptions);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新处方", description = "更新处方信息")
    public Result<PrescriptionResultVO> updatePrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Valid @RequestBody PrescriptionCreateRequest request) {
        PrescriptionResultVO prescription = prescriptionService.updatePrescription(id, request);
        return Result.success("更新处方成功", prescription);
    }

    @PostMapping("/{id}/void")
    @Operation(summary = "作废处方", description = "作废处方")
    public Result<Void> voidPrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Parameter(description = "作废原因") @RequestParam(required = false) String reason) {
        prescriptionService.voidPrescription(id, reason);
        return Result.successVoid();
    }

    @PostMapping("/{id}/audit")
    @Operation(summary = "审核处方", description = "审核处方")
    public Result<OutpatientPrescription> auditPrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName,
            @Parameter(description = "审核备注") @RequestParam(required = false) String remark) {
        OutpatientPrescription prescription = prescriptionService.auditPrescription(id, approved, auditorId, auditorName, remark);
        return Result.success("审核处方成功", prescription);
    }

    @GetMapping("/{id}/calculate")
    @Operation(summary = "计算处方金额", description = "计算处方总金额")
    public Result<PrescriptionResultVO> calculateAmount(@Parameter(description = "处方ID") @PathVariable String id) {
        PrescriptionResultVO result = prescriptionService.calculateAmount(id);
        return Result.success(result);
    }
}