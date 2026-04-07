package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.DataDictionaryDTO;
import com.yhj.his.module.system.service.DataDictionaryService;
import com.yhj.his.module.system.vo.DataDictionaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典管理控制器
 */
@Tag(name = "数据字典管理", description = "字典类型、字典项管理等接口")
@RestController
@RequestMapping("/api/system/v1/dictionary")
@RequiredArgsConstructor
public class DataDictionaryController {

    private final DataDictionaryService dataDictionaryService;

    @Operation(summary = "创建字典项")
    @PostMapping
    public Result<DataDictionaryVO> create(@Valid @RequestBody DataDictionaryDTO dto) {
        return dataDictionaryService.create(dto);
    }

    @Operation(summary = "更新字典项")
    @PutMapping
    public Result<DataDictionaryVO> update(@Valid @RequestBody DataDictionaryDTO dto) {
        return dataDictionaryService.update(dto);
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/{dictId}")
    public Result<Void> delete(@Parameter(description = "字典ID") @PathVariable String dictId) {
        return dataDictionaryService.delete(dictId);
    }

    @Operation(summary = "获取字典项详情")
    @GetMapping("/{dictId}")
    public Result<DataDictionaryVO> getById(@Parameter(description = "字典ID") @PathVariable String dictId) {
        return dataDictionaryService.getById(dictId);
    }

    @Operation(summary = "分页查询字典项")
    @GetMapping("/page")
    public Result<PageResult<DataDictionaryVO>> page(
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典编码") @RequestParam(required = false) String dictCode,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isEnabled,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return dataDictionaryService.page(dictType, dictName, dictCode, isEnabled, pageNum, pageSize);
    }

    @Operation(summary = "根据字典类型获取字典项列表")
    @GetMapping("/type/{dictType}")
    public Result<List<DataDictionaryVO>> listByType(@Parameter(description = "字典类型") @PathVariable String dictType) {
        return dataDictionaryService.listByType(dictType);
    }

    @Operation(summary = "根据字典类型获取启用的字典项列表")
    @GetMapping("/type/{dictType}/enabled")
    public Result<List<DataDictionaryVO>> listEnabledByType(@Parameter(description = "字典类型") @PathVariable String dictType) {
        return dataDictionaryService.listEnabledByType(dictType);
    }

    @Operation(summary = "获取所有字典类型")
    @GetMapping("/types")
    public Result<List<String>> listAllTypes() {
        return dataDictionaryService.listAllTypes();
    }

    @Operation(summary = "获取字典树")
    @GetMapping("/tree/{dictType}")
    public Result<List<DataDictionaryVO>> getTree(@Parameter(description = "字典类型") @PathVariable String dictType) {
        return dataDictionaryService.getTree(dictType);
    }

    @Operation(summary = "获取字典类型的默认值")
    @GetMapping("/default/{dictType}")
    public Result<DataDictionaryVO> getDefault(@Parameter(description = "字典类型") @PathVariable String dictType) {
        return dataDictionaryService.getDefault(dictType);
    }
}