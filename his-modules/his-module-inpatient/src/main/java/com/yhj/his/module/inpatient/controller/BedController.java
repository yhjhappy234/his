package com.yhj.his.module.inpatient.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.service.BedService;
import com.yhj.his.module.inpatient.vo.BedVO;
import com.yhj.his.module.inpatient.vo.WardBedStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 床位管理控制器
 */
@Tag(name = "床位管理", description = "床位查询、床位分配、床位调换、床位状态管理等接口")
@RestController
@RequestMapping("/api/inpatient/v1/bed")
@RequiredArgsConstructor
public class BedController {

    private final BedService bedService;

    @Operation(summary = "查询床位列表", description = "根据条件查询床位列表")
    @GetMapping("/list")
    public Result<List<BedVO>> list(BedQueryDTO queryDTO) {
        List<BedVO> list = bedService.list(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "查询病区床位统计", description = "查询病区床位使用情况统计")
    @GetMapping("/statistics/{wardId}")
    public Result<WardBedStatisticsVO> getStatistics(
            @Parameter(description = "病区ID") @PathVariable String wardId) {
        WardBedStatisticsVO vo = bedService.getStatistics(wardId);
        return Result.success(vo);
    }

    @Operation(summary = "分配床位", description = "为住院患者分配床位")
    @PostMapping("/assign")
    public Result<Boolean> assign(@Valid @RequestBody BedAssignDTO dto) {
        boolean result = bedService.assign(dto);
        return Result.success("床位分配成功", result);
    }

    @Operation(summary = "调换床位", description = "为住院患者调换床位")
    @PostMapping("/change")
    public Result<Boolean> change(@Valid @RequestBody BedChangeDTO dto) {
        boolean result = bedService.change(dto);
        return Result.success("床位调换成功", result);
    }

    @Operation(summary = "更新床位状态", description = "更新床位状态（维修、隔离等）")
    @PostMapping("/status")
    public Result<Boolean> updateStatus(@Valid @RequestBody BedStatusUpdateDTO dto) {
        boolean result = bedService.updateStatus(dto);
        return Result.success("床位状态更新成功", result);
    }

    @Operation(summary = "查询床位详情", description = "根据床位ID查询床位详情")
    @GetMapping("/{bedId}")
    public Result<BedVO> getById(
            @Parameter(description = "床位ID") @PathVariable String bedId) {
        BedVO vo = bedService.getById(bedId);
        return Result.success(vo);
    }

    @Operation(summary = "分页查询床位", description = "分页查询床位列表")
    @GetMapping("/page")
    public Result<PageResult<BedVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            BedQueryDTO queryDTO) {
        PageResult<BedVO> result = bedService.page(pageNum, pageSize, queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "释放床位", description = "释放指定住院患者的床位")
    @PostMapping("/release/{admissionId}")
    public Result<Boolean> release(
            @Parameter(description = "住院ID") @PathVariable String admissionId) {
        boolean result = bedService.release(admissionId);
        return Result.success("床位释放成功", result);
    }
}