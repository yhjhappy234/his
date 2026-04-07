package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.service.TestRequestItemService;
import com.yhj.his.module.lis.vo.TestRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检验申请明细REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/test-request-items")
@RequiredArgsConstructor
@Tag(name = "检验申请明细管理", description = "检验申请明细的查询及更新接口")
public class TestRequestItemController {

    private final TestRequestItemService testRequestItemService;

    @GetMapping("/request/{requestId}")
    @Operation(summary = "按申请ID查询明细", description = "根据申请ID查询所有申请明细")
    public Result<List<TestRequestVO.TestRequestItemVO>> listByRequestId(
            @Parameter(description = "申请ID") @PathVariable String requestId) {
        List<TestRequestVO.TestRequestItemVO> list = testRequestItemService.listByRequestId(requestId);
        return Result.success(list);
    }

    @GetMapping("/sample/{sampleId}")
    @Operation(summary = "按样本ID查询明细", description = "根据样本ID查询申请明细")
    public Result<List<TestRequestVO.TestRequestItemVO>> listBySampleId(
            @Parameter(description = "样本ID") @PathVariable String sampleId) {
        List<TestRequestVO.TestRequestItemVO> list = testRequestItemService.listBySampleId(sampleId);
        return Result.success(list);
    }

    @PutMapping("/{id}/sample")
    @Operation(summary = "更新样本ID", description = "更新申请明细的样本ID")
    public Result<Void> updateSampleId(
            @Parameter(description = "明细ID") @PathVariable String id,
            @Parameter(description = "样本ID") @RequestParam String sampleId) {
        testRequestItemService.updateSampleId(id, sampleId);
        return Result.success();
    }

    @PutMapping("/{id}/result-status")
    @Operation(summary = "更新结果状态", description = "更新申请明细的结果状态")
    public Result<Void> updateResultStatus(
            @Parameter(description = "明细ID") @PathVariable String id,
            @Parameter(description = "结果状态") @RequestParam String resultStatus) {
        testRequestItemService.updateResultStatus(id, resultStatus);
        return Result.success();
    }

    @DeleteMapping("/request/{requestId}")
    @Operation(summary = "删除申请明细", description = "根据申请ID删除所有明细")
    public Result<Void> deleteByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        testRequestItemService.deleteByRequestId(requestId);
        return Result.success();
    }

    @GetMapping("/count/{requestId}")
    @Operation(summary = "统计明细数量", description = "统计申请的项目数量")
    public Result<Long> countByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        long count = testRequestItemService.countByRequestId(requestId);
        return Result.success(count);
    }
}