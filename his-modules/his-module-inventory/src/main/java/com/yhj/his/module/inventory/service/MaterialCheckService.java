package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialCheck;
import com.yhj.his.module.inventory.entity.MaterialCheckItem;
import com.yhj.his.module.inventory.enums.CheckStatus;
import com.yhj.his.module.inventory.enums.CheckType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 库存盘点Service接口
 */
public interface MaterialCheckService {

    /**
     * 根据ID查询盘点记录
     */
    Optional<MaterialCheck> findById(String id);

    /**
     * 根据盘点单号查询
     */
    Optional<MaterialCheck> findByCheckNo(String checkNo);

    /**
     * 查询所有盘点记录
     */
    List<MaterialCheck> findAll();

    /**
     * 分页查询盘点记录
     */
    Page<MaterialCheck> findAll(Pageable pageable);

    /**
     * 根据状态查询
     */
    List<MaterialCheck> findByStatus(CheckStatus status);

    /**
     * 根据盘点类型查询
     */
    List<MaterialCheck> findByCheckType(CheckType checkType);

    /**
     * 根据库房ID查询
     */
    List<MaterialCheck> findByWarehouseId(String warehouseId);

    /**
     * 根据盘点日期查询
     */
    List<MaterialCheck> findByCheckDate(LocalDate checkDate);

    /**
     * 根据状态分页查询
     */
    Page<MaterialCheck> findByStatus(CheckStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    Page<MaterialCheck> search(String warehouseId, CheckStatus status, CheckType checkType,
                               LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询进行中的盘点
     */
    List<MaterialCheck> findInProgressChecks();

    /**
     * 查询待调整的盘点
     */
    List<MaterialCheck> findCompletedChecks();

    /**
     * 创建盘点计划
     */
    MaterialCheck create(MaterialCheck materialCheck);

    /**
     * 创建盘点计划（含明细）
     */
    MaterialCheck createWithItems(MaterialCheck materialCheck, List<MaterialCheckItem> items);

    /**
     * 更新盘点记录
     */
    MaterialCheck update(String id, MaterialCheck materialCheck);

    /**
     * 删除盘点记录
     */
    void delete(String id);

    /**
     * 批量删除盘点记录
     */
    void deleteBatch(List<String> ids);

    /**
     * 开始盘点
     */
    MaterialCheck startCheck(String id, String checkerId, String checkerName);

    /**
     * 输入盘点数据
     */
    MaterialCheckItem inputCheckData(String itemId, BigDecimal actualQuantity, String remark);

    /**
     * 完成盘点
     */
    MaterialCheck completeCheck(String id);

    /**
     * 调整库存
     */
    MaterialCheck adjust(String id, String adjusterId, String adjusterName);

    /**
     * 取消盘点
     */
    MaterialCheck cancel(String id);

    /**
     * 生成盘点单号
     */
    String generateCheckNo();

    /**
     * 计算盘点差异
     */
    void calculateDiff(String id);

    /**
     * 添加盘点明细
     */
    MaterialCheckItem addItem(String checkId, MaterialCheckItem item);

    /**
     * 删除盘点明细
     */
    void deleteItem(String itemId);

    /**
     * 获取盘点明细列表
     */
    List<MaterialCheckItem> getItems(String checkId);

    /**
     * 获取有差异的盘点明细
     */
    List<MaterialCheckItem> getDiffItems(String checkId);

    /**
     * 获取未调整的盘点明细
     */
    List<MaterialCheckItem> getUnadjustedDiffItems(String checkId);

    /**
     * 根据库房初始化盘点明细
     */
    List<MaterialCheckItem> initializeItemsByWarehouse(String warehouseId);
}