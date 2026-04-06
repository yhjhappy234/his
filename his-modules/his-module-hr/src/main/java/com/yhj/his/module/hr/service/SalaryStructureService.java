package com.yhj.his.module.hr.service;

import com.yhj.his.module.hr.entity.SalaryStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 薪资结构服务接口
 */
public interface SalaryStructureService {

    /**
     * 创建薪资结构
     */
    SalaryStructure createSalaryStructure(SalaryStructure salaryStructure);

    /**
     * 更新薪资结构
     */
    SalaryStructure updateSalaryStructure(SalaryStructure salaryStructure);

    /**
     * 根据ID删除薪资结构（逻辑删除）
     */
    void deleteSalaryStructure(String id);

    /**
     * 根据ID获取薪资结构
     */
    Optional<SalaryStructure> getSalaryStructureById(String id);

    /**
     * 根据结构编码获取薪资结构
     */
    Optional<SalaryStructure> getSalaryStructureByCode(String structureCode);

    /**
     * 根据薪资类型获取薪资结构列表
     */
    List<SalaryStructure> getSalaryStructuresByType(String salaryType);

    /**
     * 根据项目编码获取薪资结构
     */
    Optional<SalaryStructure> getSalaryStructureByItemCode(String itemCode);

    /**
     * 根据状态获取薪资结构列表
     */
    List<SalaryStructure> getSalaryStructuresByStatus(String status);

    /**
     * 分页查询薪资结构
     */
    Page<SalaryStructure> searchSalaryStructures(String salaryType, String status,
                                                   String keyword, Pageable pageable);

    /**
     * 获取所有启用的薪资结构
     */
    List<SalaryStructure> getAllEnabledSalaryStructures();

    /**
     * 检查结构编码是否存在
     */
    boolean existsByStructureCode(String structureCode);

    /**
     * 检查项目编码是否存在
     */
    boolean existsByItemCode(String itemCode);

    /**
     * 启用薪资结构
     */
    SalaryStructure enableSalaryStructure(String salaryStructureId);

    /**
     * 禁用薪资结构
     */
    SalaryStructure disableSalaryStructure(String salaryStructureId);

    /**
     * 批量创建薪资结构
     */
    List<SalaryStructure> batchCreateSalaryStructures(List<SalaryStructure> salaryStructures);

    /**
     * 根据公式计算金额
     */
    BigDecimal calculateByFormula(String formula, BigDecimal baseValue);

    /**
     * 生成结构编码
     */
    String generateStructureCode(String salaryType);

    /**
     * 获取薪资结构的默认金额范围
     */
    BigDecimal[] getAmountRange(String salaryType);
}