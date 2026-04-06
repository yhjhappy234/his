package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.dto.ProgressRecordSaveDTO;
import com.yhj.his.module.emr.entity.ProgressRecord;
import com.yhj.his.module.emr.enums.EmrStatus;
import com.yhj.his.module.emr.enums.ProgressRecordType;
import com.yhj.his.module.emr.service.ProgressRecordService;
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
 * 病程记录REST控制器
 */
@Tag(name = "病程记录管理", description = "病程记录的CRUD及业务操作")
@RestController
@RequestMapping("/api/emr/v1/progress-records")
@RequiredArgsConstructor
public class ProgressRecordController {

    private final ProgressRecordService recordService;

    @Operation(summary = "创建病程记录", description = "创建新的病程记录")
    @PostMapping
    public Result<ProgressRecord> createRecord(@Valid @RequestBody ProgressRecordSaveDTO dto) {
        ProgressRecord record = recordService.createRecord(dto);
        return Result.success("病程记录创建成功", record);
    }

    @Operation(summary = "创建首次病程记录", description = "创建住院的首次病程记录")
    @PostMapping("/first-progress")
    public Result<ProgressRecord> createFirstProgressRecord(@Valid @RequestBody ProgressRecordSaveDTO dto) {
        ProgressRecord record = recordService.createFirstProgressRecord(dto);
        return Result.success("首次病程记录创建成功", record);
    }

    @Operation(summary = "创建上级医师查房记录", description = "创建上级医师查房记录")
    @PostMapping("/chief-round")
    public Result<ProgressRecord> createChiefRoundRecord(@Valid @RequestBody ProgressRecordSaveDTO dto) {
        ProgressRecord record = recordService.createChiefRoundRecord(dto);
        return Result.success("上级医师查房记录创建成功", record);
    }

    @Operation(summary = "更新病程记录", description = "更新指定的病程记录")
    @PutMapping("/{id}")
    public Result<ProgressRecord> updateRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Valid @RequestBody ProgressRecordSaveDTO dto) {
        ProgressRecord record = recordService.updateRecord(id, dto);
        return Result.success("病程记录更新成功", record);
    }

    @Operation(summary = "删除病程记录", description = "删除指定的病程记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        recordService.deleteRecord(id);
        return Result.successVoid();
    }

    @Operation(summary = "获取病程记录详情", description = "根据ID获取病程记录详情")
    @GetMapping("/{id}")
    public Result<ProgressRecord> getRecord(@Parameter(description = "记录ID") @PathVariable String id) {
        ProgressRecord record = recordService.getRecordById(id);
        return Result.success(record);
    }

    @Operation(summary = "根据住院ID查询病程记录列表", description = "获取住院的所有病程记录")
    @GetMapping("/admission/{admissionId}")
    public Result<List<ProgressRecord>> getRecordsByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<ProgressRecord> records = recordService.getRecordsByAdmissionId(admissionId);
        return Result.success(records);
    }

    @Operation(summary = "根据住院ID和类型查询", description = "获取住院指定类型的病程记录")
    @GetMapping("/admission/{admissionId}/type/{recordType}")
    public Result<List<ProgressRecord>> getRecordsByAdmissionIdAndType(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "记录类型") @PathVariable ProgressRecordType recordType) {
        List<ProgressRecord> records = recordService.getRecordsByAdmissionIdAndType(admissionId, recordType);
        return Result.success(records);
    }

    @Operation(summary = "根据住院ID和日期查询", description = "获取住院指定日期的病程记录")
    @GetMapping("/admission/{admissionId}/date/{recordDate}")
    public Result<List<ProgressRecord>> getRecordsByAdmissionIdAndDate(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "记录日期") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        List<ProgressRecord> records = recordService.getRecordsByAdmissionIdAndDate(admissionId, recordDate);
        return Result.success(records);
    }

    @Operation(summary = "根据住院ID和日期范围查询", description = "获取住院指定日期范围的病程记录")
    @GetMapping("/admission/{admissionId}/range")
    public Result<List<ProgressRecord>> getRecordsByAdmissionIdAndDateRange(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProgressRecord> records = recordService.getRecordsByAdmissionIdAndDateRange(admissionId, startDate, endDate);
        return Result.success(records);
    }

    @Operation(summary = "分页查询病程记录", description = "分页查询所有病程记录")
    @GetMapping
    public Result<Page<ProgressRecord>> listRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProgressRecord> records = recordService.listRecords(pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据患者ID查询病程记录", description = "分页查询患者的病程记录")
    @GetMapping("/patient/{patientId}")
    public Result<Page<ProgressRecord>> getRecordsByPatientId(
            @Parameter(description = "患者ID") @PathVariable String patientId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgressRecord> records = recordService.getRecordsByPatientId(patientId, pageable);
        return Result.success(records);
    }

    @Operation(summary = "根据医生ID查询病程记录", description = "分页查询医生书写的病程记录")
    @GetMapping("/doctor/{doctorId}")
    public Result<Page<ProgressRecord>> getRecordsByDoctorId(
            @Parameter(description = "医生ID") @PathVariable String doctorId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgressRecord> records = recordService.getRecordsByDoctorId(doctorId, pageable);
        return Result.success(records);
    }

    @Operation(summary = "获取首次病程记录", description = "获取住院的首次病程记录")
    @GetMapping("/admission/{admissionId}/first")
    public Result<ProgressRecord> getFirstProgressRecord(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        Optional<ProgressRecord> record = recordService.getFirstProgressRecord(admissionId);
        return record.map(Result::success)
                .orElseGet(() -> Result.error(404, "未找到首次病程记录"));
    }

    @Operation(summary = "根据状态查询病程记录", description = "分页查询指定状态的病程记录")
    @GetMapping("/status/{status}")
    public Result<Page<ProgressRecord>> getRecordsByStatus(
            @Parameter(description = "记录状态") @PathVariable EmrStatus status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProgressRecord> records = recordService.getRecordsByStatus(status, pageable);
        return Result.success(records);
    }

    @Operation(summary = "统计病程记录数", description = "统计住院期间的病程记录数")
    @GetMapping("/admission/{admissionId}/count")
    public Result<Long> countByAdmissionId(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        Long count = recordService.countByAdmissionId(admissionId);
        return Result.success(count);
    }

    @Operation(summary = "提交病程记录", description = "提交病程记录待审核")
    @PostMapping("/submit")
    public Result<ProgressRecord> submitRecord(@Valid @RequestBody EmrSubmitDTO dto) {
        ProgressRecord record = recordService.submitRecord(dto);
        return Result.success("病程记录提交成功", record);
    }

    @Operation(summary = "审核病程记录", description = "审核病程记录")
    @PostMapping("/{id}/audit")
    public Result<ProgressRecord> auditRecord(
            @Parameter(description = "记录ID") @PathVariable String id,
            @Parameter(description = "是否通过") @RequestParam boolean approved,
            @Parameter(description = "审核人ID") @RequestParam String reviewerId,
            @Parameter(description = "审核人姓名") @RequestParam String reviewerName) {
        ProgressRecord record = recordService.auditRecord(id, approved, reviewerId, reviewerName);
        return Result.success(approved ? "病程记录审核通过" : "病程记录审核退回", record);
    }
}