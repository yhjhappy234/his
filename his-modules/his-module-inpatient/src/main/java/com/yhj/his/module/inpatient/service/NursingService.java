package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.vo.NursingRecordVO;

import java.util.List;

/**
 * 护理管理服务接口
 */
public interface NursingService {

    /**
     * 录入生命体征
     *
     * @param dto 生命体征录入请求
     * @return 记录ID
     */
    String recordVitalSigns(VitalSignsDTO dto);

    /**
     * 护理评估
     *
     * @param dto 护理评估请求
     * @return 评估ID
     */
    String assessment(NursingAssessmentDTO dto);

    /**
     * 护理记录
     *
     * @param dto 护理记录请求
     * @return 记录ID
     */
    String recordNursing(NursingRecordDTO dto);

    /**
     * 查询护理记录
     *
     * @param admissionId 住院ID
     * @return 护理记录列表
     */
    List<NursingRecordVO> listByAdmission(String admissionId);

    /**
     * 查询生命体征记录
     *
     * @param admissionId 住院ID
     * @return 生命体征记录列表
     */
    List<NursingRecordVO> listVitalSigns(String admissionId);

    /**
     * 查询最后一次生命体征
     *
     * @param admissionId 住院ID
     * @return 生命体征记录
     */
    NursingRecordVO getLastVitalSigns(String admissionId);

    /**
     * 分页查询护理记录
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param admissionId 住院ID
     * @return 分页结果
     */
    PageResult<NursingRecordVO> page(Integer pageNum, Integer pageSize, String admissionId);
}