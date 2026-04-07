package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.DischargeRecordSaveDTO;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.entity.DischargeRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.service.DischargeRecordService;
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
 * 出院记录REST控制器
 */
@Tag(name = "出院记录管理", description = "出院记录的CRUD及业务操作")
@RestController
@RequestMapping("/api/emr/v1/discharge-records")
@RequiredArgsConstructor
public class DischargeRecordController {

    private final DischargeRecordService recordService;

    @Operation(summary = "创建出院记录", description = "创建新的出院记录")
    @PostMapping
    public Result<DischargeRecord> createRecord(@Valid @RequestBody DischargeRecordSaveDTO dto) {
        DischargeRecord record = recordService.createRecord(dto);
        return Result.success("出院记录创建成功", record);
    }

    @Operation(summary = "从模板创建出院记录", description = "使用模板创建出院记录")
    @PostMapping("/from-template/{templateId}")
    public Result<DischargeRecord> createFromTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Valid @RequestBody DischargeRecordSaveDTO dto) {
        DischargeRecord record = recordService.createFromTemplate(templateId, dto);
        return Result.success("从模板创建出院记录成功", record);
    }

    @Operation(summary = "更新出院记录", description = "更新指定的出院记录")
    @PutMapping("/{id}")
    public Result<DischargeRecord> updateRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Valid @RequestBody DischargeRecordSaveDTO dto) {
        DischargeRecord record = recordService.updateRecord(id, dto);
        return Result.success("出院记录更新成功", record);
    }

    @Operation(summary = "删除出院记录", description = "删除指定的出院记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        recordService.deleteRecord(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取出院记录详情", description = "根据ID获取出院记录详情")
    @GetMapping("/{id}")
    public Result<DischargeRecord> getRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        DischargeRecord record = recordService.getRecordById(id);
        return Result.success(record);
    }

    @Operation(summary = "根据住院ID获取出院记录", description = "根据住院ID获取出院记录")
    @GetMapping("/admission/{admissionId}")
    public Result<DischargeRecord> getRecordByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        Optional<DischargeRecord> record = recordService.getRecordByAdmissionId(admissionId);
        return record.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该住院ID对应的出院记录"));
    }

    @Operation(summary = "分页查询出院记录", description = "分页查询所有出院记录")
    @GetMapping
    public Result<Page<DischargeRecord>> listRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DischargeRecord> records = recordService.listRecords(pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据患者ID查询出院记录列表", description = "获取患者的所有出院记录")
    @GetMapping("/patient/{patientId}")
    public Result<List<DischargeRecord>> getRecordsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<DischargeRecord> records = recordService.getRecordsByPatientId(patientId);
        return Result.success(records);
    }

    @Operation(summary = "根据科室查询出院记录", description = "分页查询指定科室的出院记录")
    @GetMapping("/dept/{deptId}")
    public Result<Page<DischargeRecord>> getRecordsByDeptId(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DischargeRecord> records = recordService.getRecordsByDeptId(deptId, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据状态查询出院记录", description = "分页查询指定状态的出院记录")
    @GetMapping("/status/{status}")
    public Result<Page<DischargeRecord>> getRecordsByStatus(
            @Parameter(description = "记录状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DischargeRecord> records = recordService.getRecordsByStatus(status, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据科室和状态查询", description = "分页查询指定科室和状态的出院记录")
    @GetMapping("/dept/{deptId}/status/{status}")
    public Result<Page<DischargeRecord>> getRecordsByDeptIdAndStatus(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "记录状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DischargeRecord> records = recordService.getRecordsByDeptIdAndStatus(deptId, status, pageable);
        return Result.success(records);
    }

    @Operation(summary = "获取患者最新出院记录", description = "获取患者最新的出院记录")
    @GetMapping("/patient/{patientId}/latest")
    public Result<DischargeRecord> getLatestRecordByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        Optional<DischargeRecord> record = recordService.getLatestRecordByPatientId(patientId);
        return record.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到该患者的出院记录"));
    }

    @Operation(summary = "搜索出院记录", description = "根据患者姓名模糊搜索出院记录")
    @GetMapping("/search")
    public Result<Page<DischargeRecord>> searchByPatientName(
            @Parameter(description = "患者姓名") @RequestParam String patientName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DischargeRecord> records = recordService.searchByPatientName(patientName, pageable);
        return Result.success(records);
    }

    @Operation(summary = "提交出院记录", description = "提交出院记录待审核")
    @PostMapping("/submit")
    public Result<DischargeRecord> submitRecord(@Valid @RequestBody EmrSubmitDTO dto) {
        DischargeRecord record = recordService.submitRecord(dto);
        return Result.success("出院记录提交成功", record);
    }

    @Operation(summary = "审核出院记录", description = "审核出院记录")
    @PostMapping("/{id}/audit")
    public Result<DischargeRecord> auditRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName,
            @Parameter(description = "审核意见") @RequestParam(required = false) String comment) {
        DischargeRecord record = recordService.auditRecord(id, approved, auditorId, auditorName, comment);
        return Result.success(approved ? "出院记录审核通过" : "出院记录审核退回", record);
    }
}