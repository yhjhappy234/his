package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.service.AdmissionService;
import com.yhj.his.module.inpatient.vo.AdmissionRegisterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 入院管理控制器
 */
@Tag(name = "入院管理", description = "入院登记、入院评估、预交金缴纳等接口")
@RestController
@RequestMapping("/api/inpatient/v1/admission")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    @Operation(summary = "入院登记", description = "患者入院登记，分配床位")
    @PostMapping("/register")
    public Result<AdmissionRegisterVO> register(@Valid @RequestBody AdmissionRegisterDTO dto) {
        AdmissionRegisterVO vo = admissionService.register(dto);
        return Result.success("入院登记成功", vo);
    }

    @Operation(summary = "入院评估", description = "入院护理评估，包括跌倒、压疮、疼痛等风险评估")
    @PostMapping("/assessment")
    public Result<Boolean> assessment(@Valid @RequestBody AdmissionAssessmentDTO dto) {
        boolean result = admissionService.assessment(dto);
        return Result.success("入院评估成功", result);
    }

    @Operation(summary = "预交金缴纳", description = "缴纳住院预交金")
    @PostMapping("/deposit")
    public Result<DepositPaymentResponseDTO> payDeposit(@Valid @RequestBody DepositPaymentDTO dto) {
        DepositPaymentResponseDTO vo = admissionService.payDeposit(dto);
        return Result.success("预交金缴纳成功", vo);
    }

    @Operation(summary = "查询住院记录", description = "根据住院ID查询住院记录详情")
    @GetMapping("/{admissionId}")
    public Result<AdmissionRegisterVO> getById(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        AdmissionRegisterVO vo = admissionService.getById(admissionId);
        return Result.success(vo);
    }

    @Operation(summary = "根据住院号查询", description = "根据住院号查询住院记录")
    @GetMapping("/no/{admissionNo}")
    public Result<AdmissionRegisterVO> getByAdmissionNo(
            @Parameter(description = "住院号") @PathVariable String admissionNo) {
        AdmissionRegisterVO vo = admissionService.getByAdmissionNo(admissionNo);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询住院记录", description = "分页查询住院记录列表")
    @GetMapping("/page")
    public Result<PageResult<AdmissionRegisterVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status) {
        PageResult<AdmissionRegisterVO> result = admissionService.page(pageNum, pageSize, status);
        return Result.success(result);
    }
}