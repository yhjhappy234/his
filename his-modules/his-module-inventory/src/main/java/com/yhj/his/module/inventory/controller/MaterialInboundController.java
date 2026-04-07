package com.yhj.his.module.inventory.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.inventory.dto.AuditDTO;
import com.yhj.his.module.inventory.dto.ConfirmDTO;
import com.yhj.his.module.inventory.dto.InboundDTO;
import com.yhj.his.module.inventory.dto.InboundItemDTO;
import com.yhj.his.module.inventory.entity.MaterialInbound;
import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
import com.yhj.his.module.inventory.service.MaterialInboundService;
import com.yhj.his.module.inventory.vo.MaterialInboundItemVO;
import com.yhj.his.module.inventory.vo.MaterialInboundVO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入库管理Controller
 */
@RestController
@RequestMapping("/api/inventory/v1/inbounds")
@Tag(name = "入库管理", description = "物资入库登记、审核、确认接口")
public class MaterialInboundController {

    @Autowired
    private MaterialInboundService materialInboundService;

    @PostMapping
    @Operation(summary = "入库登记", description = "创建入库登记单")
    public Result<MaterialInboundVO> register(@Valid @RequestBody InboundDTO dto) {
        MaterialInbound entity = new MaterialInbound();
        BeanUtils.copyProperties(dto, entity);
        entity.setInboundNo(materialInboundService.generateInboundNo());
        entity.setInboundType(InboundType.valueOf(dto.getInboundType()));
        entity.setStatus(InboundStatus.PENDING);

        List<MaterialInboundItem> items = null;
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            items = dto.getItems().stream().map(this::convertItemDTOToEntity).collect(Collectors.toList());
        }

        MaterialInbound saved = items != null ?
                materialInboundService.createWithItems(entity, items) :
                materialInboundService.create(entity);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新入库单", description = "更新入库单信息(仅待审核状态)")
    public Result<MaterialInboundVO> update(
            @Parameter(description = "入库单ID") @PathVariable String id,
            @Valid @RequestBody InboundDTO dto) {
        MaterialInbound entity = new MaterialInbound();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getInboundType() != null) {
            entity.setInboundType(InboundType.valueOf(dto.getInboundType()));
        }
        MaterialInbound updated = materialInboundService.update(id, entity);
        return Result.success(convertToVO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除入库单", description = "删除入库单(仅待审核状态)")
    public Result<Void> delete(@Parameter(description = "入库单ID") @PathVariable String id) {
        materialInboundService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询入库单详情", description = "根据ID查询入库单详情")
    public Result<MaterialInboundVO> getById(@Parameter(description = "入库单ID") @PathVariable String id) {
        return materialInboundService.findById(id)
                .map(entity -> {
                    MaterialInboundVO vo = convertToVO(entity);
                    List<MaterialInboundItem> items = materialInboundService.getItems(id);
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("入库单不存在"));
    }

    @GetMapping("/no/{inboundNo}")
    @Operation(summary = "根据入库单号查询", description = "根据入库单号查询入库单")
    public Result<MaterialInboundVO> getByNo(@Parameter(description = "入库单号") @PathVariable String inboundNo) {
        return materialInboundService.findByInboundNo(inboundNo)
                .map(entity -> {
                    MaterialInboundVO vo = convertToVO(entity);
                    List<MaterialInboundItem> items = materialInboundService.getItems(entity.getId());
                    vo.setItems(items.stream().map(this::convertItemToVO).collect(Collectors.toList()));
                    return Result.success(vo);
                })
                .orElse(Result.error("入库单不存在"));
    }

    @GetMapping
    @Operation(summary = "分页查询入库单", description = "分页查询所有入库单")
    public Result<PageResult<MaterialInboundVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        Page<MaterialInbound> page = materialInboundService.findAll(pageable);
        List<MaterialInboundVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待审核入库单", description = "查询所有待审核的入库单")
    public Result<List<MaterialInboundVO>> listPending() {
        List<MaterialInbound> inbounds = materialInboundService.findPendingInbounds();
        List<MaterialInboundVO> voList = inbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "根据库房查询入库单", description = "根据库房ID查询入库单列表")
    public Result<List<MaterialInboundVO>> listByWarehouse(@Parameter(description = "库房ID") @PathVariable String warehouseId) {
        List<MaterialInbound> inbounds = materialInboundService.findByWarehouseId(warehouseId);
        List<MaterialInboundVO> voList = inbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "根据入库类型查询", description = "根据入库类型查询入库单列表")
    public Result<List<MaterialInboundVO>> listByType(@Parameter(description = "入库类型") @PathVariable String type) {
        InboundType inboundType = InboundType.valueOf(type);
        List<MaterialInbound> inbounds = materialInboundService.findByInboundType(inboundType);
        List<MaterialInboundVO> voList = inbounds.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交入库申请", description = "提交入库申请进入审核流程")
    public Result<MaterialInboundVO> submit(@Parameter(description = "入库单ID") @PathVariable String id) {
        MaterialInbound inbound = materialInboundService.submit(id);
        return Result.success(convertToVO(inbound));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核入库申请", description = "审核入库申请(通过或驳回)")
    public Result<MaterialInboundVO> audit(@Valid @RequestBody AuditDTO dto) {
        boolean approved = "通过".equals(dto.getResult()) || "APPROVED".equals(dto.getResult());
        MaterialInbound inbound = materialInboundService.audit(dto.getId(), dto.getAuditorId(),
                dto.getAuditorName(), approved, dto.getRemark());
        return Result.success(convertToVO(inbound));
    }

    @PostMapping("/confirm")
    @Operation(summary = "确认入库", description = "确认入库并更新库存")
    public Result<MaterialInboundVO> confirm(@Valid @RequestBody ConfirmDTO dto) {
        MaterialInbound inbound = materialInboundService.confirm(dto.getId(), dto.getOperatorId(), dto.getOperatorName());
        return Result.success(convertToVO(inbound));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消入库", description = "取消入库申请")
    public Result<MaterialInboundVO> cancel(@Parameter(description = "入库单ID") @PathVariable String id) {
        MaterialInbound inbound = materialInboundService.cancel(id);
        return Result.success(convertToVO(inbound));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "添加入库明细", description = "向入库单添加明细项")
    public Result<MaterialInboundItemVO> addItem(
            @Parameter(description = "入库单ID") @PathVariable String id,
            @Valid @RequestBody InboundItemDTO itemDTO) {
        MaterialInboundItem item = convertItemDTOToEntity(itemDTO);
        MaterialInboundItem saved = materialInboundService.addItem(id, item);
        return Result.success(convertItemToVO(saved));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "删除入库明细", description = "删除入库单明细项")
    public Result<Void> deleteItem(
            @Parameter(description = "入库单ID") @PathVariable String id,
            @Parameter(description = "明细ID") @PathVariable String itemId) {
        materialInboundService.deleteItem(itemId);
        return Result.success();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询入库明细", description = "查询入库单的所有明细")
    public Result<List<MaterialInboundItemVO>> getItems(@Parameter(description = "入库单ID") @PathVariable String id) {
        List<MaterialInboundItem> items = materialInboundService.getItems(id);
        List<MaterialInboundItemVO> voList = items.stream().map(this::convertItemToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/search")
    @Operation(summary = "条件查询入库单", description = "根据条件查询入库单")
    public Result<PageResult<MaterialInboundVO>> search(
            @Parameter(description = "库房ID") @RequestParam(required = false) String warehouseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "入库类型") @RequestParam(required = false) String inboundType,
            @Parameter(description = "开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
        InboundStatus inboundStatus = status != null ? InboundStatus.valueOf(status) : null;
        InboundType inboundTypeEnum = inboundType != null ? InboundType.valueOf(inboundType) : null;
        Page<MaterialInbound> page = materialInboundService.search(warehouseId, inboundStatus, inboundTypeEnum,
                startDate, endDate, pageable);
        List<MaterialInboundVO> list = page.getContent().stream()
                .map(this::convertToVO).collect(Collectors.toList());
        return Result.success(PageResult.of(list, page.getTotalElements(), pageNum, pageSize));
    }

    private MaterialInboundVO convertToVO(MaterialInbound entity) {
        MaterialInboundVO vo = new MaterialInboundVO();
        BeanUtils.copyProperties(entity, vo);
        if (entity.getInboundType() != null) {
            vo.setInboundType(entity.getInboundType().name());
        }
        if (entity.getStatus() != null) {
            vo.setStatus(entity.getStatus().name());
        }
        return vo;
    }

    private MaterialInboundItemVO convertItemToVO(MaterialInboundItem entity) {
        MaterialInboundItemVO vo = new MaterialInboundItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private MaterialInboundItem convertItemDTOToEntity(InboundItemDTO dto) {
        MaterialInboundItem entity = new MaterialInboundItem();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}