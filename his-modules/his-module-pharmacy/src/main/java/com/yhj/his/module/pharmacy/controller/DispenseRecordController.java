package com.yhj.his.module.pharmacy.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DispenseConfirmDTO;
import com.yhj.his.module.pharmacy.dto.DispenseQueryDTO;
import com.yhj.his.module.pharmacy.dto.PrescriptionAuditDTO;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.service.DispenseRecordService;
import com.yhj.his.module.pharmacy.vo.DispenseRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发药记录控制器
 */
@Tag(name = "发药管理", description = "药品发药记录管理接口")
@RestController
@RequestMapping("/api/pharmacy/v1/dispense")
@RequiredArgsConstructor
public class DispenseRecordController {

    private final DispenseRecordService dispenseRecordService;

    @Operation(summary = "获取发药记录详情", description = "根据ID查询发药记录详情")
    @GetMapping("/{dispenseId}")
    public Result<DispenseRecordVO> getDispenseRecord(
            @Parameter(description = "发药ID") @PathVariable String dispenseId) {
        return dispenseRecordService.getDispenseRecordById(dispenseId);
    }

    @Operation(summary = "根据发药单号查询", description = "根据发药单号查询发药记录")
    @GetMapping("/no/{dispenseNo}")
    public Result<DispenseRecordVO> getDispenseRecordByNo(
            @Parameter(description = "发药单号") @PathVariable String dispenseNo) {
        return dispenseRecordService.getDispenseRecordByNo(dispenseNo);
    }

    @Operation(summary = "分页查询发药记录", description = "支持多条件分页查询发药记录")
    @PostMapping("/query")
    public Result<PageResult<DispenseRecordVO>> queryDispenseRecords(@RequestBody DispenseQueryDTO dto) {
        return dispenseRecordService.queryDispenseRecords(dto);
    }

    @Operation(summary = "查询处方待发药记录", description = "查询指定处方的待发药记录")
    @GetMapping("/prescription/{prescriptionId}")
    public Result<DispenseRecordVO> getPendingDispenseByPrescription(
            @Parameter(description = "处方ID") @PathVariable String prescriptionId) {
        return dispenseRecordService.getPendingDispenseByPrescription(prescriptionId);
    }

    @Operation(summary = "审核处方", description = "药师审核处方(通过/驳回)")
    @PostMapping("/{dispenseId}/audit")
    public Result<DispenseRecordVO> auditPrescription(
            @Parameter(description = "发药ID") @PathVariable String dispenseId,
            @Valid @RequestBody PrescriptionAuditDTO dto) {
        return dispenseRecordService.auditPrescription(dispenseId, dto);
    }

    @Operation(summary = "确认发药", description = "药师确认发药")
    @PostMapping("/confirm")
    public Result<DispenseRecordVO> confirmDispense(@Valid @RequestBody DispenseConfirmDTO dto) {
        return dispenseRecordService.confirmDispense(dto);
    }

    @Operation(summary = "取消发药", description = "取消发药记录")
    @PostMapping("/{dispenseId}/cancel")
    public Result<Void> cancelDispense(
            @Parameter(description = "发药ID") @PathVariable String dispenseId,
            @Parameter(description = "取消原因") @RequestParam String reason) {
        return dispenseRecordService.cancelDispense(dispenseId, reason);
    }

    @Operation(summary = "退药处理", description = "处理患者退药申请")
    @PostMapping("/{dispenseId}/return")
    public Result<DispenseRecordVO> processDrugReturn(
            @Parameter(description = "发药ID") @PathVariable String dispenseId,
            @Parameter(description = "退药原因") @RequestParam String reason,
            @Parameter(description = "操作员ID") @RequestParam String operatorId) {
        return dispenseRecordService.processDrugReturn(dispenseId, reason, operatorId);
    }

    @Operation(summary = "查询待审核发药记录", description = "查询药房待审核的发药记录")
    @GetMapping("/pending-audit/{pharmacyId}")
    public Result<List<DispenseRecordVO>> getPendingAuditRecords(
            @Parameter(description = "药房ID") @PathVariable String pharmacyId) {
        return dispenseRecordService.getPendingAuditRecords(pharmacyId);
    }

    @Operation(summary = "查询待发药记录", description = "查询药房待发药的记录")
    @GetMapping("/pending-dispense/{pharmacyId}")
    public Result<List<DispenseRecordVO>> getPendingDispenseRecords(
            @Parameter(description = "药房ID") @PathVariable String pharmacyId) {
        return dispenseRecordService.getPendingDispenseRecords(pharmacyId);
    }

    @Operation(summary = "查询患者发药记录", description = "查询患者的所有发药记录")
    @GetMapping("/patient/{patientId}")
    public Result<List<DispenseRecordVO>> getPatientDispenseRecords(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        return dispenseRecordService.getPatientDispenseRecords(patientId);
    }

    @Operation(summary = "更新审核状态", description = "手动更新审核状态")
    @PutMapping("/{dispenseId}/audit-status")
    public Result<Void> updateAuditStatus(
            @Parameter(description = "发药ID") @PathVariable String dispenseId,
            @Parameter(description = "审核状态") @RequestParam AuditStatus status) {
        return dispenseRecordService.updateAuditStatus(dispenseId, status);
    }

    @Operation(summary = "更新发药状态", description = "手动更新发药状态")
    @PutMapping("/{dispenseId}/dispense-status")
    public Result<Void> updateDispenseStatus(
            @Parameter(description = "发药ID") @PathVariable String dispenseId,
            @Parameter(description = "发药状态") @RequestParam DispenseStatus status) {
        return dispenseRecordService.updateDispenseStatus(dispenseId, status);
    }

    @Operation(summary = "患者确认接收", description = "患者确认接收药品")
    @PostMapping("/{dispenseId}/confirm-receive")
    public Result<Void> confirmReceive(
            @Parameter(description = "发药ID") @PathVariable String dispenseId) {
        return dispenseRecordService.confirmReceive(dispenseId);
    }
}