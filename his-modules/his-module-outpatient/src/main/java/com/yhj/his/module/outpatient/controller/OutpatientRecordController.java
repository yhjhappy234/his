package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.RecordSaveRequest;
import com.yhj.his.module.outpatient.service.OutpatientRecordService;
import com.yhj.his.module.outpatient.vo.OutpatientRecordVO;

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
 * 门诊病历控制器
 */
@RestController
@RequestMapping("/api/outpatient/v1/records")
@RequiredArgsConstructor
@Tag(name = "门诊病历管理", description = "门诊病历的增删改查接口")
public class OutpatientRecordController {

    private final OutpatientRecordService outpatientRecordService;

    @PostMapping("/draft")
    @Operation(summary = "保存病历草稿", description = "保存病历草稿")
    public Result<OutpatientRecordVO> saveDraft(@Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.saveDraft(request);
        return Result.success("保存病历草稿成功", record);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交病历", description = "提交病历")
    public Result<OutpatientRecordVO> submitRecord(@Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.submitRecord(request);
        return Result.success("提交病历成功", record);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取病历详情", description = "根据ID获取病历详细信息")
    public Result<OutpatientRecordVO> getRecord(@Parameter(description = "病历ID") @PathVariable String id) {
        OutpatientRecordVO record = outpatientRecordService.getRecordDetail(id);
        return Result.success(record);
    }

    @GetMapping("/registration/{registrationId}")
    @Operation(summary = "根据挂号ID获取病历", description = "根据挂号ID获取病历详情")
    public Result<OutpatientRecordVO> getRecordByRegistration(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        OutpatientRecordVO record = outpatientRecordService.getRecordByRegistrationId(registrationId);
        return Result.success(record);
    }

    @GetMapping
    @Operation(summary = "查询病历列表", description = "分页查询病历列表")
    public Result<PageResult<OutpatientRecordVO>> listRecords(
            @Parameter(description = "患者ID") @RequestParam(required = false) String patientId,
            @Parameter(description = "医生ID") @RequestParam(required = false) String doctorId,
            @Parameter(description = "科室ID") @RequestParam(required = false) String deptId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("visitDate").descending());
        PageResult<OutpatientRecordVO> result = outpatientRecordService.listRecords(patientId, doctorId, deptId, startDate, endDate, pageable);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "查询患者病历", description = "查询患者的所有病历记录")
    public Result<List<OutpatientRecordVO>> listPatientRecords(
            @Parameter(description = "患者ID") @PathVariable String patientId) {
        List<OutpatientRecordVO> records = outpatientRecordService.listPatientRecords(patientId);
        return Result.success(records);
    }

    @GetMapping("/patient/{patientId}/recent")
    @Operation(summary = "查询患者历史病历", description = "查询患者最近N次病历记录")
    public Result<List<OutpatientRecordVO>> listRecentRecords(
            @Parameter(description = "患者ID") @PathVariable String patientId,
            @Parameter(description = "数量") @RequestParam(defaultValue = "5") int limit) {
        List<OutpatientRecordVO> records = outpatientRecordService.listRecentRecords(patientId, limit);
        return Result.success(records);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新病历", description = "更新病历信息")
    public Result<OutpatientRecordVO> updateRecord(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.updateRecord(id, request);
        return Result.success("更新病历成功", record);
    }

    @PostMapping("/{id}/void")
    @Operation(summary = "作废病历", description = "作废病历")
    public Result<Void> voidRecord(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Parameter(description = "作废原因") @RequestParam(required = false) String reason) {
        outpatientRecordService.voidRecord(id, reason);
        return Result.successVoid();
    }
}