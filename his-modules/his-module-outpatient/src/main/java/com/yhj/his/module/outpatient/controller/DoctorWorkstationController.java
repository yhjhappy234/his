package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.RecordSaveRequest;
import com.yhj.his.module.outpatient.service.OutpatientRecordService;
import com.yhj.his.module.outpatient.service.RegistrationService;
import com.yhj.his.module.outpatient.vo.OutpatientRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 门诊医生工作站Controller
 */
@Tag(name = "门诊医生工作站", description = "病历书写、结束就诊等接口")
@RestController
@RequestMapping("/api/outpatient/v1/doctor")
@RequiredArgsConstructor
public class DoctorWorkstationController {

    private final OutpatientRecordService outpatientRecordService;
    private final RegistrationService registrationService;

    @Operation(summary = "保存病历草稿", description = "保存病历草稿")
    @PostMapping("/record/draft")
    public Result<OutpatientRecordVO> saveDraft(@Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.saveDraft(request);
        return Result.success("保存成功", record);
    }

    @Operation(summary = "提交病历", description = "提交病历")
    @PostMapping("/record/submit")
    public Result<OutpatientRecordVO> submitRecord(@Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.submitRecord(request);
        return Result.success("提交成功", record);
    }

    @Operation(summary = "获取病历", description = "根据挂号ID获取病历")
    @GetMapping("/record/get")
    public Result<OutpatientRecordVO> getRecord(
            @Parameter(description = "挂号ID") @RequestParam String registrationId) {
        OutpatientRecordVO record = outpatientRecordService.getRecordByRegistrationId(registrationId);
        return Result.success(record);
    }

    @Operation(summary = "更新病历", description = "更新病历")
    @PutMapping("/record/update/{id}")
    public Result<OutpatientRecordVO> updateRecord(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Valid @RequestBody RecordSaveRequest request) {
        OutpatientRecordVO record = outpatientRecordService.updateRecord(id, request);
        return Result.success("更新成功", record);
    }

    @Operation(summary = "获取患者历史病历", description = "获取患者历史就诊病历")
    @GetMapping("/record/history")
    public Result<List<OutpatientRecordVO>> getPatientHistoryRecords(
            @Parameter(description = "患者ID") @RequestParam String patientId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<OutpatientRecordVO> records = outpatientRecordService.listRecentRecords(patientId, limit);
        return Result.success(records);
    }

    @Operation(summary = "作废病历", description = "作废病历")
    @PostMapping("/record/void/{id}")
    public Result<Void> voidRecord(
            @Parameter(description = "病历ID") @PathVariable String id,
            @Parameter(description = "原因") @RequestParam(required = false) String reason) {
        outpatientRecordService.voidRecord(id, reason);
        return Result.successVoid();
    }

    @Operation(summary = "开始就诊", description = "开始就诊")
    @PostMapping("/visit/start/{registrationId}")
    public Result<?> startVisit(
            @Parameter(description = "挂号ID") @PathVariable String registrationId,
            @Parameter(description = "医生ID") @RequestParam String doctorId) {
        return Result.success("开始就诊", registrationService.startVisit(registrationId, doctorId));
    }

    @Operation(summary = "结束就诊", description = "结束当前患者就诊")
    @PostMapping("/visit/end/{registrationId}")
    public Result<?> endVisit(
            @Parameter(description = "挂号ID") @PathVariable String registrationId) {
        return Result.success("就诊结束", registrationService.endVisit(registrationId));
    }
}