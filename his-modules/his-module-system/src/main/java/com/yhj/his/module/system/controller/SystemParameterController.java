package com.yhj.his.module.system.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.system.dto.SystemParameterDTO;
import com.yhj.his.module.system.service.SystemParameterService;
import com.yhj.his.module.system.vo.SystemParameterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统参数管理控制器
 */
@Tag(name = "系统参数管理", description = "系统参数配置、查询等接口")
@RestController
@RequestMapping("/api/system/v1/parameter")
@RequiredArgsConstructor
public class SystemParameterController {

    private final SystemParameterService systemParameterService;

    @Operation(summary = "创建参数")
    @PostMapping
    public Result<SystemParameterVO> create(@Valid @RequestBody SystemParameterDTO dto) {
        return systemParameterService.create(dto);
    }

    @Operation(summary = "更新参数")
    @PutMapping
    public Result<SystemParameterVO> update(@Valid @RequestBody SystemParameterDTO dto) {
        return systemParameterService.update(dto);
    }

    @Operation(summary = "删除参数")
    @DeleteMapping("/{paramId}")
    public Result<Void> delete(@Parameter(description = "参数ID") @PathVariable String paramId) {
        return systemParameterService.delete(paramId);
    }

    @Operation(summary = "获取参数详情")
    @GetMapping("/{paramId}")
    public Result<SystemParameterVO> getById(@Parameter(description = "参数ID") @PathVariable String paramId) {
        return systemParameterService.getById(paramId);
    }

    @Operation(summary = "根据参数编码获取参数值")
    @GetMapping("/value/{paramCode}")
    public Result<String> getValueByCode(@Parameter(description = "参数编码") @PathVariable String paramCode) {
        return systemParameterService.getValueByCode(paramCode);
    }

    @Operation(summary = "分页查询参数")
    @GetMapping("/page")
    public Result<PageResult<SystemParameterVO>> page(
            @Parameter(description = "参数名称") @RequestParam(required = false) String paramName,
            @Parameter(description = "参数编码") @RequestParam(required = false) String paramCode,
            @Parameter(description = "参数分组") @RequestParam(required = false) String paramGroup,
            @Parameter(description = "参数类型") @RequestParam(required = false) String paramType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        return systemParameterService.page(paramName, paramCode, paramGroup, paramType, pageNum, pageSize);
    }

    @Operation(summary = "获取所有参数列表")
    @GetMapping("/list")
    public Result<List<SystemParameterVO>> listAll() {
        return systemParameterService.listAll();
    }

    @Operation(summary = "根据分组获取参数列表")
    @GetMapping("/group/{paramGroup}")
    public Result<List<SystemParameterVO>> listByGroup(@Parameter(description = "参数分组") @PathVariable String paramGroup) {
        return systemParameterService.listByGroup(paramGroup);
    }

    @Operation(summary = "根据编码列表获取参数Map")
    @PostMapping("/values")
    public Result<Map<String, String>> getValuesByCodes(@RequestBody List<String> paramCodes) {
        return systemParameterService.getValuesByCodes(paramCodes);
    }
}