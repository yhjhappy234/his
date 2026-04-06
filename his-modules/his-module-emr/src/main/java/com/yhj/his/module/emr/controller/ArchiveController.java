package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.EmrSubmitDTO;
import com.yhj.his.module.emr.service.*;
import com.yhj.his.module.emr.vo.QcSubmitResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 病历归档Controller
 */
@Tag(name = "病历归档管理", description = "病历审核归档、病案借阅")
@RestController
@RequestMapping("/api/emr/v1/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final OutpatientEmrService outpatientEmrService;
    private final AdmissionRecordService admissionRecordService;
    private final DischargeRecordService dischargeRecordService;
    private final ProgressRecordService progressRecordService;
    private final OperationRecordService operationRecordService;

    @Operation(summary = "提交病历归档")
    @PostMapping("/submit")
    public Result<QcSubmitResultVO> submitForArchive(@Valid @RequestBody EmrSubmitDTO dto) {
        QcSubmitResultVO result;
        switch (dto.getRecordType()) {
            case "门诊病历":
                result = outpatientEmrService.submitOutpatientEmr(dto);
                break;
            case "入院记录":
                result = admissionRecordService.submitAdmissionRecord(dto);
                break;
            case "病程记录":
                result = progressRecordService.submitProgressRecord(dto);
                break;
            case "出院记录":
                result = dischargeRecordService.submitDischargeRecord(dto);
                break;
            case "手术记录":
                result = operationRecordService.submitOperationRecord(dto);
                break;
            default:
                return Result.error("不支持的病历类型");
        }
        return Result.success("提交成功", result);
    }

    @Operation(summary = "病案借阅申请")
    @PostMapping("/borrow/{patientId}")
    public Result<Void> borrowMedicalRecord(
            @PathVariable String patientId,
            @RequestParam String applicantId,
            @RequestParam String applicantName,
            @RequestParam String reason) {
        // TODO: 实现病案借阅申请逻辑
        return Result.successVoid();
    }

    @Operation(summary = "病案归还")
    @PostMapping("/return/{borrowId}")
    public Result<Void> returnMedicalRecord(@PathVariable String borrowId) {
        // TODO: 实现病案归还逻辑
        return Result.successVoid();
    }
}