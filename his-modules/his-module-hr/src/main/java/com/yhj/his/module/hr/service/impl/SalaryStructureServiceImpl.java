package com.yhj.his.module.hr.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.hr.dto.*;
import com.yhj.his.module.hr.entity.SalaryStructure;
import com.yhj.his.module.hr.repository.SalaryStructureRepository;
import com.yhj.his.module.hr.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 薪资结构服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;

    @Override
    public SalaryStructure createSalaryStructure(SalaryStructure salaryStructure) {
        // 默认状态为启用
        salaryStructure.setStatus("启用");

        // 默认应税和计入社保基数
        if (salaryStructure.getTaxable() == null) {
            salaryStructure.setTaxable(true);
        }
        if (salaryStructure.getInsuranceable() == null) {
            salaryStructure.setInsuranceable(true);
        }

        return salaryStructureRepository.save(salaryStructure);
    }

    @Override
    public SalaryStructure updateSalaryStructure(SalaryStructure salaryStructure) {
        SalaryStructure existing = salaryStructureRepository.findById(salaryStructure.getId())
                .orElseThrow(() -> new RuntimeException("薪资结构不存在: " + salaryStructure.getId()));

        updateStructureFromEntity(salaryStructure, existing);

        return salaryStructureRepository.save(existing);
    }

    @Override
    public void deleteSalaryStructure(String id) {
        SalaryStructure structure = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在: " + id));
        structure.setDeleted(true);
        salaryStructureRepository.save(structure);
    }

    @Override
    public Optional<SalaryStructure> getSalaryStructureById(String id) {
        return salaryStructureRepository.findById(id)
                .filter(s -> !s.getDeleted());
    }

    @Override
    public Optional<SalaryStructure> getSalaryStructureByCode(String structureCode) {
        return salaryStructureRepository.findByStructureCode(structureCode)
                .filter(s -> !s.getDeleted());
    }

    @Override
    public List<SalaryStructure> getSalaryStructuresByType(String salaryType) {
        return salaryStructureRepository.findBySalaryTypeAndDeletedFalseOrderBySortOrderAsc(salaryType);
    }

    @Override
    public Optional<SalaryStructure> getSalaryStructureByItemCode(String itemCode) {
        return salaryStructureRepository.findByItemCodeAndDeletedFalse(itemCode);
    }

    @Override
    public List<SalaryStructure> getSalaryStructuresByStatus(String status) {
        return salaryStructureRepository.findByStatusAndDeletedFalseOrderBySortOrderAsc(status);
    }

    @Override
    public Page<SalaryStructure> searchSalaryStructures(String salaryType, String status,
                                                         String keyword, Pageable pageable) {
        return salaryStructureRepository.findByConditions(
                salaryType, status, keyword, pageable);
    }

    @Override
    public List<SalaryStructure> getAllEnabledSalaryStructures() {
        return salaryStructureRepository.findAllEnabled();
    }

    @Override
    public boolean existsByStructureCode(String structureCode) {
        return salaryStructureRepository.existsByStructureCode(structureCode);
    }

    @Override
    public boolean existsByItemCode(String itemCode) {
        return salaryStructureRepository.existsByItemCode(itemCode);
    }

    @Override
    public SalaryStructure enableSalaryStructure(String salaryStructureId) {
        SalaryStructure structure = salaryStructureRepository.findById(salaryStructureId)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在: " + salaryStructureId));

        structure.setStatus("启用");
        return salaryStructureRepository.save(structure);
    }

    @Override
    public SalaryStructure disableSalaryStructure(String salaryStructureId) {
        SalaryStructure structure = salaryStructureRepository.findById(salaryStructureId)
                .orElseThrow(() -> new RuntimeException("薪资结构不存在: " + salaryStructureId));

        structure.setStatus("禁用");
        return salaryStructureRepository.save(structure);
    }

    @Override
    public List<SalaryStructure> batchCreateSalaryStructures(List<SalaryStructure> salaryStructures) {
        return salaryStructureRepository.saveAll(salaryStructures);
    }

    @Override
    public BigDecimal calculateByFormula(String formula, BigDecimal baseValue) {
        // 简单的公式计算实现（实际项目中需要更复杂的公式解析）
        if (formula == null || formula.isEmpty()) {
            return baseValue;
        }

        try {
            // 支持简单的乘除运算，如 "base * 0.5" 或 "base / 12"
            if (formula.contains("*")) {
                String[] parts = formula.split("*");
                BigDecimal multiplier = new BigDecimal(parts[1].trim());
                return baseValue.multiply(multiplier);
            } else if (formula.contains("/")) {
                String[] parts = formula.split("/");
                BigDecimal divisor = new BigDecimal(parts[1].trim());
                return baseValue.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
            }
        } catch (Exception e) {
            // 公式解析失败，返回基础值
        }

        return baseValue;
    }

    @Override
    public String generateStructureCode(String salaryType) {
        return salaryType.substring(0, 2) + System.currentTimeMillis();
    }

    @Override
    public BigDecimal[] getAmountRange(String salaryType) {
        List<SalaryStructure> structures = salaryStructureRepository.findBySalaryTypeAndDeletedFalseOrderBySortOrderAsc(salaryType);

        if (structures.isEmpty()) {
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }

        BigDecimal min = structures.stream()
                .filter(s -> s.getMinAmount() != null)
                .map(SalaryStructure::getMinAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal max = structures.stream()
                .filter(s -> s.getMaxAmount() != null)
                .map(SalaryStructure::getMaxAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new BigDecimal[]{min, max};
    }

    private void updateStructureFromEntity(SalaryStructure source, SalaryStructure target) {
        if (source.getStructureName() != null) target.setStructureName(source.getStructureName());
        if (source.getSalaryType() != null) target.setSalaryType(source.getSalaryType());
        if (source.getItemName() != null) target.setItemName(source.getItemName());
        if (source.getDefaultAmount() != null) target.setDefaultAmount(source.getDefaultAmount());
        if (source.getMinAmount() != null) target.setMinAmount(source.getMinAmount());
        if (source.getMaxAmount() != null) target.setMaxAmount(source.getMaxAmount());
        if (source.getCalcFormula() != null) target.setCalcFormula(source.getCalcFormula());
        if (source.getTaxable() != null) target.setTaxable(source.getTaxable());
        if (source.getInsuranceable() != null) target.setInsuranceable(source.getInsuranceable());
        if (source.getSortOrder() != null) target.setSortOrder(source.getSortOrder());
        if (source.getStatus() != null) target.setStatus(source.getStatus());
        if (source.getDescription() != null) target.setDescription(source.getDescription());
        if (source.getRemark() != null) target.setRemark(source.getRemark());
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