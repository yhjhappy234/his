package com.yhj.his.module.inventory.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.inventory.dto.QueryDTO;
import com.yhj.his.module.inventory.vo.MaterialInventoryVO;
import com.yhj.his.module.inventory.vo.MaterialAlertVO;

import java.util.List;

/**
 * 库存预警服务接口
 */
public interface AlertService {

    /**
     * 查询库存下限预警
     */
    List<MaterialAlertVO> getLowStockAlerts();

    /**
     * 查询效期预警
     */
    List<MaterialAlertVO> getExpiryAlerts(int days);

    /**
     * 分页查询预警
     */
    PageResult<MaterialAlertVO> list(Integer pageNum, Integer pageSize);

    /**
     * 条件查询预警
     */
    PageResult<MaterialAlertVO> query(QueryDTO query);

    /**
     * 处理预警
     */
    MaterialAlertVO handle(String alertId, String handlerId, String handlerName, String remark);

    /**
     * 批量处理预警
     */
    void batchHandle(List<String> alertIds, String handlerId, String handlerName, String remark);

    /**
     * 执行预警检查
     */
    void checkAndGenerateAlerts();

    /**
     * 根据物资ID查询预警
     */
    List<MaterialAlertVO> getByMaterialId(String materialId);
}