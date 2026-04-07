package com.yhj.his.module.hr.controller;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.SalaryStructure;
import com.yhj.his.module.hr.repository.SalaryStructureRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 薪资结构管理控制器
 */
@RestController
@RequestMapping("/api/hr/v1/salary-structures")
@Tag(name = "薪资结构管理", description = "薪资结构管理相关接口")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureRepository salaryStructureRepository;

    @PostMapping
    @Operation(summary = "创建薪资结构", description = "新增薪资结构")
    public Result<SalaryStructureVO> createStructure(@Valid @RequestBody SalaryStructureCreateDTO dto) {
        SalaryStructure structure = new SalaryStructure();
        structure.setStructureName(dto.getStructureName());
        structure.setStructureCode(dto.getStructureCode());
        structure.setSalaryType(dto.getSalaryType());
        structure.setItemName(dto.getItemName());
        structure.setItemCode(dto.getItemCode());
        structure.setDefaultAmount(dto.getDefaultAmount());
        structure.setMinAmount(dto.getMinAmount());
        structure.setMaxAmount(dto.getMaxAmount());
        structure.setCalcFormula(dto.getCalcFormula());
        structure.setTaxable(dto.getTaxable() != null ? dto.getTaxable() : true);
        structure.setInsuranceable(dto.getInsuranceable() != null ? dto.getInsuranceable() : true);
        structure.setSortOrder(dto.getSortOrder());
        structure.setStatus(dto.getStatus() != null ? dto.getStatus() : "启用");
        structure.setDescription(dto.getDescription());
        structure.setRemark(dto.getRemark());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return Result.success(convertToVO(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新薪资结构", description = "更新薪资结构信息")
    public Result<SalaryStructureVO> updateStructure(
            @Parameter(description = "薪资结构ID") @PathVariable String id,
            @Valid @RequestBody SalaryStructureCreateDTO dto) {
        SalaryStructure structure = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在"));

        structure.setStructureName(dto.getStructureName());
        structure.setSalaryType(dto.getSalaryType());
        structure.setItemName(dto.getItemName());
        structure.setItemCode(dto.getItemCode());
        structure.setDefaultAmount(dto.getDefaultAmount());
        structure.setMinAmount(dto.getMinAmount());
        structure.setMaxAmount(dto.getMaxAmount());
        structure.setCalcFormula(dto.getCalcFormula());
        structure.setTaxable(dto.getTaxable());
        structure.setInsuranceable(dto.getInsuranceable());
        structure.setSortOrder(dto.getSortOrder());
        structure.setStatus(dto.getStatus());
        structure.setDescription(dto.getDescription());
        structure.setRemark(dto.getRemark());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return Result.success(convertToVO(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除薪资结构", description = "删除薪资结构(逻辑删除)")
    public Result<Void> deleteStructure(@Parameter(description = "薪资结构ID") @PathVariable String id) {
        SalaryStructure structure = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在"));
        structure.setDeleted(true);
        salaryStructureRepository.save(structure);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取薪资结构详情", description = "根据ID获取薪资结构详细信息")
    public Result<SalaryStructureVO> getStructure(@Parameter(description = "薪资结构ID") @PathVariable String id) {
        SalaryStructure structure = salaryStructureRepository.findById(id)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() -> new RuntimeException("薪资结构不存在"));
        return Result.success(convertToVO(structure));
    }

    @GetMapping("/code/{structureCode}")
    @Operation(summary = "根据编码获取薪资结构", description = "根据结构编码获取薪资结构")
    public Result<SalaryStructureVO> getStructureByCode(
            @Parameter(description = "结构编码") @PathVariable String structureCode) {
        SalaryStructure structure = salaryStructureRepository.findByStructureCode(structureCode)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在"));
        return Result.success(convertToVO(structure));
    }

    @GetMapping
    @Operation(summary = "分页查询薪资结构", description = "分页查询薪资结构列表")
    public Result<PageResult<SalaryStructureVO>> listStructures(
            @Parameter(description = "薪资类型") @RequestParam(required = false) String salaryType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("sortOrder").ascending());

        Page<SalaryStructure> page = salaryStructureRepository.findByConditions(salaryType, status, keyword, pageable);

        List<SalaryStructureVO> voList = page.getContent().stream()
                .filter(s -> !s.getDeleted())
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<SalaryStructureVO> result = PageResult.of(voList, page.getTotalElements(), pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/type/{salaryType}")
    @Operation(summary = "根据类型查询薪资结构", description = "查询指定类型的薪资结构列表")
    public Result<List<SalaryStructureVO>> listStructuresByType(
            @Parameter(description = "薪资类型") @PathVariable String salaryType) {
        List<SalaryStructure> structures = salaryStructureRepository.findBySalaryTypeAndDeletedFalseOrderBySortOrderAsc(salaryType);
        List<SalaryStructureVO> voList = structures.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/active")
    @Operation(summary = "获取启用的薪资结构", description = "查询所有启用的薪资结构列表")
    public Result<List<SalaryStructureVO>> listActiveStructures() {
        List<SalaryStructure> structures = salaryStructureRepository.findAllEnabled();
        List<SalaryStructureVO> voList = structures.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private SalaryStructureVO convertToVO(SalaryStructure structure) {
        SalaryStructureVO vo = new SalaryStructureVO();
        vo.setId(structure.getId());
        vo.setStructureName(structure.getStructureName());
        vo.setStructureCode(structure.getStructureCode());
        vo.setSalaryType(structure.getSalaryType());
        vo.setItemName(structure.getItemName());
        vo.setItemCode(structure.getItemCode());
        vo.setDefaultAmount(structure.getDefaultAmount());
        vo.setMinAmount(structure.getMinAmount());
        vo.setMaxAmount(structure.getMaxAmount());
        vo.setCalcFormula(structure.getCalcFormula());
        vo.setTaxable(structure.getTaxable());
        vo.setInsuranceable(structure.getInsuranceable());
        vo.setSortOrder(structure.getSortOrder());
        vo.setStatus(structure.getStatus());
        vo.setDescription(structure.getDescription());
        vo.setRemark(structure.getRemark());
        vo.setCreateTime(structure.getCreateTime());
        vo.setUpdateTime(structure.getUpdateTime());
        return vo;
    }
}