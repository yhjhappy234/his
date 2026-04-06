package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.InsurancePolicyCreateDTO;
import com.yhj.his.module.finance.dto.InsurancePolicyUpdateDTO;
import com.yhj.his.module.finance.service.InsurancePolicyService;
import com.yhj.his.module.finance.vo.InsurancePolicyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 医保政策管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/insurance-policy")
@RequiredArgsConstructor
@Tag(name = "医保政策管理", description = "医保类型配置、报销比例设置等")
public class InsurancePolicyController {

    private final InsurancePolicyService insurancePolicyService;

    @PostMapping
    @Operation(summary = "创建医保政策")
    public Result<InsurancePolicyVO> create(@Valid @RequestBody InsurancePolicyCreateDTO dto) {
        return Result.success("创建成功", insurancePolicyService.create(dto));
    }

    @PutMapping
    @Operation(summary = "更新医保政策")
    public Result<InsurancePolicyVO> update(@Valid @RequestBody InsurancePolicyUpdateDTO dto) {
        return Result.success("更新成功", insurancePolicyService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除医保政策")
    public Result<Void> delete(@Parameter(description = "政策ID") @PathVariable String id) {
        insurancePolicyService.delete(id);
        return Result.successVoid();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询医保政策")
    public Result<InsurancePolicyVO> getById(@Parameter(description = "政策ID") @PathVariable String id) {
        return Result.success(insurancePolicyService.getById(id));
    }

    @GetMapping("/type/{insuranceType}")
    @Operation(summary = "根据医保类型查询政策")
    public Result<InsurancePolicyVO> getByInsuranceType(@Parameter(description = "医保类型") @PathVariable String insuranceType) {
        return Result.success(insurancePolicyService.getByInsuranceType(insuranceType));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询医保政策")
    public Result<PageResult<InsurancePolicyVO>> pageList(
            @Parameter(description = "政策名称") @RequestParam(required = false) String policyName,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(insurancePolicyService.pageList(policyName, status, pageNum, pageSize));
    }

    @GetMapping("/list/active")
    @Operation(summary = "查询所有启用的医保政策")
    public Result<List<InsurancePolicyVO>> listAllActive() {
        return Result.success(insurancePolicyService.listAllActive());
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "更新医保政策状态")
    public Result<InsurancePolicyVO> updateStatus(
            @Parameter(description = "政策ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        return Result.success(insurancePolicyService.updateStatus(id, status));
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算医保报销金额")
    public Result<BigDecimal> calculateInsuranceAmount(
            @Parameter(description = "医保类型") @RequestParam String insuranceType,
            @Parameter(description = "项目医保类型") @RequestParam String itemInsuranceType,
            @Parameter(description = "项目金额") @RequestParam BigDecimal amount) {
        return Result.success(insurancePolicyService.calculateInsuranceAmount(insuranceType, itemInsuranceType, amount));
    }

    @PostMapping("/calculate-settlement")
    @Operation(summary = "计算医保结算")
    public Result<InsurancePolicyService.InsuranceSettlementResult> calculateSettlement(
            @Parameter(description = "医保类型") @RequestParam String insuranceType,
            @Parameter(description = "总金额") @RequestParam BigDecimal totalAmount,
            @Parameter(description = "甲类金额") @RequestParam BigDecimal classAAmount,
            @Parameter(description = "乙类金额") @RequestParam BigDecimal classBAmount,
            @Parameter(description = "丙类金额") @RequestParam BigDecimal classCAmount) {
        return Result.success(insurancePolicyService.calculateSettlement(insuranceType, totalAmount, classAAmount, classBAmount, classCAmount));
    }
}