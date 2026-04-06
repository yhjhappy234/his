package com.yhj.his.module.pharmacy.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DrugCreateDTO;
import com.yhj.his.module.pharmacy.dto.DrugQueryDTO;
import com.yhj.his.module.pharmacy.dto.DrugUpdateDTO;
import com.yhj.his.module.pharmacy.service.DrugService;
import com.yhj.his.module.pharmacy.vo.DrugVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品信息控制器
 */
@Tag(name = "药品管理", description = "药品信息管理接口")
@RestController
@RequestMapping("/api/pharmacy/v1/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    @Operation(summary = "创建药品", description = "新增药品信息")
    @PostMapping
    public Result<DrugVO> createDrug(@Valid @RequestBody DrugCreateDTO dto) {
        return drugService.createDrug(dto);
    }

    @Operation(summary = "更新药品", description = "更新药品信息")
    @PutMapping("/{drugId}")
    public Result<DrugVO> updateDrug(
            @Parameter(description = "药品ID") @PathVariable String drugId,
            @Valid @RequestBody DrugUpdateDTO dto) {
        return drugService.updateDrug(drugId, dto);
    }

    @Operation(summary = "删除药品", description = "删除药品信息(逻辑删除)")
    @DeleteMapping("/{drugId}")
    public Result<Void> deleteDrug(@Parameter(description = "药品ID") @PathVariable String drugId) {
        return drugService.deleteDrug(drugId);
    }

    @Operation(summary = "获取药品详情", description = "根据ID查询药品信息")
    @GetMapping("/{drugId}")
    public Result<DrugVO> getDrug(@Parameter(description = "药品ID") @PathVariable String drugId) {
        return drugService.getDrugById(drugId);
    }

    @Operation(summary = "根据编码查询药品", description = "根据药品编码查询药品信息")
    @GetMapping("/code/{drugCode}")
    public Result<DrugVO> getDrugByCode(@Parameter(description = "药品编码") @PathVariable String drugCode) {
        return drugService.getDrugByCode(drugCode);
    }

    @Operation(summary = "分页查询药品列表", description = "支持多条件分页查询药品")
    @PostMapping("/query")
    public Result<PageResult<DrugVO>> queryDrugs(@RequestBody DrugQueryDTO dto) {
        return drugService.queryDrugs(dto);
    }

    @Operation(summary = "搜索药品", description = "根据名称或拼音码搜索药品")
    @GetMapping("/search")
    public Result<List<DrugVO>> searchDrugs(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return drugService.searchDrugs(keyword);
    }

    @Operation(summary = "获取药品分类列表", description = "获取所有药品分类")
    @GetMapping("/categories")
    public Result<List<String>> getDrugCategories() {
        return drugService.getDrugCategories();
    }

    @Operation(summary = "按分类查询药品", description = "根据药品分类查询药品列表")
    @GetMapping("/category/{category}")
    public Result<List<DrugVO>> getDrugsByCategory(
            @Parameter(description = "药品分类") @PathVariable String category) {
        return drugService.getDrugsByCategory(category);
    }
}