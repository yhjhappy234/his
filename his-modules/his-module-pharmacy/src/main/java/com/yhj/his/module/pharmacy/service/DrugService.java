package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DrugCreateDTO;
import com.yhj.his.module.pharmacy.dto.DrugQueryDTO;
import com.yhj.his.module.pharmacy.dto.DrugUpdateDTO;
import com.yhj.his.module.pharmacy.vo.DrugVO;

import java.util.List;

/**
 * 药品服务接口
 */
public interface DrugService {

    /**
     * 创建药品
     */
    Result<DrugVO> createDrug(DrugCreateDTO dto);

    /**
     * 更新药品
     */
    Result<DrugVO> updateDrug(String drugId, DrugUpdateDTO dto);

    /**
     * 删除药品
     */
    Result<Void> deleteDrug(String drugId);

    /**
     * 根据ID查询药品
     */
    Result<DrugVO> getDrugById(String drugId);

    /**
     * 根据编码查询药品
     */
    Result<DrugVO> getDrugByCode(String drugCode);

    /**
     * 分页查询药品列表
     */
    Result<PageResult<DrugVO>> queryDrugs(DrugQueryDTO dto);

    /**
     * 获取药品分类列表
     */
    Result<List<String>> getDrugCategories();

    /**
     * 根据分类查询药品
     */
    Result<List<DrugVO>> getDrugsByCategory(String category);

    /**
     * 搜索药品
     */
    Result<List<DrugVO>> searchDrugs(String keyword);

    /**
     * 获取处方药品列表
     */
    Result<List<DrugVO>> getPrescriptionDrugs();

    /**
     * 获取OTC药品列表
     */
    Result<List<DrugVO>> getOtcDrugs();

    /**
     * 获取医保药品列表
     */
    Result<List<DrugVO>> getInsuranceDrugs();

    /**
     * 切换药品状态
     */
    Result<Void> toggleDrugStatus(String drugId, String status);
}