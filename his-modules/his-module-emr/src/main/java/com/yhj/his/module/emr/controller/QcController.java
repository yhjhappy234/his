package com.yhj.his.module.emr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.emr.dto.RectificationDTO;
import com.yhj.his.module.emr.service.QcService;
import com.yhj.his.module.emr.vo.QcResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 病历质控Controller
 */
@Tag(name = "病历质控管理", description = "质控检查、评分、缺陷标记、整改通知")
@RestController
@RequestMapping("/api/emr/v1/qc")
@RequiredArgsConstructor
public class QcController {

    private final QcService qcService;

    @Operation(summary = "执行质控检查")
    @PostMapping("/check")
    public Result<QcResultVO> performQcCheck(
            @Parameter(description = "病历记录ID", required = true) @RequestParam String recordId,
            @Parameter(description = "记录类型", required = true) @RequestParam String recordType) {
        QcResultVO vo = qcService.performQcCheck(recordId, recordType);
        return Result.success("质控检查完成", vo);
    }

    @Operation(summary = "获取质控结果")
    @GetMapping("/result/{recordId}")
    public Result<QcResultVO> getQcResult(@PathVariable String recordId) {
        QcResultVO vo = qcService.getQcResult(recordId);
        return Result.success(vo);
    }

    @Operation(summary = "获取质控结果详情")
    @GetMapping("/detail/{qcResultId}")
    public Result<QcResultVO> getQcResultById(@PathVariable String qcResultId) {
        QcResultVO vo = qcService.getQcResultById(qcResultId);
        return Result.success(vo);
    }

    @Operation(summary = "获取患者质控记录")
    @GetMapping("/patient/{patientId}")
    public Result<PageResult<QcResultVO>> getPatientQcResults(
            @PathVariable String patientId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<QcResultVO> result = qcService.getPatientQcResults(patientId, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "获取待整改列表")
    @GetMapping("/pendingRectification")
    public Result<PageResult<QcResultVO>> getPendingRectificationList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<QcResultVO> result = qcService.getPendingRectificationList(pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "发送整改通知")
    @PostMapping("/notify/{qcResultId}")
    public Result<Void> sendRectificationNotice(@PathVariable String qcResultId) {
        qcService.sendRectificationNotice(qcResultId);
        return Result.successVoid();
    }

    @Operation(summary = "完成整改")
    @PostMapping("/rectify")
    public Result<Void> completeRectification(@RequestBody RectificationDTO dto) {
        qcService.completeRectification(dto);
        return Result.successVoid();
    }

    @Operation(summary = "人工复核质控结果")
    @PostMapping("/review/{qcResultId}")
    public Result<QcResultVO> manualReview(
            @PathVariable String qcResultId,
            @Parameter(description = "质控人ID", required = true) @RequestParam String qcUserId,
            @Parameter(description = "质控人姓名", required = true) @RequestParam String qcUserName,
            @Parameter(description = "评分", required = true) @RequestParam Integer score,
            @Parameter(description = "备注") @RequestParam(required = false) String comment) {
        QcResultVO vo = qcService.manualReview(qcResultId, qcUserId, qcUserName, score, comment);
        return Result.success("复核完成", vo);
    }
}