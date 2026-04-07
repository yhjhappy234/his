package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.service.InpatientFeeService;
import com.yhj.his.module.inpatient.vo.InpatientFeeSummaryVO;
import com.yhj.his.module.inpatient.vo.InpatientFeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 住院费用控制器
 */
@Tag(name = "住院费用", description = "住院费用查询、费用汇总等接口")
@RestController
@RequestMapping("/api/inpatient/v1/fee")
@RequiredArgsConstructor
public class InpatientFeeController {

    private final InpatientFeeService feeService;

    @Operation(summary = "查询费用明细", description = "查询患者的费用明细列表")
    @GetMapping("/list/{admissionId}")
    public Result<List<InpatientFeeVO>> listByAdmission(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        List<InpatientFeeVO> list = feeService.listByAdmission(admissionId);
        return Result.success(list);
    }

    @Operation(summary = "查询费用汇总", description = "查询患者的费用汇总信息")
    @GetMapping("/summary/{admissionId}")
    public Result<InpatientFeeSummaryVO> getSummary(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        InpatientFeeSummaryVO vo = feeService.getSummary(admissionId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询费用", description = "分页查询费用明细")
    @GetMapping("/page")
    public Result<PageResult<InpatientFeeVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "住院ID") @RequestParam String admissionId) {
        PageResult<InpatientFeeVO> result = feeService.page(pageNum, pageSize, admissionId);
        return Result.success(result);
    }

    @Operation(summary = "查询未结算费用", description = "查询患者的未结算费用总额")
    @GetMapping("/unsettled/{admissionId}")
    public Result<BigDecimal> getUnsettledAmount(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        BigDecimal amount = feeService.getUnsettledAmount(admissionId);
        return Result.success(amount);
    }

    @Operation(summary = "查询每日费用", description = "查询指定日期的费用明细")
    @GetMapping("/daily/{admissionId}")
    public Result<List<InpatientFeeVO>> listByDate(
            @Parameter(description = "住院ID") @PathVariable String admissionId,
            @Parameter(description = "费用日期") @RequestParam String feeDate) {
        List<InpatientFeeVO> list = feeService.listByDate(admissionId, feeDate);
        return Result.success(list);
    }
}