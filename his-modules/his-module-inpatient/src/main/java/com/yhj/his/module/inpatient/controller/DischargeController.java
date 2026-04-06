package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.service.DischargeService;
import com.yhj.his.module.inpatient.vo.DischargeSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 出院管理控制器
 */
@Tag(name = "出院管理", description = "出院申请、出院结算、出院小结等接口")
@RestController
@RequestMapping("/api/inpatient/v1/discharge")
@RequiredArgsConstructor
public class DischargeController {

    private final DischargeService dischargeService;

    @Operation(summary = "出院申请", description = "申请办理出院")
    @PostMapping("/apply")
    public Result<Boolean> apply(@Valid @RequestBody DischargeApplyDTO dto) {
        boolean result = dischargeService.apply(dto);
        return Result.success("出院申请成功", result);
    }

    @Operation(summary = "出院结算", description = "住院费用结算")
    @PostMapping("/settle")
    public Result<DischargeSettleResponseDTO> settle(@Valid @RequestBody DischargeSettleDTO dto) {
        DischargeSettleResponseDTO vo = dischargeService.settle(dto);
        return Result.success("结算成功", vo);
    }

    @Operation(summary = "出院小结", description = "书写出院小结")
    @PostMapping("/summary")
    public Result<String> summary(@Valid @RequestBody DischargeSummaryDTO dto) {
        String summaryId = dischargeService.summary(dto);
        return Result.success("出院小结完成", summaryId);
    }

    @Operation(summary = "查询出院小结", description = "查询患者的出院小结")
    @GetMapping("/summary/{admissionId}")
    public Result<DischargeSummaryVO> getSummary(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        DischargeSummaryVO vo = dischargeService.getSummary(admissionId);
        return Result.success(vo);
    }

    @Operation(summary = "取消出院申请", description = "取消出院申请")
    @PostMapping("/cancel/{admissionId}")
    public Result<Boolean> cancelApply(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        boolean result = dischargeService.cancelApply(admissionId);
        return Result.success("取消出院申请成功", result);
    }
}