package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.OutboundDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.service.OutboundService;
import com.yhj.his.module.inventory.vo.MaterialOutboundVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出库管理控制器
 */
@Tag(name = "出库管理", description = "出库申请、审核、确认接口")
@RestController
@RequestMapping("/api/inventory/v1/outbound")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundService service;

    @Operation(summary = "出库申请")
    @PostMapping("/apply")
    public Result<MaterialOutboundVO> apply(@Valid @RequestBody OutboundDTO dto) {
        return Result.success(service.apply(dto));
    }

    @Operation(summary = "出库审核")
    @PostMapping("/audit")
    public Result<MaterialOutboundVO> audit(@Valid @RequestBody AuditDTO dto) {
        return Result.success(service.audit(dto));
    }

    @Operation(summary = "出库确认")
    @PostMapping("/confirm")
    public Result<MaterialOutboundVO> confirm(@Valid @RequestBody ConfirmDTO dto) {
        return Result.success(service.confirm(dto));
    }

    @Operation(summary = "取消出库")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@Parameter(description = "出库ID") @PathVariable String id) {
        service.cancel(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询出库记录")
    @GetMapping("/{id}")
    public Result<MaterialOutboundVO> getById(@Parameter(description = "出库ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据出库单号查询")
    @GetMapping("/no/{outboundNo}")
    public Result<MaterialOutboundVO> getByNo(@Parameter(description = "出库单号") @PathVariable String outboundNo) {
        return Result.success(service.getByNo(outboundNo));
    }

    @Operation(summary = "分页查询出库记录")
    @GetMapping("/list")
    public Result<PageResult<MaterialOutboundVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询出库记录")
    @PostMapping("/query")
    public Result<PageResult<MaterialOutboundVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "查询待审核出库单")
    @GetMapping("/pending")
    public Result<List<MaterialOutboundVO>> listPending() {
        return Result.success(service.listPending());
    }
}