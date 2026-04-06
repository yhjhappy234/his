package com.yhj.his.module.inpatient.controller;

import com.yhj.his.module.inpatient.service.NursingService;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.NursingAssessmentDTO;
import com.yhj.his.module.inpatient.dto.NursingRecordDTO;
import com.yhj.his.module.inpatient.dto.VitalSignsDTO;
import com.yhj.his.module.inpatient.vo.NursingRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 护理记录管理控制器
 */
@RestController
@RequestMapping("/api/inpatient/v1/nursing-records")
@Tag(name = "护理记录管理", description = "护理记录、生命体征录入、护理评估等接口")
public class NursingRecordController {

    @Autowired
    private NursingService nursingService;

    /**
     * 录入生命体征
     */
    @PostMapping("/vital-signs")
    @Operation(summary = "录入生命体征", description = "录入患者生命体征数据（体温、脉搏、呼吸、血压等）")
    public Result<String> recordVitalSigns(@Valid @RequestBody VitalSignsDTO dto) {
        String recordId = nursingService.recordVitalSigns(dto);
        return Result.success("生命体征录入成功", recordId);
    }

    /**
     * 护理评估
     */
    @PostMapping("/assessment")
    @Operation(summary = "护理评估", description = "对患者进行护理评估")
    public Result<String> assessment(@Valid @RequestBody NursingAssessmentDTO dto) {
        String assessmentId = nursingService.assessment(dto);
        return Result.success("护理评估完成", assessmentId);
    }

    /**
     * 护理记录
     */
    @PostMapping
    @Operation(summary = "护理记录", description = "录入护理记录")
    public Result<String> recordNursing(@Valid @RequestBody NursingRecordDTO dto) {
        String recordId = nursingService.recordNursing(dto);
        return Result.success("护理记录录入成功", recordId);
    }

    /**
     * 查询护理记录列表
     */
    @GetMapping("/admission/{admissionId}")
    @Operation(summary = "查询护理记录列表", description = "查询指定住院患者的护理记录列表")
    public Result<List<NursingRecordVO>> listByAdmission(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<NursingRecordVO> list = nursingService.listByAdmission(admissionId);
        return Result.success(list);
    }

    /**
     * 查询生命体征记录
     */
    @GetMapping("/vital-signs/{admissionId}")
    @Operation(summary = "查询生命体征记录", description = "查询指定住院患者的生命体征记录")
    public Result<List<NursingRecordVO>> listVitalSigns(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<NursingRecordVO> list = nursingService.listVitalSigns(admissionId);
        return Result.success(list);
    }

    /**
     * 查询最后一次生命体征
     */
    @GetMapping("/vital-signs/{admissionId}/latest")
    @Operation(summary = "查询最后一次生命体征", description = "查询指定住院患者最后一次录入的生命体征")
    public Result<NursingRecordVO> getLastVitalSigns(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        NursingRecordVO vo = nursingService.getLastVitalSigns(admissionId);
        return Result.success(vo);
    }

    /**
     * 分页查询护理记录
     */
    @GetMapping
    @Operation(summary = "分页查询护理记录", description = "分页查询护理记录列表")
    public Result<PageResult<NursingRecordVO>> page(
            @Parameter(description = "住院ID") @RequestParam String admissionId,
            @PageableDefault Pageable pageable) {
        PageResult<NursingRecordVO> result = nursingService.page(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                admissionId);
        return Result.success(result);
    }

    /**
     * 批量录入生命体征
     */
    @PostMapping("/vital-signs/batch")
    @Operation(summary = "批量录入生命体征", description = "批量录入多个患者的生命体征数据")
    public Result<List<String>> batchRecordVitalSigns(@Valid @RequestBody List<VitalSignsDTO> dtoList) {
        List<String> recordIds = dtoList.stream()
                .map(nursingService::recordVitalSigns)
                .toList();
        return Result.success("批量录入成功", recordIds);
    }

    /**
     * 查询今日护理记录
     */
    @GetMapping("/today/{admissionId}")
    @Operation(summary = "查询今日护理记录", description = "查询指定住院患者今日的护理记录")
    public Result<List<NursingRecordVO>> listTodayRecords(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<NursingRecordVO> allRecords = nursingService.listByAdmission(admissionId);
        // Filter today's records
        java.time.LocalDate today = java.time.LocalDate.now();
        List<NursingRecordVO> todayRecords = allRecords.stream()
                .filter(r -> r.getRecordTime() != null && r.getRecordTime().toLocalDate().equals(today))
                .toList();
        return Result.success(todayRecords);
    }
}