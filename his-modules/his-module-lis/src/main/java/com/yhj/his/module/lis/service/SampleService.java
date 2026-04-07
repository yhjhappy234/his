package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.SampleCollectDTO;
import com.yhj.his.module.lis.dto.SampleReceiveDTO;
import com.yhj.his.module.lis.dto.SampleRejectDTO;
import com.yhj.his.module.lis.entity.Sample;
import com.yhj.his.module.lis.enums.SampleStatus;
import com.yhj.his.module.lis.vo.SampleVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 样本服务接口
 */
public interface SampleService {

    /**
     * 采集样本
     */
    SampleVO collect(SampleCollectDTO dto);

    /**
     * 核收样本
     */
    SampleVO receive(SampleReceiveDTO dto);

    /**
     * 拒收样本
     */
    SampleVO reject(SampleRejectDTO dto);

    /**
     * 根据ID获取样本
     */
    SampleVO getById(String id);

    /**
     * 根据样本编号获取样本
     */
    SampleVO getBySampleNo(String sampleNo);

    /**
     * 分页查询样本
     */
    PageResult<SampleVO> list(Pageable pageable);

    /**
     * 根据申请ID查询样本
     */
    List<SampleVO> listByRequestId(String requestId);

    /**
     * 根据患者ID查询样本
     */
    List<SampleVO> listByPatientId(String patientId);

    /**
     * 根据状态查询样本
     */
    List<SampleVO> listByStatus(SampleStatus status);

    /**
     * 查询待采集样本
     */
    List<SampleVO> listPendingSamples();

    /**
     * 查询待核收样本
     */
    List<SampleVO> listCollectedSamples();

    /**
     * 查询急诊样本
     */
    List<SampleVO> listEmergencySamples();

    /**
     * 根据采集时间范围查询
     */
    PageResult<SampleVO> listByCollectionTime(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 更新样本状态
     */
    SampleVO updateStatus(String id, SampleStatus status);

    /**
     * 删除样本
     */
    void delete(String id);

    /**
     * 统计某状态的样本数量
     */
    long countByStatus(SampleStatus status);

    /**
     * 生成样本标签
     */
    String generateLabel(String sampleId);
}