package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.OperationRecordSaveDTO;
import com.yhj.his.module.emr.entity.OperationRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.service.OperationRecordService;
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

/**
 * 手术记录REST控制器
 */
@Tag(name = "手术记录管理", description = "手术记录的CRUD及业务操作")
@RestController
@RequestMapping("/api/emr/v1/operation-records")
@RequiredArgsConstructor
public class OperationRecordController {

    private final OperationRecordService recordService;

    @Operation(summary = "创建手术记录", description = "创建新的手术记录")
    @PostMapping
    public Result<OperationRecord> createRecord(@Valid @RequestBody OperationRecordSaveDTO dto) {
        OperationRecord record = recordService.createRecord(dto);
        return Result.success("手术记录创建成功", record);
    }

    @Operation(summary = "从模板创建手术记录", description = "使用模板创建手术记录")
    @PostMapping("/from-template/{templateId}")
    public Result<OperationRecord> createFromTemplate(
            @Parameter(description = "模板ID") @PathVariable String templateId,
            @Valid @RequestBody OperationRecordSaveDTO dto) {
        OperationRecord record = recordService.createFromTemplate(templateId, dto);
        return Result.success("从模板创建手术记录成功", record);
    }

    @Operation(summary = "更新手术记录", description = "更新指定的手术记录")
    @PutMapping("/{id}")
    public Result<OperationRecord> updateRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Valid @RequestBody OperationRecordSaveDTO dto) {
        OperationRecord record = recordService.updateRecord(id, dto);
        return Result.success("手术记录更新成功", record);
    }

    @Operation(summary = "删除手术记录", description = "删除指定的手术记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        recordService.deleteRecord(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取手术记录详情", description = "根据ID获取手术记录详情")
    @GetMapping("/{id}")
    public Result<OperationRecord> getRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        OperationRecord record = recordService.getRecordById(id);
        return Result.success(record);
    }

    @Operation(summary = "根据住院ID查询手术记录列表", description = "获取住院的所有手术记录")
    @GetMapping("/admission/{admissionId}")
    public Result<List<OperationRecord>> getRecordsByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<OperationRecord> records = recordService.getRecordsByAdmissionId(admissionId);
        return Result.success(records);
    }

    @Operation(summary = "根据住院ID和日期范围查询", description = "获取住院指定日期范围的手术记录")
    @GetMapping("/admission/{admissionId}/range")
    public Result<List<OperationRecord>> getRecordsByAdmissionIdAndDateRange(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<OperationRecord> records = recordService.getRecordsByAdmissionIdAndDateRange(admissionId, startDate, endDate);
        return Result.success(records);
    }

    @Operation(summary = "根据患者ID查询手术记录", description = "获取患者的所有手术记录")
    @GetMapping("/patient/{patientId}")
    public Result<List<OperationRecord>> getRecordsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<OperationRecord> records = recordService.getRecordsByPatientId(patientId);
        return Result.success(records);
    }

    @Operation(summary = "分页查询手术记录", description = "分页查询所有手术记录")
    @GetMapping
    public Result<Page<OperationRecord>> listRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OperationRecord> records = recordService.listRecords(pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据科室查询手术记录", description = "分页查询指定科室的手术记录")
    @GetMapping("/dept/{deptId}")
    public Result<Page<OperationRecord>> getRecordsByDeptId(
            @Parameter(description = "科室ID") @PathVariable String deptId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.getRecordsByDeptId(deptId, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据主刀医生查询手术记录", description = "分页查询主刀医生的手术记录")
    @GetMapping("/surgeon/{surgeonId}")
    public Result<Page<OperationRecord>> getRecordsBySurgeonId(
            @Parameter(description = "主刀医生ID") @PathVariable String surgeonId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.getRecordsBySurgeonId(surgeonId, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据手术日期查询", description = "分页查询指定日期的手术记录")
    @GetMapping("/date/{operationDate}")
    public Result<Page<OperationRecord>> getRecordsByOperationDate(
            @Parameter(description = "手术日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate operationDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.getRecordsByOperationDate(operationDate, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据状态查询手术记录", description = "分页查询指定状态的手术记录")
    @GetMapping("/status/{status}")
    public Result<Page<OperationRecord>> getRecordsByStatus(
            @Parameter(description = "记录状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.getRecordsByStatus(status, pageable);
        return Result.success(records);
    }

    @Operation(summary = "搜索手术记录(患者姓名)", description = "根据患者姓名模糊搜索手术记录")
    @GetMapping("/search/patient")
    public Result<Page<OperationRecord>> searchByPatientName(
            @Parameter(description = "患者姓名") @RequestParam String patientName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.searchByPatientName(patientName, pageable);
        return Result.success(records);
    }

    @Operation(summary = "搜索手术记录(手术名称)", description = "根据手术名称模糊搜索手术记录")
    @GetMapping("/search/operation")
    public Result<Page<OperationRecord>> searchByOperationName(
            @Parameter(description = "手术名称") @RequestParam String operationName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationRecord> records = recordService.searchByOperationName(operationName, pageable);
        return Result.success(records);
    }

    @Operation(summary = "统计医生手术数量", description = "统计医生的手术数量")
    @GetMapping("/surgeon/{surgeonId}/count")
    public Result<Long> countBySurgeonId(
            @Parameter(description = "主刀医生ID") @PathVariable String surgeonId) {
        Long count = recordService.countBySurgeonId(surgeonId);
        return Result.success(count);
    }

    @Operation(summary = "提交手术记录", description = "提交手术记录待审核")
    @PostMapping("/submit")
    public Result<OperationRecord> submitRecord(@Valid @RequestBody EmrSubmitDTO dto) {
        OperationRecord record = recordService.submitRecord(dto);
        return Result.success("手术记录提交成功", record);
    }

    @Operation(summary = "审核手术记录", description = "审核手术记录")
    @PostMapping("/{id}/audit")
    public Result<OperationRecord> auditRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String auditorId,
            @Parameter(description = "审核人姓名") @RequestParam String auditorName,
            @Parameter(description = "审核意见") @RequestParam(required = false) String comment) {
        OperationRecord record = recordService.auditRecord(id, approved, auditorId, auditorName, comment);
        return Result.success(approved ? "手术记录审核通过" : "手术记录审核退回", record);
    }
}