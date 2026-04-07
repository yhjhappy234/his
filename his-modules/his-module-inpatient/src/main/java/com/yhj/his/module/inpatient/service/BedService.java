package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.vo.BedVO;
import com.yhj.his.module.inpatient.vo.WardBedStatisticsVO;

import java.util.List;

/**
 * 床位管理服务接口
 */
public interface BedService {

    /**
     * 查询床位列表
     *
     * @param queryDTO 查询条件
     * @return 床位列表
     */
    List<BedVO> list(BedQueryDTO queryDTO);

    /**
     * 查询病区床位统计
     *
     * @param wardId 病区ID
     * @return 床位统计
     */
    WardBedStatisticsVO getStatistics(String wardId);

    /**
     * 分配床位
     *
     * @param dto 床位分配请求
     * @return 分配是否成功
     */
    boolean assign(BedAssignDTO dto);

    /**
     * 调换床位
     *
     * @param dto 床位调换请求
     * @return 调换是否成功
     */
    boolean change(BedChangeDTO dto);

    /**
     * 更新床位状态
     *
     * @param dto 床位状态变更请求
     * @return 更新是否成功
     */
    boolean updateStatus(BedStatusUpdateDTO dto);

    /**
     * 查询床位详情
     *
     * @param bedId 床位ID
     * @return 床位详情
     */
    BedVO getById(String bedId);

    /**
     * 分页查询床位
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<BedVO> page(Integer pageNum, Integer pageSize, BedQueryDTO queryDTO);

    /**
     * 释放床位
     *
     * @param admissionId 住院ID
     * @return 释放是否成功
     */
    boolean release(String admissionId);
}