package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.InboundDTO;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.service.InboundService;
import com.yhj.his.module.inventory.vo.MaterialInboundVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 入库管理控制器
 */
@Tag(name = "入库管理", description = "入库登记、审核、确认接口")
@RestController
@RequestMapping("/api/inventory/v1/inbound")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService service;

    @Operation(summary = "入库登记")
    @PostMapping("/register")
    public Result<MaterialInboundVO> register(@Valid @RequestBody InboundDTO dto) {
        return Result.success(service.register(dto));
    }

    @Operation(summary = "入库审核")
    @PostMapping("/audit")
    public Result<MaterialInboundVO> audit(@Valid @RequestBody AuditDTO dto) {
        return Result.success(service.audit(dto));
    }

    @Operation(summary = "入库确认")
    @PostMapping("/confirm")
    public Result<MaterialInboundVO> confirm(@Valid @RequestBody ConfirmDTO dto) {
        return Result.success(service.confirm(dto));
    }

    @Operation(summary = "取消入库")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@Parameter(description = "入库ID") @PathVariable String id) {
        service.cancel(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询入库记录")
    @GetMapping("/{id}")
    public Result<MaterialInboundVO> getById(@Parameter(description = "入库ID") @PathVariable String id) {
        return Result.success(service.getById(id));
    }

    @Operation(summary = "根据入库单号查询")
    @GetMapping("/no/{inboundNo}")
    public Result<MaterialInboundVO> getByNo(@Parameter(description = "入库单号") @PathVariable String inboundNo) {
        return Result.success(service.getByNo(inboundNo));
    }

    @Operation(summary = "分页查询入库记录")
    @GetMapping("/list")
    public Result<PageResult<MaterialInboundVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.list(pageNum, pageSize));
    }

    @Operation(summary = "条件查询入库记录")
    @PostMapping("/query")
    public Result<PageResult<MaterialInboundVO>> query(@RequestBody QueryDTO query) {
        return Result.success(service.query(query));
    }

    @Operation(summary = "查询待审核入库单")
    @GetMapping("/pending")
    public Result<List<MaterialInboundVO>> listPending() {
        return Result.success(service.listPending());
    }
}