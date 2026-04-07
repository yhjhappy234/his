package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.OutpatientEmrSaveDTO;
import com.yhj.his.module.emr.entity.OutpatientEmr;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.service.OutpatientEmrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 门诊病历REST控制器
 */
@Tag(name = "门诊病历管理", description = "门诊病历的CRUD及业务操作")
@RestController
@RequestMapping("/api/emr/v1/outpatient-emrs")
@RequiredArgsConstructor
public class OutpatientEmrController {

    private final OutpatientEmrService emrService;

    @Operation(summary = "创建门诊病历", description = "创建新的门诊病历")
    @PostMapping
    public Result<OutpatientEmr> createEmr(@Valid @RequestBody OutpatientEmrSaveDTO dto) {
        OutpatientEmr emr = emrService.createEmr(dto);
        return Result.success("门诊病历创建成功", emr);
    }

    @Operation(summary = "从模板创建门诊病历", description = "使用模板创建门诊病历")
    @PostMapping("/from-template/{templateId}")
    public Result<OutpatientEmr> createFromTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Valid @RequestBody OutpatientEmrSaveDTO dto) {
        OutpatientEmr emr = emrService.createFromTemplate(templateId, dto);
        return Result.success("从模板创建门诊病历成功", emr);
    }

    @Operation(summary = "更新门诊病历", description = "更新指定的门诊病历")
    @PutMapping("/{id}")
    public Result<OutpatientEmr> updateEmr(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Valid @RequestBody OutpatientEmrSaveDTO dto) {
        OutpatientEmr emr = emrService.updateEmr(id, dto);
        return Result.success("门诊病历更新成功", emr);
    }

    @Operation(summary = "删除门诊病历", description = "删除指定的门诊病历")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmr(@Parameter(description = "病历ID") @PathVariable String id) {
        emrService.deleteEmr(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取门诊病历详情", description = "根据ID获取门诊病历详情")
    @GetMapping("/{id}")
    public Result<OutpatientEmr> getEmr(@Parameter(description = "病历ID") @PathVariable String id) {
        OutpatientEmr emr = emrService.getEmrById(id);
        return Result.success(emr);
    }

    @Operation(summary = "根据就诊ID获取门诊病历", description = "根据就诊ID获取门诊病历")
    @GetMapping("/visit/{visitId}")
    public Result<OutpatientEmr> getEmrByVisitId(
            @Parameter(description = "就诊ID") @PathVariable String visitId) {
        Optional<OutpatientEmr> emr = emrService.getEmrByVisitId(visitId);
        return emr.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该就诊ID对应的门诊病历"));
    }

    @Operation(summary = "分页查询门诊病历", description = "分页查询所有门诊病历")
    @GetMapping
    public Result<Page<OutpatientEmr>> listEmrs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OutpatientEmr> emrs = emrService.listEmrs(pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "根据患者ID查询病历列表", description = "获取患者的所有门诊病历")
    @GetMapping("/patient/{patientId}")
    public Result<List<OutpatientEmr>> getEmrsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<OutpatientEmr> emrs = emrService.getEmrsByPatientId(patientId);
        return Result.success(emrs);
    }

    @Operation(summary = "根据医生ID查询病历", description = "分页查询医生的门诊病历")
    @GetMapping("/doctor/{doctorId}")
    public Result<Page<OutpatientEmr>> getEmrsByDoctorId(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutpatientEmr> emrs = emrService.getEmrsByDoctorId(doctorId, pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "根据科室和日期查询", description = "分页查询指定科室某日期的门诊病历")
    @GetMapping("/dept/{deptId}/date/{visitDate}")
    public Result<Page<OutpatientEmr>> getEmrsByDeptIdAndDate(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "就诊日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutpatientEmr> emrs = emrService.getEmrsByDeptIdAndDate(deptId, visitDate, pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "根据状态查询", description = "分页查询指定状态的门诊病历")
    @GetMapping("/status/{status}")
    public Result<Page<OutpatientEmr>> getEmrsByStatus(
            @Parameter(description = "病历状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutpatientEmr> emrs = emrService.getEmrsByStatus(status, pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "根据科室和状态查询", description = "分页查询指定科室和状态的门诊病历")
    @GetMapping("/dept/{deptId}/status/{status}")
    public Result<Page<OutpatientEmr>> getEmrsByDeptIdAndStatus(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "病历状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutpatientEmr> emrs = emrService.getEmrsByDeptIdAndStatus(deptId, status, pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "获取患者最新病历", description = "获取患者最新的门诊病历")
    @GetMapping("/patient/{patientId}/latest")
    public Result<OutpatientEmr> getLatestEmrByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        Optional<OutpatientEmr> emr = emrService.getLatestEmrByPatientId(patientId);
        return emr.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该患者的门诊病历"));
    }

    @Operation(summary = "搜索门诊病历", description = "根据患者姓名模糊搜索门诊病历")
    @GetMapping("/search")
    public Result<Page<OutpatientEmr>> searchByPatientName(
            @Parameter(description = "患者姓名") @RequestParam String patientName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutpatientEmr> emrs = emrService.searchByPatientName(patientName, pageable);
        return Result.success(emrs);
    }

    @Operation(summary = "提交门诊病历", description = "提交门诊病历待审核")
    @PostMapping("/submit")
    public Result<OutpatientEmr> submitEmr(@Valid @RequestBody EmrSubmitDTO dto) {
        OutpatientEmr emr = emrService.submitEmr(dto);
        return Result.success("门诊病历提交成功", emr);
    }

    @Operation(summary = "审核门诊病历", description = "审核门诊病历")
    @PostMapping("/{id}/audit")
    public Result<OutpatientEmr> auditEmr(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName,
            @Parameter(description = "审核意见") @RequestParam(required = false) String comment) {
        OutpatientEmr emr = emrService.auditEmr(id, approved, auditorId, auditorName, comment);
        return Result.success(approved ? "门诊病历审核通过" : "门诊病历审核退回", emr);
    }

    @Operation(summary = "统计科室某日期病历数", description = "统计指定科室某日期的门诊病历数量")
    @GetMapping("/dept/{deptId}/date/{visitDate}/count")
    public Result<Long> countByDeptIdAndVisitDate(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "就诊日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitDate) {
        Long count = emrService.countByDeptIdAndVisitDate(deptId, visitDate);
        return Result.success(count);
    }
}