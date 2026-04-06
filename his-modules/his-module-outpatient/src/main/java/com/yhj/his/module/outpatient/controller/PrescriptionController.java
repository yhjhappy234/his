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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 处方管理Controller
 */
@Tag(name = "处方管理", description = "处方开立、查询、作废等接口")
@RestController
@RequestMapping("/api/outpatient/v1/prescription")
@RequiredArgsConstructor
public class PrescriptionController {

    private final OutpatientPrescriptionService prescriptionService;

    @Operation(summary = "开立处方", description = "医生开立处方")
    @PostMapping("/create")
    public Result<PrescriptionResultVO> createPrescription(@Valid @RequestBody PrescriptionCreateRequest request) {
        PrescriptionResultVO result = prescriptionService.createPrescription(request);
        return Result.success("处方开立成功", result);
    }

    @Operation(summary = "更新处方", description = "更新处方信息")
    @PutMapping("/update/{id}")
    public Result<PrescriptionResultVO> updatePrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Valid @RequestBody PrescriptionCreateRequest request) {
        PrescriptionResultVO result = prescriptionService.updatePrescription(id, request);
        return Result.success("更新成功", result);
    }

    @Operation(summary = "作废处方", description = "作废已开立的处方")
    @PostMapping("/void/{id}")
    public Result<Void> voidPrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Parameter(description = "作废原因") @RequestParam(required = false) String reason) {
        prescriptionService.voidPrescription(id, reason);
        return Result.successVoid();
    }

    @Operation(summary = "审核处方", description = "审核处方")
    @PostMapping("/audit/{id}")
    public Result<OutpatientPrescription> auditPrescription(
            @Parameter(description = "处方ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName,
            @Parameter(description = "审核备注") @RequestParam(required = false) String remark) {
        OutpatientPrescription prescription = prescriptionService.auditPrescription(id, approved, auditorId, auditorName, remark);
        return Result.success("审核完成", prescription);
    }

    @Operation(summary = "根据ID查询处方", description = "根据处方ID查询处方详情")
    @GetMapping("/get/{id}")
    public Result<PrescriptionResultVO> getPrescriptionById(
            @Parameter(description = "处方ID") @PathVariable String id) {
        PrescriptionResultVO prescription = prescriptionService.getPrescriptionDetail(id);
        return Result.success(prescription);
    }

    @Operation(summary = "根据处方号查询处方", description = "根据处方号查询处方详情")
    @GetMapping("/getByNo")
    public Result<PrescriptionResultVO> getPrescriptionByNo(
            @Parameter(description = "处方号") @RequestParam String prescriptionNo) {
        return prescriptionService.findByPrescriptionNo(prescriptionNo)
                .map(p -> Result.success(prescriptionService.getPrescriptionDetail(p.getId())))
                .orElse(Result.error("处方不存在"));
    }

    @Operation(summary = "分页查询处方列表", description = "分页查询处方列表")
    @GetMapping("/list")
    public Result<PageResult<OutpatientPrescription>> listPrescriptions(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "收费状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<OutpatientPrescription> result = prescriptionService.listPrescriptions(patientId, doctorId, payStatus, status, startDate, endDate, PageRequest.of(pageNum - 1, pageSize));
        return Result.success(result);
    }

    @Operation(summary = "查询挂号关联处方列表", description = "查询挂号关联的所有处方")
    @GetMapping("/listByRegistration")
    public Result<List<OutpatientPrescription>> getPrescriptionsByRegistration(
            @Parameter(description = "挂号ID") @RequestParam String registrationId) {
        List<OutpatientPrescription> result = prescriptionService.listPrescriptionsByRegistration(registrationId);
        return Result.success(result);
    }

    @Operation(summary = "查询患者处方列表", description = "查询患者的处方历史")
    @GetMapping("/listByPatient")
    public Result<List<OutpatientPrescription>> getPrescriptionsByPatient(
            @Parameter(description = "患者ID") @RequestParam String patientId) {
        List<OutpatientPrescription> result = prescriptionService.listPatientPrescriptions(patientId);
        return Result.success(result);
    }

    @Operation(summary = "计算处方金额", description = "计算处方总金额")
    @GetMapping("/calculate/{id}")
    public Result<PrescriptionResultVO> calculateAmount(
            @Parameter(description = "处方ID") @PathVariable String id) {
        PrescriptionResultVO result = prescriptionService.calculateAmount(id);
        return Result.success(result);
    }
}