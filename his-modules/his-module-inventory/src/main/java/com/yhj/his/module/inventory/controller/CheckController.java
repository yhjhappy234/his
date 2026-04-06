package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.CheckAdjustDTO;
import com.yhj.his.module.inventory.dto.CheckInputDTO;
import com.yhj.his.module.inventory.dto.CheckPlanDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.service.CheckService;
import com.yhj.his.module.inventory.vo.MaterialCheckVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存盘点控制器
 */
@Tag(name = "库存盘点", description = "盘点计划、录入、差异处理接口")
@RestController
@RequestMapping("/api/inventory/v1/check")
@RequiredArgsConstructor
public class CheckController {

    private final CheckService service;

    @Operation(summary = "创建盘点计划")
    @PostMapping("/plan")
    public Result<MaterialCheckVO> createPlan(@Valid @RequestBody CheckPlanDTO dto) {
        return Result.success(service.createPlan(dto));
    }

    @Operation(summary = "开始盘点")
    @PostMapping("/start/{checkId}")
    public Result<MaterialCheckVO> start(@Parameter(description = "盘点ID") @PathVariable String checkId) {
        return Result.success(service.start(checkId));
    }

    @Operation(summary = "盘点录入")
    @PostMapping("/input")
    public Result<MaterialCheckVO> input(@Valid @RequestBody CheckInputDTO dto) {
        return Result.success(service.input(dto));
    }

    @Operation(summary = "完成盘点")
    @PostMapping("/complete/{checkId}")
    public Result<MaterialCheckVO> complete(@Parameter(description = "盘点ID") @PathVariable String checkId) {
        return Result.success(service.complete(checkId));
    }

    @Operation(summary = "差异处理")
    @PostMapping("/adjust")
    public Result<MaterialCheckVO> adjust(@Valid @RequestBody CheckAdjustDTO dto) {
        return Result.success(service.adjust(dto));
    }

    @Operation(summary = "取消盘点")
    @PostMapping("/cancel/{checkId}")
    public Result<Void> cancel(@Parameter(description = "盘点ID") @PathVariable String checkId) {
        service.cancel(checkId);
        return Result.success();
    }

    @Operation(summary = "根据ID查询盘点记录")
    @GetMapping("/{id}")
    public Result<MaterialCheckVO> getById(@Parameter(description = "盘点ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据盘点单号查询")
    @GetMapping("/no/{checkNo}")
    public Result<MaterialCheckVO> getByNo(@Parameter(description = "盘点单号") @PathVariable String checkNo) {
        return Result.success(service.getByNo(checkNo));
    }

    @Operation(summary = "分页查询盘点记录")
    @GetMapping("/list")
    public Result<PageResult<MaterialCheckVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询盘点记录")
    @PostMapping("/query")
    public Result<PageResult<MaterialCheckVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "查询进行中的盘点")
    @GetMapping("/in-progress")
    public Result<List<MaterialCheckVO>> listInProgress() {
        return Result.success(service.listInProgress());
    }
}