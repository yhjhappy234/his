package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 库存预警Service接口
 */
public interface MaterialAlertService {

    /**
     * 根据ID查询预警
     */
    Optional<MaterialAlert> findById(String id);

    /**
     * 查询所有预警
     */
    List<MaterialAlert> findAll();

    /**
     * 分页查询预警
     */
    Page<MaterialAlert> findAll(Pageable pageable);

    /**
     * 根据预警类型查询
     */
    List<MaterialAlert> findByAlertType(String alertType);

    /**
     * 根据状态查询
     */
    List<MaterialAlert> findByStatus(Integer status);

    /**
     * 根据物资ID查询
     */
    List<MaterialAlert> findByMaterialId(String materialId);

    /**
     * 根据库房ID查询
     */
    List<MaterialAlert> findByWarehouseId(String warehouseId);

    /**
     * 条件查询
     */
    Page<MaterialAlert> search(String alertType, Integer status, String warehouseId, String materialId, Pageable pageable);

    /**
     * 查询未处理的预警
     */
    List<MaterialAlert> findUnhandledAlerts();

    /**
     * 查询某物资未处理的预警
     */
    List<MaterialAlert> findUnhandledAlertsByMaterialId(String materialId);

    /**
     * 创建预警记录
     */
    MaterialAlert create(MaterialAlert materialAlert);

    /**
     * 更新预警记录
     */
    MaterialAlert update(String id, MaterialAlert materialAlert);

    /**
     * 删除预警记录
     */
    void delete(String id);

    /**
     * 批量删除预警记录
     */
    void deleteBatch(List<String> ids);

    /**
     * 处理预警
     */
    MaterialAlert handle(String id, String handlerId, String handlerName, String handleRemark);

    /**
     * 批量处理预警
     */
    void handleBatch(List<String> ids, String handlerId, String handlerName, String handleRemark);

    /**
     * 创建库存下限预警
     */
    MaterialAlert createLowStockAlert(String materialId, String materialCode, String materialName,
                                      String materialSpec, String materialUnit, String warehouseId,
                                      String warehouseName, BigDecimal currentQuantity, BigDecimal alertThreshold);

    /**
     * 创建库存上限预警
     */
    MaterialAlert createHighStockAlert(String materialId, String materialCode, String materialName,
                                       String materialSpec, String materialUnit, String warehouseId,
                                       String warehouseName, BigDecimal currentQuantity, BigDecimal alertThreshold);

    /**
     * 创建效期预警
     */
    MaterialAlert createExpiryAlert(String materialId, String materialCode, String materialName,
                                    String materialSpec, String materialUnit, String warehouseId,
                                    String warehouseName, String batchNo, LocalDate expiryDate);

    /**
     * 检查并生成库存预警
     */
    void checkAndGenerateAlerts();

    /**
     * 检查低库存预警
     */
    void checkLowStockAlerts();

    /**
     * 检查效期预警
     */
    void checkExpiryAlerts();

    /**
     * 获取预警统计数据
     */
    java.util.Map<String, Object> getAlertStatistics();
}