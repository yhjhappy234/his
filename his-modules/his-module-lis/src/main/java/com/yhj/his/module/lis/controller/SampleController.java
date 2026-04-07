package com.yhj.his.module.lis.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.lis.dto.SampleCollectDTO;
import com.yhj.his.module.lis.dto.SampleReceiveDTO;
import com.yhj.his.module.lis.dto.SampleRejectDTO;
import com.yhj.his.module.lis.enums.SampleStatus;
import com.yhj.his.module.lis.service.SampleService;
import com.yhj.his.module.lis.vo.SampleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 样本REST控制器
 */
@RestController
@RequestMapping("/api/lis/v1/samples")
@RequiredArgsConstructor
@Tag(name = "样本管理", description = "样本的采集、核收、拒收及查询接口")
public class SampleController {

    private final SampleService sampleService;

    @PostMapping("/collect")
    @Operation(summary = "采集样本", description = "采集检验样本")
    public Result<SampleVO> collect(@Valid @RequestBody SampleCollectDTO dto) {
        SampleVO vo = sampleService.collect(dto);
        return Result.success("采集成功", vo);
    }

    @PostMapping("/receive")
    @Operation(summary = "核收样本", description = "核收检验样本")
    public Result<SampleVO> receive(@Valid @RequestBody SampleReceiveDTO dto) {
        SampleVO vo = sampleService.receive(dto);
        return Result.success("核收成功", vo);
    }

    @PostMapping("/reject")
    @Operation(summary = "拒收样本", description = "拒收检验样本")
    public Result<SampleVO> reject(@Valid @RequestBody SampleRejectDTO dto) {
        SampleVO vo = sampleService.reject(dto);
        return Result.success("拒收成功", vo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取样本", description = "根据ID获取样本详情")
    public Result<SampleVO> getById(@Parameter(description = "样本ID") @PathVariable String id) {
        SampleVO vo = sampleService.getById(id);
        return Result.success(vo);
    }

    @GetMapping("/no/{sampleNo}")
    @Operation(summary = "根据样本编号获取", description = "根据样本编号获取样本详情")
    public Result<SampleVO> getBySampleNo(@Parameter(description = "样本编号") @PathVariable String sampleNo) {
        SampleVO vo = sampleService.getBySampleNo(sampleNo);
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "分页查询样本", description = "分页查询样本列表")
    public Result<PageResult<SampleVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        PageResult<SampleVO> result = sampleService.list(pageable);
        return Result.success(result);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "按申请ID查询样本", description = "根据申请ID查询样本列表")
    public Result<List<SampleVO>> listByRequestId(@Parameter(description = "申请ID") @PathVariable String requestId) {
        List<SampleVO> list = sampleService.listByRequestId(requestId);
        return Result.success(list);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "按患者ID查询样本", description = "根据患者ID查询样本列表")
    public Result<List<SampleVO>> listByPatientId(@Parameter(description = "患者ID") @PathVariable String patientId) {
        List<SampleVO> list = sampleService.listByPatientId(patientId);
        return Result.success(list);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询样本", description = "根据状态查询样本列表")
    public Result<List<SampleVO>> listByStatus(@Parameter(description = "状态") @PathVariable String status) {
        SampleStatus sampleStatus = SampleStatus.valueOf(status);
        List<SampleVO> list = sampleService.listByStatus(sampleStatus);
        return Result.success(list);
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待采集样本", description = "查询待采集的样本列表")
    public Result<List<SampleVO>> listPendingSamples() {
        List<SampleVO> list = sampleService.listPendingSamples();
        return Result.success(list);
    }

    @GetMapping("/collected")
    @Operation(summary = "查询待核收样本", description = "查询已采集待核收的样本列表")
    public Result<List<SampleVO>> listCollectedSamples() {
        List<SampleVO> list = sampleService.listCollectedSamples();
        return Result.success(list);
    }

    @GetMapping("/emergency")
    @Operation(summary = "查询急诊样本", description = "查询急诊样本列表")
    public Result<List<SampleVO>> listEmergencySamples() {
        List<SampleVO> list = sampleService.listEmergencySamples();
        return Result.success(list);
    }

    @GetMapping("/collection-time")
    @Operation(summary = "按采集时间查询", description = "根据采集时间范围查询样本")
    public Result<PageResult<SampleVO>> listByCollectionTime(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResult<SampleVO> result = sampleService.listByCollectionTime(startTime, endTime, pageable);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新样本状态", description = "更新样本的状态")
    public Result<SampleVO> updateStatus(
            @Parameter(description = "样本ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        SampleStatus sampleStatus = SampleStatus.valueOf(status);
        SampleVO vo = sampleService.updateStatus(id, sampleStatus);
        return Result.success("状态更新成功", vo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除样本", description = "删除指定的样本")
    public Result<Void> delete(@Parameter(description = "样本ID") @PathVariable String id) {
        sampleService.delete(id);
        return Result.success();
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "统计样本数量", description = "统计指定状态的样本数量")
    public Result<Long> countByStatus(@Parameter(description = "状态") @PathVariable String status) {
        SampleStatus sampleStatus = SampleStatus.valueOf(status);
        long count = sampleService.countByStatus(sampleStatus);
        return Result.success(count);
    }

    @GetMapping("/{id}/label")
    @Operation(summary = "生成样本标签", description = "生成样本标签内容")
    public Result<String> generateLabel(@Parameter(description = "样本ID") @PathVariable String id) {
        String label = sampleService.generateLabel(id);
        return Result.success(label);
    }
}