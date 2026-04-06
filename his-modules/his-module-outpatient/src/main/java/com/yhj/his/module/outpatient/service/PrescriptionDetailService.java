package com.yhj.his.module.outpatient.service;

import com.yhj.his.module.outpatient.entity.PrescriptionDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 处方明细服务接口
 */
public interface PrescriptionDetailService {

    /**
     * 保存处方明细
     */
    PrescriptionDetail save(PrescriptionDetail detail);

    /**
     * 批量保存处方明细
     */
    List<PrescriptionDetail> saveAll(List<PrescriptionDetail> details);

    /**
     * 根据ID查询处方明细
     */
    Optional<PrescriptionDetail> findById(String id);

    /**
     * 查询所有处方明细
     */
    List<PrescriptionDetail> findAll();

    /**
     * 分页查询处方明细
     */
    Page<PrescriptionDetail> findAll(Pageable pageable);

    /**
     * 根据处方ID查询明细列表
     */
    List<PrescriptionDetail> findByPrescriptionId(String prescriptionId);

    /**
     * 根据药品ID查询明细列表
     */
    List<PrescriptionDetail> findByDrugId(String drugId);

    /**
     * 更新处方明细
     */
    PrescriptionDetail update(PrescriptionDetail detail);

    /**
     * 删除处方明细
     */
    void deleteById(String id);

    /**
     * 根据处方ID删除所有明细
     */
    void deleteByPrescriptionId(String prescriptionId);

    /**
     * 计算明细金额
     */
    BigDecimal calculateAmount(PrescriptionDetail detail);

    /**
     * 批量更新明细金额
     */
    void updateAmounts(List<PrescriptionDetail> details);

    /**
     * 统计处方药品数量
     */
    long countByPrescriptionId(String prescriptionId);

    /**
     * 检查明细是否存在
     */
    boolean existsById(String id);

    /**
     * 根据处方ID和药品ID查询明细
     */
    Optional<PrescriptionDetail> findByPrescriptionIdAndDrugId(String prescriptionId, String drugId);
}