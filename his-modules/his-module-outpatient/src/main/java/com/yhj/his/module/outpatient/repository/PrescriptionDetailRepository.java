package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 处方明细Repository
 */
@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, String>, JpaSpecificationExecutor<PrescriptionDetail> {

    /**
     * 根据处方ID查询明细列表
     */
    List<PrescriptionDetail> findByPrescriptionId(String prescriptionId);

    /**
     * 根据药品ID查询明细列表
     */
    List<PrescriptionDetail> findByDrugId(String drugId);

    /**
     * 根据处方ID删除所有明细
     */
    void deleteByPrescriptionId(String prescriptionId);
}