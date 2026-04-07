package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.InformedConsentSaveDTO;
import com.yhj.his.module.emr.dto.SignatureDTO;
import com.yhj.his.module.emr.entity.InformedConsent;
import com.yhj.his.module.emr.enums.ConsentType;
import com.yhj.his.module.emr.service.InformedConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 知情同意书REST控制器
 */
@Tag(name = "知情同意书管理", description = "知情同意书的CRUD及签署操作")
@RestController
@RequestMapping("/api/emr/v1/informed-consents")
@RequiredArgsConstructor
public class InformedConsentController {

    private final InformedConsentService consentService;

    @Operation(summary = "创建知情同意书", description = "创建新的知情同意书")
    @PostMapping
    public Result<InformedConsent> createConsent(@Valid @RequestBody InformedConsentSaveDTO dto) {
        InformedConsent consent = consentService.createConsent(dto);
        return Result.success("知情同意书创建成功", consent);
    }

    @Operation(summary = "从模板创建知情同意书", description = "使用模板创建知情同意书")
    @PostMapping("/from-template/{templateId}")
    public Result<InformedConsent> createFromTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Valid @RequestBody InformedConsentSaveDTO dto) {
        InformedConsent consent = consentService.createFromTemplate(templateId, dto);
        return Result.success("从模板创建知情同意书成功", consent);
    }

    @Operation(summary = "为手术创建知情同意书", description = "为指定手术创建知情同意书")
    @PostMapping("/for-operation/{operationId}")
    public Result<InformedConsent> createForOperation(
            @Parameter(description = "手术ID") @PathVariable String operationId,
            @Parameter(description = "同意书类型") @RequestParam ConsentType consentType,
            @Valid @RequestBody InformedConsentSaveDTO dto) {
        InformedConsent consent = consentService.createForOperation(operationId, consentType, dto);
        return Result.success("为手术创建知情同意书成功", consent);
    }

    @Operation(summary = "更新知情同意书", description = "更新指定的知情同意书")
    @PutMapping("/{id}")
    public Result<InformedConsent> updateConsent(
            @Parameter(description = "同意书ID") @PathVariable String id,
            @Valid @RequestBody InformedConsentSaveDTO dto) {
        InformedConsent consent = consentService.updateConsent(id, dto);
        return Result.success("知情同意书更新成功", consent);
    }

    @Operation(summary = "删除知情同意书", description = "删除指定的知情同意书")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConsent(@Parameter(description = "同意书ID") @PathVariable String id) {
        consentService.deleteConsent(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取知情同意书详情", description = "根据ID获取知情同意书详情")
    @GetMapping("/{id}")
    public Result<InformedConsent> getConsent(@Parameter(description = "同意书ID") @PathVariable String id) {
        InformedConsent consent = consentService.getConsentById(id);
        return Result.success(consent);
    }

    @Operation(summary = "根据住院ID查询知情同意书", description = "获取住院的所有知情同意书")
    @GetMapping("/admission/{admissionId}")
    public Result<List<InformedConsent>> getConsentsByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<InformedConsent> consents = consentService.getConsentsByAdmissionId(admissionId);
        return Result.success(consents);
    }

    @Operation(summary = "根据就诊ID查询知情同意书", description = "获取就诊的所有知情同意书")
    @GetMapping("/visit/{visitId}")
    public Result<List<InformedConsent>> getConsentsByVisitId(
            @Parameter(description = "就诊ID") @PathVariable String visitId) {
        List<InformedConsent> consents = consentService.getConsentsByVisitId(visitId);
        return Result.success(consents);
    }

    @Operation(summary = "根据患者ID查询知情同意书", description = "获取患者的所有知情同意书")
    @GetMapping("/patient/{patientId}")
    public Result<List<InformedConsent>> getConsentsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<InformedConsent> consents = consentService.getConsentsByPatientId(patientId);
        return Result.success(consents);
    }

    @Operation(summary = "分页查询知情同意书", description = "分页查询所有知情同意书")
    @GetMapping
    public Result<Page<InformedConsent>> listConsents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InformedConsent> consents = consentService.listConsents(pageable);
        return Result.success(consents);
    }

    @Operation(summary = "根据同意书类型查询", description = "分页查询指定类型的知情同意书")
    @GetMapping("/type/{consentType}")
    public Result<Page<InformedConsent>> getConsentsByType(
            @Parameter(description = "同意书类型") @PathVariable ConsentType consentType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InformedConsent> consents = consentService.getConsentsByType(consentType, pageable);
        return Result.success(consents);
    }

    @Operation(summary = "根据状态查询知情同意书", description = "分页查询指定状态的知情同意书")
    @GetMapping("/status/{status}")
    public Result<Page<InformedConsent>> getConsentsByStatus(
            @Parameter(description = "同意书状态") @PathVariable String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InformedConsent> consents = consentService.getConsentsByStatus(status, pageable);
        return Result.success(consents);
    }

    @Operation(summary = "根据住院ID和类型查询", description = "获取住院指定类型的知情同意书")
    @GetMapping("/admission/{admissionId}/type/{consentType}")
    public Result<InformedConsent> getConsentByAdmissionIdAndType(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "同意书类型") @PathVariable ConsentType consentType) {
        Optional<InformedConsent> consent = consentService.getConsentByAdmissionIdAndType(admissionId, consentType);
        return consent.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该类型的知情同意书"));
    }

    @Operation(summary = "查询待签署同意书", description = "获取住院的所有待签署知情同意书")
    @GetMapping("/admission/{admissionId}/pending")
    public Result<List<InformedConsent>> getPendingConsentsByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<InformedConsent> consents = consentService.getPendingConsentsByAdmissionId(admissionId);
        return Result.success(consents);
    }

    @Operation(summary = "搜索知情同意书", description = "根据患者姓名模糊搜索知情同意书")
    @GetMapping("/search")
    public Result<Page<InformedConsent>> searchByPatientName(
            @Parameter(description = "患者姓名") @RequestParam String patientName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InformedConsent> consents = consentService.searchByPatientName(patientName, pageable);
        return Result.success(consents);
    }

    @Operation(summary = "根据医生ID查询知情同意书", description = "分页查询医生告知的知情同意书")
    @GetMapping("/doctor/{doctorId}")
    public Result<Page<InformedConsent>> getConsentsByDoctorId(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InformedConsent> consents = consentService.getConsentsByDoctorId(doctorId, pageable);
        return Result.success(consents);
    }

    @Operation(summary = "医生签名", description = "医生签署知情同意书")
    @PostMapping("/doctor-sign")
    public Result<InformedConsent> doctorSign(@Valid @RequestBody SignatureDTO dto) {
        InformedConsent consent = consentService.doctorSign(dto);
        return Result.success("医生签名成功", consent);
    }

    @Operation(summary = "患者签名", description = "患者签署知情同意书")
    @PostMapping("/patient-sign")
    public Result<InformedConsent> patientSign(@Valid @RequestBody SignatureDTO dto) {
        InformedConsent consent = consentService.patientSign(dto);
        return Result.success("患者签名成功", consent);
    }

    @Operation(summary = "代理人签名", description = "代理人签署知情同意书")
    @PostMapping("/agent-sign")
    public Result<InformedConsent> agentSign(@Valid @RequestBody SignatureDTO dto) {
        InformedConsent consent = consentService.agentSign(dto);
        return Result.success("代理人签名成功", consent);
    }

    @Operation(summary = "拒绝签署", description = "拒绝签署知情同意书")
    @PostMapping("/{id}/refuse")
    public Result<InformedConsent> refuseSign(
            @Parameter(description = "同意书ID") @PathVariable String id,
            @Parameter(description = "拒绝原因") @RequestParam String reason) {
        InformedConsent consent = consentService.refuseSign(id, reason);
        return Result.success("已记录拒绝签署", consent);
    }
}