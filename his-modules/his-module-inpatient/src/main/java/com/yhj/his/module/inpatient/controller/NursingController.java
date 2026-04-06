package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.service.NursingService;
import com.yhj.his.module.inpatient.vo.NursingRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 护理管理控制器
 */
@Tag(name = "护理管理", description = "生命体征录入、护理评估、护理记录等接口")
@RestController
@RequestMapping("/api/inpatient/v1/nursing")
@RequiredArgsConstructor
public class NursingController {

    private final NursingService nursingService;

    @Operation(summary = "录入生命体征", description = "录入患者生命体征（体温、脉搏、呼吸、血压等）")
    @PostMapping("/vital-signs")
    public Result<String> recordVitalSigns(@Valid @RequestBody VitalSignsDTO dto) {
        String recordId = nursingService.recordVitalSigns(dto);
        return Result.success("录入成功", recordId);
    }

    @Operation(summary = "护理评估", description = "进行护理风险评估（跌倒、压疮、疼痛等）")
    @PostMapping("/assessment")
    public Result<String> assessment(@Valid @RequestBody NursingAssessmentDTO dto) {
        String assessmentId = nursingService.assessment(dto);
        return Result.success("评估记录成功", assessmentId);
    }

    @Operation(summary = "护理记录", description = "记录护理过程、出入量等")
    @PostMapping("/record")
    public Result<String> recordNursing(@Valid @RequestBody NursingRecordDTO dto) {
        String recordId = nursingService.recordNursing(dto);
        return Result.success("护理记录成功", recordId);
    }

    @Operation(summary = "查询护理记录", description = "查询患者的所有护理记录")
    @GetMapping("/list/{admissionId}")
    public Result<List<NursingRecordVO>> listByAdmission(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<NursingRecordVO> list = nursingService.listByAdmission(admissionId);
        return Result.success(list);
    }

    @Operation(summary = "查询生命体征记录", description = "查询患者的生命体征记录")
    @GetMapping("/vital-signs/{admissionId}")
    public Result<List<NursingRecordVO>> listVitalSigns(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<NursingRecordVO> list = nursingService.listVitalSigns(admissionId);
        return Result.success(list);
    }

    @Operation(summary = "查询最后一次生命体征", description = "查询患者最后一次记录的生命体征")
    @GetMapping("/vital-signs/last/{admissionId}")
    public Result<NursingRecordVO> getLastVitalSigns(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        NursingRecordVO vo = nursingService.getLastVitalSigns(admissionId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询护理记录", description = "分页查询护理记录列表")
    @GetMapping("/page")
    public Result<PageResult<NursingRecordVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "住院ID") @RequestParam String admissionId) {
        PageResult<NursingRecordVO> result = nursingService.page(pageNum, pageSize, admissionId);
        return Result.success(result);
    }
}