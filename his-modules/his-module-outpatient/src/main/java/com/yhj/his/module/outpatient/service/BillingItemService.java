package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.entity.BillingItem;
import com.yhj.his.module.outpatient.vo.PendingBillingVO;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 收费项目服务接口
 */
public interface BillingItemService {

    /**
     * 创建收费项目
     */
    BillingItem createBillingItem(BillingItem item);

    /**
     * 批量创建收费项目
     */
    List<BillingItem> createBillingItems(List<BillingItem> items);

    /**
     * 根据ID查询收费项目
     */
    Optional<BillingItem> findById(String id);

    /**
     * 获取收费项目详情
     */
    BillingItem getBillingItemDetail(String id);

    /**
     * 分页查询收费项目列表
     */
    PageResult<BillingItem> listBillingItems(String patientId, String registrationId, String payStatus, Pageable pageable);

    /**
     * 查询挂号关联收费项目
     */
    List<BillingItem> listBillingItemsByRegistration(String registrationId);

    /**
     * 查询待收费项目
     */
    List<BillingItem> listUnpaidItems(String registrationId);

    /**
     * 获取待收费汇总
     */
    PendingBillingVO getPendingBilling(String registrationId);

    /**
     * 计算待收费总金额
     */
    BigDecimal calculateTotalAmount(String registrationId);

    /**
     * 更新收费状态
     */
    BillingItem updatePayStatus(String id, String payStatus);

    /**
     * 批量更新收费状态
     */
    void batchUpdatePayStatus(List<String> ids, String payStatus);

    /**
     * 删除收费项目
     */
    void deleteBillingItem(String id);

    /**
     * 更新收费项目
     */
    BillingItem updateBillingItem(String id, BillingItem item);
}