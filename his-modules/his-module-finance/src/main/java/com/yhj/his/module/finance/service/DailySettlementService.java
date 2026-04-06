package com.yhj.his.module.finance.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.finance.vo.DailySettlementVO;

import java.time.LocalDate;

/**
 * 日结服务接口
 */
public interface DailySettlementService {

    /**
     * 执行收款日结
     *
     * @param operatorId 收款员ID
     * @param operatorName 收款员姓名
     * @param settlementDate 日结日期
     * @return 日结记录
     */
    DailySettlementVO performDailySettlement(String operatorId, String operatorName, LocalDate settlementDate);

    /**
     * 确认日结
     *
     * @param settlementNo 日结单号
     * @param confirmerId 确认人ID
     * @param confirmerName 确认人姓名
     * @return 日结记录
     */
    DailySettlementVO confirmSettlement(String settlementNo, String confirmerId, String confirmerName);

    /**
     * 根据日结单号查询
     */
    DailySettlementVO getBySettlementNo(String settlementNo);

    /**
     * 根据日期和操作员查询
     */
    DailySettlementVO getByDateAndOperator(LocalDate settlementDate, String operatorId);

    /**
     * 根据日期查询日结记录列表
     */
    java.util.List<DailySettlementVO> listByDate(LocalDate settlementDate);

    /**
     * 分页查询日结记录
     */
    PageResult<DailySettlementVO> pageList(String operatorId, LocalDate startDate, LocalDate endDate, String status, int pageNum, int pageSize);

    /**
     * 检查指定日期是否已日结
     */
    boolean isSettled(LocalDate settlementDate, String operatorId);

    /**
     * 获取日结报表数据
     *
     * @param settlementDate 日结日期
     * @param operatorId 操作员ID
     * @return 日结报表数据
     */
    DailySettlementVO getDailyReport(LocalDate settlementDate, String operatorId);
}