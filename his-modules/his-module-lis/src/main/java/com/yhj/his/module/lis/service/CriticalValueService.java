package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.CriticalValueConfirmDTO;
import com.yhj.his.module.lis.dto.CriticalValueHandleDTO;
import com.yhj.his.module.lis.dto.CriticalValueNotifyDTO;
import com.yhj.his.module.lis.entity.CriticalValue;
import com.yhj.his.module.lis.enums.CriticalValueStatus;
import com.yhj.his.module.lis.vo.CriticalValueVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 危急值服务接口
 */
public interface CriticalValueService {

    /**
     * 创建危急值记录
     */
    CriticalValueVO create(String requestId, String sampleId, String resultId, String patientId,
                           String patientName, String itemId, String itemName, String testValue,
                           String criticalLevel, String criticalRange, String detecterId, String detecterName);

    /**
     * 通知危急值
     */
    CriticalValueVO notify(CriticalValueNotifyDTO dto);

    /**
     * 确认危急值
     */
    CriticalValueVO confirm(CriticalValueConfirmDTO dto);

    /**
     * 处理危急值
     */
    CriticalValueVO handle(CriticalValueHandleDTO dto);

    /**
     * 关闭危急值
     */
    CriticalValueVO close(String id);

    /**
     * 根据ID获取危急值
     */
    CriticalValueVO getById(String id);

    /**
     * 根据结果ID获取危急值
     */
    CriticalValueVO getByResultId(String resultId);

    /**
     * 分页查询危急值
     */
    PageResult<CriticalValueVO> list(Pageable pageable);

    /**
     * 根据申请ID查询危急值
     */
    List<CriticalValueVO> listByRequestId(String requestId);

    /**
     * 根据患者ID查询危急值
     */
    List<CriticalValueVO> listByPatientId(String patientId);

    /**
     * 根据状态查询危急值
     */
    List<CriticalValueVO> listByStatus(CriticalValueStatus status);

    /**
     * 查询待处理危急值
     */
    List<CriticalValueVO> listPendingCriticalValues();

    /**
     * 查询未确认危急值
     */
    List<CriticalValueVO> listNotifiedCriticalValues();

    /**
     * 根据发现时间范围查询
     */
    PageResult<CriticalValueVO> listByDetectTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 删除危急值
     */
    void delete(String id);

    /**
     * 统计某状态的危急值数量
     */
    long countByStatus(CriticalValueStatus status);

    /**
     * 统计患者未处理危急值数量
     */
    long countPendingByPatientId(String patientId);
}