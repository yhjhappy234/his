package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.CheckAdjustDTO;
import com.yhj.his.module.inventory.dto.CheckInputDTO;
import com.yhj.his.module.inventory.dto.CheckItemDTO;
import com.yhj.his.module.inventory.dto.CheckPlanDTO;
import com.yhj.his.module.inventory.entity.MaterialCheck;
import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
import com.yhj.his.module.inventory.service.MaterialCheckService;
import com.yhj.his.module.inventory.vo.MaterialCheckItemVO;
import com.yhj.his.module.inventory.vo.MaterialCheckVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存盘点Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/checks")
@Tag(name = "库存盘点管理", description = "库存盘点计划、录入、调整接口")
public class MaterialCheckController {

    @Autowired
    private MaterialCheckService materialCheckService;

    @PostMapping
    @Operation(summary = "创建盘点计划", description = "创建库存盘点计划")
    public Result<MaterialCheckVO> createPlan(@Valid @RequestBody CheckPlanDTO dto) {
        MaterialCheck entity = new MaterialCheck();
        BeanUtils.copyProperties(dto, entity);
        entity.setCheckNo(materialCheckService.generateCheckNo());
        entity.setCheckType(CheckType.valueOf(dto.getCheckType()));
        entity.setStatus(CheckStatus.PENDING);

        // 根据库房初始化盘点明细
        List<MaterialCheckItem> items = materialCheckService.initializeItemsByWarehouse(dto.getWarehouseId());

        MaterialCheck saved = materialCheckService.createWithItems(entity, items);
        return Result.success(convertToVO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除盘点单", description = "删除盘点单(仅待盘点状态)")
    public Result<Void> delete(@Parameter(description = "盘点单ID") @PathVariable String id) {
        materialCheckService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询盘点单详情", description = "根据ID查询盘点单详情")
    public Result<MaterialCheckVO> getById(@Parameter(description = "盘点单ID") @PathVariable String id) {
        return materialCheckService.findById(id)
                .map(entity -> {
                    MaterialCheckVO vo = convertToVO(entity);
                    List<MaterialCheckItem> items = materialCheckService.getItems(id);
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("盘点单不存在"));
    }

    @GetMapping("/no/{checkNo}")
    @Operation(summary = "根据盘点单号查询", description = "根据盘点单号查询盘点单")
    public Result<MaterialCheckVO> getByNo(@Parameter(description = "盘点单号") @PathVariable String checkNo) {
        return materialCheckService.findByCheckNo(checkNo)
                .map(entity -> {
                    MaterialCheckVO vo = convertToVO(entity);
                    List<MaterialCheckItem> items = materialCheckService.getItems(entity.getId());
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("盘点单不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询盘点单", description = "分页查询所有盘点单")
    public Result<PageResult<MaterialCheckVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialCheck> page = materialCheckService.findAll(pageable);
        List<MaterialCheckVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/in-progress")
    @Operation(summary = "查询进行中的盘点", description = "查询所有进行中的盘点单")
    public Result<List<MaterialCheckVO>> listInProgress() {
        List<MaterialCheck> checks = materialCheckService.findInProgressChecks();
        List<MaterialCheckVO> voList = checks.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/completed")
    @Operation(summary = "查询已完成的盘点", description = "查询所有已完成待调整的盘点单")
    public Result<List<MaterialCheckVO>> listCompleted() {
        List<MaterialCheck> checks = materialCheckService.findCompletedChecks();
        List<MaterialCheckVO> voList = checks.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询盘点单", description = "根据库房ID查询盘点单列表")
    public Result<List<MaterialCheckVO>> listByWarehouse(@Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialCheck> checks = materialCheckService.findByWarehouseId(warehouseId);
        List<MaterialCheckVO> voList = checks.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "根据盘点类型查询", description = "根据盘点类型查询盘点单列表")
    public Result<List<MaterialCheckVO>> listByType(@Parameter(description = "盘点类型") @PathVariable String type) {
        CheckType checkType = CheckType.valueOf(type);
        List<MaterialCheck> checks = materialCheckService.findByCheckType(checkType);
        List<MaterialCheckVO> voList = checks.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开始盘点", description = "开始执行盘点")
    public Result<MaterialCheckVO> start(
            @Parameter(description = "盘点单ID") @PathVariable String id,
            @Parameter(description = "盘点人ID") @RequestParam String checkerId,
            @Parameter(description = "盘点人姓名") @RequestParam String checkerName) {
        MaterialCheck check = materialCheckService.startCheck(id, checkerId, checkerName);
        return Result.success(convertToVO(check));
    }

    @PostMapping("/input")
    @Operation(summary = "盘点录入", description = "录入盘点实际数量")
    public Result<MaterialCheckItemVO> inputCheckData(@Valid @RequestBody CheckItemDTO dto) {
        MaterialCheckItem item = materialCheckService.inputCheckData(dto.getItemId(), dto.getActualQuantity(), dto.getRemark());
        return Result.success(convertItemToVO(item));
    }

    @PostMapping("/batch-input")
    @Operation(summary = "批量盘点录入", description = "批量录入盘点实际数量")
    public Result<Void> batchInputCheckData(@Valid @RequestBody CheckInputDTO dto) {
        if (dto.getItems() != null) {
            for (CheckItemDTO itemDTO : dto.getItems()) {
                materialCheckService.inputCheckData(itemDTO.getItemId(), itemDTO.getActualQuantity(), itemDTO.getRemark());
            }
        }
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成盘点", description = "完成盘点并计算差异")
    public Result<MaterialCheckVO> complete(@Parameter(description = "盘点单ID") @PathVariable String id) {
        MaterialCheck check = materialCheckService.completeCheck(id);
        return Result.success(convertToVO(check));
    }

    @PostMapping("/adjust")
    @Operation(summary = "调整库存", description = "根据盘点差异调整库存")
    public Result<MaterialCheckVO> adjust(@Valid @RequestBody CheckAdjustDTO dto) {
        MaterialCheck check = materialCheckService.adjust(dto.getCheckId(), dto.getAdjusterId(), dto.getAdjusterName());
        return Result.success(convertToVO(check));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消盘点", description = "取消盘点计划")
    public Result<MaterialCheckVO> cancel(@Parameter(description = "盘点单ID") @PathVariable String id) {
        MaterialCheck check = materialCheckService.cancel(id);
        return Result.success(convertToVO(check));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询盘点明细", description = "查询盘点单的所有明细")
    public Result<List<MaterialCheckItemVO>> getItems(@Parameter(description = "盘点单ID") @PathVariable String id) {
        List<MaterialCheckItem> items = materialCheckService.getItems(id);
        List<MaterialCheckItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/{id}/diff-items")
    @Operation(summary = "查询有差异的盘点明细", description = "查询有差异的盘点明细项")
    public Result<List<MaterialCheckItemVO>> getDiffItems(@Parameter(description = "盘点单ID") @PathVariable String id) {
        List<MaterialCheckItem> items = materialCheckService.getDiffItems(id);
        List<MaterialCheckItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/{id}/unadjusted-diff-items")
    @Operation(summary = "查询未调整的盘点明细", description = "查询未调整的差异明细项")
    public Result<List<MaterialCheckItemVO>> getUnadjustedDiffItems(@Parameter(description = "盘点单ID") @PathVariable String id) {
        List<MaterialCheckItem> items = materialCheckService.getUnadjustedDiffItems(id);
        List<MaterialCheckItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询盘点单", description = "根据条件查询盘点单")
    public Result<PageResult<MaterialCheckVO>> search(
            @Parameter(description = "库房ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "盘点类型") @RequestParam(required = false) String checkType,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        CheckStatus checkStatus = status != null ? CheckStatus.valueOf(status) : null;
        CheckType checkTypeEnum = checkType != null ? CheckType.valueOf(checkType) : null;
        Page<MaterialCheck> page = materialCheckService.search(warehouseId, checkStatus, checkTypeEnum,
                startDate, endDate, pageable);
        List<MaterialCheckVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    private MaterialCheckVO convertToVO(MaterialCheck entity) {
        MaterialCheckVO vo = new MaterialCheckVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getCheckType() != null) {
            vo.setCheckType(entity.getCheckType().name());
        }
        if (entity.getStatus() != null) {
            vo.setStatus(entity.getStatus().name());
        }
        return vo;
    }

    private MaterialCheckItemVO convertItemToVO(MaterialCheckItem entity) {
        MaterialCheckItemVO vo = new MaterialCheckItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}