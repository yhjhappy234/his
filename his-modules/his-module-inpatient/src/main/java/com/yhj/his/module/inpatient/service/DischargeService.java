package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.vo.DischargeSummaryVO;
import com.yhj.his.module.inpatient.vo.DischargeSummaryVO;

/**
 * 出院管理服务接口
 */
public interface DischargeService {

    /**
     * 出院申请
     *
     * @param dto 出院申请请求
     * @return 申请是否成功
     */
    boolean apply(DischargeApplyDTO dto);

    /**
     * 出院结算
     *
     * @param dto 出院结算请求
     * @return 结算结果
     */
    DischargeSettleResponseDTO settle(DischargeSettleDTO dto);

    /**
     * 出院小结
     *
     * @param dto 出院小结请求
     * @return 小结ID
     */
    String summary(DischargeSummaryDTO dto);

    /**
     * 查询出院小结
     *
     * @param admissionId 住院ID
     * @return 出院小结
     */
    DischargeSummaryVO getSummary(String admissionId);

    /**
     * 取消出院申请
     *
     * @param admissionId 住院ID
     * @return 取消是否成功
     */
    boolean cancelApply(String admissionId);
}