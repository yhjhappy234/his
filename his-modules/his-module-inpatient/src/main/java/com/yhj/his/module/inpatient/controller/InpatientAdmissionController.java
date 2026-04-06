package com.yhj.his.module.inpatient.controller;

import com.yhj.his.module.inpatient.service.AdmissionService;
import com.yhj.his.module.inpatient.service.DischargeService;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.AdmissionAssessmentDTO;
import com.yhj.his.module.inpatient.dto.AdmissionRegisterDTO;
import com.yhj.his.module.inpatient.dto.DepositPaymentDTO;
import com.yhj.his.module.inpatient.dto.DepositPaymentResponseDTO;
import com.yhj.his.module.inpatient.dto.DischargeApplyDTO;
import com.yhj.his.module.inpatient.dto.DischargeSummaryDTO;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.vo.AdmissionRegisterVO;
import com.yhj.his.module.inpatient.vo.DischargeSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * 住院入院管理控制器
 */
@RestController
@RequestMapping("/api/inpatient/v1/admissions")
@Tag(name = "住院入院管理", description = "住院入院登记、评估、出院等管理接口")
public class InpatientAdmissionController {

    @Autowired
    private AdmissionService admissionService;

    @Autowired
    private DischargeService dischargeService;

    /**
     * 入院登记
     */
    @PostMapping
    @Operation(summary = "入院登记", description = "为患者办理入院登记手续")
    public Result<AdmissionRegisterVO> register(@Valid @RequestBody AdmissionRegisterDTO dto) {
        AdmissionRegisterVO vo = admissionService.register(dto);
        return Result.success("入院登记成功", vo);
    }

    /**
     * 入院评估
     */
    @PostMapping("/{admissionId}/assessment")
    @Operation(summary = "入院评估", description = "对患者进行入院评估")
    public Result<Boolean> assessment(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Valid @RequestBody AdmissionAssessmentDTO dto) {
        dto.setAdmissionId(admissionId);
        boolean result = admissionService.assessment(dto);
        return Result.success("入院评估完成", result);
    }

    /**
     * 预交金缴纳
     */
    @PostMapping("/{admissionId}/deposit")
    @Operation(summary = "预交金缴纳", description = "为住院患者缴纳预交金")
    public Result<DepositPaymentResponseDTO> payDeposit(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Valid @RequestBody DepositPaymentDTO dto) {
        dto.setAdmissionId(admissionId);
        DepositPaymentResponseDTO response = admissionService.payDeposit(dto);
        return Result.success("预交金缴纳成功", response);
    }

    /**
     * 出院申请
     */
    @PostMapping("/{admissionId}/discharge/apply")
    @Operation(summary = "出院申请", description = "提交出院申请")
    public Result<Boolean> applyDischarge(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Valid @RequestBody DischargeApplyDTO dto) {
        dto.setAdmissionId(admissionId);
        boolean result = dischargeService.apply(dto);
        return Result.success("出院申请已提交", result);
    }

    /**
     * 取消出院申请
     */
    @DeleteMapping("/{admissionId}/discharge/apply")
    @Operation(summary = "取消出院申请", description = "取消出院申请")
    public Result<Boolean> cancelDischargeApply(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        boolean result = dischargeService.cancelApply(admissionId);
        return Result.success("出院申请已取消", result);
    }

    /**
     * 出院小结
     */
    @PostMapping("/{admissionId}/discharge/summary")
    @Operation(summary = "出院小结", description = "填写出院小结")
    public Result<String> dischargeSummary(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Valid @RequestBody DischargeSummaryDTO dto) {
        dto.setAdmissionId(admissionId);
        String summaryId = dischargeService.summary(dto);
        return Result.success("出院小结已保存", summaryId);
    }

    /**
     * 查询出院小结
     */
    @GetMapping("/{admissionId}/discharge/summary")
    @Operation(summary = "查询出院小结", description = "获取出院小结信息")
    public Result<DischargeSummaryVO> getDischargeSummary(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        DischargeSummaryVO vo = dischargeService.getSummary(admissionId);
        return Result.success(vo);
    }

    /**
     * 根据ID查询住院记录
     */
    @GetMapping("/{admissionId}")
    @Operation(summary = "查询住院记录", description = "根据住院ID查询住院记录详情")
    public Result<AdmissionRegisterVO> getById(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        AdmissionRegisterVO vo = admissionService.getById(admissionId);
        return Result.success(vo);
    }

    /**
     * 根据住院号查询
     */
    @GetMapping("/no/{admissionNo}")
    @Operation(summary = "根据住院号查询", description = "根据住院号查询住院记录")
    public Result<AdmissionRegisterVO> getByAdmissionNo(
            @Parameter(description = "住院号") @PathVariable String admissionNo) {
        AdmissionRegisterVO vo = admissionService.getByAdmissionNo(admissionNo);
        return Result.success(vo);
    }

    /**
     * 分页查询住院记录
     */
    @GetMapping
    @Operation(summary = "分页查询住院记录", description = "分页查询住院记录列表")
    public Result<PageResult<AdmissionRegisterVO>> page(
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @PageableDefault Pageable pageable) {
        PageResult<AdmissionRegisterVO> result = admissionService.page(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                status);
        return Result.success(result);
    }

    /**
     * 查询待入院患者
     */
    @GetMapping("/pending")
    @Operation(summary = "查询待入院患者", description = "查询待入院状态的患者列表")
    public Result<PageResult<AdmissionRegisterVO>> listPending(@PageableDefault Pageable pageable) {
        PageResult<AdmissionRegisterVO> result = admissionService.page(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                AdmissionStatus.PENDING.name());
        return Result.success(result);
    }

    /**
     * 查询在院患者
     */
    @GetMapping("/in-hospital")
    @Operation(summary = "查询在院患者", description = "查询在院状态的患者列表")
    public Result<PageResult<AdmissionRegisterVO>> listInHospital(@PageableDefault Pageable pageable) {
        PageResult<AdmissionRegisterVO> result = admissionService.page(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                AdmissionStatus.IN_HOSPITAL.name());
        return Result.success(result);
    }

    /**
     * 查询待出院患者
     */
    @GetMapping("/pending-discharge")
    @Operation(summary = "查询待出院患者", description = "查询待出院状态的患者列表")
    public Result<PageResult<AdmissionRegisterVO>> listPendingDischarge(@PageableDefault Pageable pageable) {
        PageResult<AdmissionRegisterVO> result = admissionService.page(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                AdmissionStatus.PENDING_DISCHARGE.name());
        return Result.success(result);
    }
}