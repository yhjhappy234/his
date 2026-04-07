package com.yhj.his.module.finance.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.finance.dto.PriceAdjustDTO;
import com.yhj.his.module.finance.dto.PriceItemCreateDTO;
import com.yhj.his.module.finance.dto.PriceItemUpdateDTO;
import com.yhj.his.module.finance.service.PriceItemService;
import com.yhj.his.module.finance.vo.PriceItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 价表管理控制器
 */
@RestController
@RequestMapping("/api/finance/v1/price-item")
@RequiredArgsConstructor
@Tag(name = "价表管理", description = "收费项目管理、价格调整等")
public class PriceItemController {

    private final PriceItemService priceItemService;

    @PostMapping
    @Operation(summary = "创建收费项目")
    public Result<PriceItemVO> create(@Valid @RequestBody PriceItemCreateDTO dto) {
        return Result.success("创建成功", priceItemService.create(dto));
    }

    @PutMapping
    @Operation(summary = "更新收费项目")
    public Result<PriceItemVO> update(@Valid @RequestBody PriceItemUpdateDTO dto) {
        return Result.success("更新成功", priceItemService.update(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除收费项目")
    public Result<Void> delete(@Parameter(description = "项目ID") @PathVariable String id) {
        priceItemService.delete(id);
        return Result.successVoid();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询收费项目")
    public Result<PriceItemVO> getById(@Parameter(description = "项目ID") @PathVariable String id) {
        return Result.success(priceItemService.getById(id));
    }

    @GetMapping("/code/{itemCode}")
    @Operation(summary = "根据编码查询收费项目")
    public Result<PriceItemVO> getByCode(@Parameter(description = "项目编码") @PathVariable String itemCode) {
        return Result.success(priceItemService.getByCode(itemCode));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询收费项目")
    public Result<PageResult<PriceItemVO>> pageList(
            @Parameter(description = "项目名称") @RequestParam(required = false) String itemName,
            @Parameter(description = "项目分类") @RequestParam(required = false) String itemCategory,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(priceItemService.pageList(itemName, itemCategory, status, pageNum, pageSize));
    }

    @GetMapping("/list/category/{category}")
    @Operation(summary = "根据分类查询收费项目列表")
    public Result<List<PriceItemVO>> listByCategory(@Parameter(description = "项目分类") @PathVariable String category) {
        return Result.success(priceItemService.listByCategory(category));
    }

    @GetMapping("/list/effective")
    @Operation(summary = "查询生效的收费项目列表")
    public Result<List<PriceItemVO>> listEffectiveItems() {
        return Result.success(priceItemService.listEffectiveItems());
    }

    @PostMapping("/adjust/{itemId}")
    @Operation(summary = "调整价格")
    public Result<PriceAdjustDTO> adjustPrice(
            @Parameter(description = "项目ID") @PathVariable String itemId,
            @Parameter(description = "新价格") @RequestParam BigDecimal newPrice,
            @Parameter(description = "调价原因") @RequestParam String reason) {
        return Result.success("调价成功", priceItemService.adjustPrice(itemId, newPrice, reason));
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "更新项目状态")
    public Result<PriceItemVO> updateStatus(
            @Parameter(description = "项目ID") @PathVariable String id,
            @Parameter(description = "状态") @RequestParam String status) {
        return Result.success(priceItemService.updateStatus(id, status));
    }

    @PostMapping("/batch-import")
    @Operation(summary = "批量导入收费项目")
    public Result<List<PriceItemVO>> batchImport(@RequestBody List<PriceItemCreateDTO> items) {
        return Result.success("批量导入完成", priceItemService.batchImport(items));
    }

    @GetMapping("/search")
    @Operation(summary = "根据名称搜索收费项目")
    public Result<List<PriceItemVO>> searchByName(@Parameter(description = "项目名称") @RequestParam String itemName) {
        return Result.success(priceItemService.searchByName(itemName));
    }
}