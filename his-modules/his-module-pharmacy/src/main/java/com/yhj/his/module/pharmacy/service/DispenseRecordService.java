package com.yhj.his.module.pharmacy.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.pharmacy.dto.DispenseConfirmDTO;
import com.yhj.his.module.pharmacy.dto.DispenseQueryDTO;
import com.yhj.his.module.pharmacy.dto.PrescriptionAuditDTO;
import com.yhj.his.module.pharmacy.enums.AuditStatus;
import com.yhj.his.module.pharmacy.enums.DispenseStatus;
import com.yhj.his.module.pharmacy.vo.DispenseRecordVO;

import java.util.List;

/**
 * 发药记录服务接口
 */
public interface DispenseRecordService {

    /**
     * 根据ID查询发药记录
     */
    Result<DispenseRecordVO> getDispenseRecordById(String dispenseId);

    /**
     * 根据发药单号查询
     */
    Result<DispenseRecordVO> getDispenseRecordByNo(String dispenseNo);

    /**
     * 分页查询发药记录列表
     */
    Result<PageResult<DispenseRecordVO>> queryDispenseRecords(DispenseQueryDTO query);

    /**
     * 查询处方待发药记录
     */
    Result<DispenseRecordVO> getPendingDispenseByPrescription(String prescriptionId);

    /**
     * 审核处方
     */
    Result<DispenseRecordVO> auditPrescription(String dispenseId, PrescriptionAuditDTO dto);

    /**
     * 确认发药
     */
    Result<DispenseRecordVO> confirmDispense(DispenseConfirmDTO dto);

    /**
     * 取消发药
     */
    Result<Void> cancelDispense(String dispenseId, String reason);

    /**
     * 退药处理
     */
    Result<DispenseRecordVO> processDrugReturn(String dispenseId, String reason, String operatorId);

    /**
     * 查询待审核发药记录
     */
    Result<List<DispenseRecordVO>> getPendingAuditRecords(String pharmacyId);

    /**
     * 查询待发药记录
     */
    Result<List<DispenseRecordVO>> getPendingDispenseRecords(String pharmacyId);

    /**
     * 查询患者发药记录
     */
    Result<List<DispenseRecordVO>> getPatientDispenseRecords(String patientId);

    /**
     * 更新审核状态
     */
    Result<Void> updateAuditStatus(String dispenseId, AuditStatus status);

    /**
     * 更新发药状态
     */
    Result<Void> updateDispenseStatus(String dispenseId, DispenseStatus status);

    /**
     * 患者确认接收
     */
    Result<Void> confirmReceive(String dispenseId);
}