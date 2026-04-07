package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialInbound;
import com.yhj.his.module.inventory.entity.MaterialInboundItem;
import com.yhj.his.module.inventory.enums.InboundStatus;
import com.yhj.his.module.inventory.enums.InboundType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 入库记录Service接口
 */
public interface MaterialInboundService {

    /**
     * 根据ID查询入库记录
     */
    Optional<MaterialInbound> findById(String id);

    /**
     * 根据入库单号查询
     */
    Optional<MaterialInbound> findByInboundNo(String inboundNo);

    /**
     * 查询所有入库记录
     */
    List<MaterialInbound> findAll();

    /**
     * 分页查询入库记录
     */
    Page<MaterialInbound> findAll(Pageable pageable);

    /**
     * 根据状态查询
     */
    List<MaterialInbound> findByStatus(InboundStatus status);

    /**
     * 根据入库类型查询
     */
    List<MaterialInbound> findByInboundType(InboundType inboundType);

    /**
     * 根据库房ID查询
     */
    List<MaterialInbound> findByWarehouseId(String warehouseId);

    /**
     * 根据入库日期查询
     */
    List<MaterialInbound> findByInboundDate(LocalDate inboundDate);

    /**
     * 根据状态分页查询
     */
    Page<MaterialInbound> findByStatus(InboundStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    Page<MaterialInbound> search(String warehouseId, InboundStatus status, InboundType inboundType,
                                 LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询待审核入库单
     */
    List<MaterialInbound> findPendingInbounds();

    /**
     * 创建入库申请
     */
    MaterialInbound create(MaterialInbound materialInbound);

    /**
     * 创建入库申请（含明细）
     */
    MaterialInbound createWithItems(MaterialInbound materialInbound, List<MaterialInboundItem> items);

    /**
     * 更新入库记录
     */
    MaterialInbound update(String id, MaterialInbound materialInbound);

    /**
     * 删除入库记录
     */
    void delete(String id);

    /**
     * 批量删除入库记录
     */
    void deleteBatch(List<String> ids);

    /**
     * 提交入库申请
     */
    MaterialInbound submit(String id);

    /**
     * 审核入库申请
     */
    MaterialInbound audit(String id, String auditorId, String auditorName, boolean approved, String auditRemark);

    /**
     * 确认入库
     */
    MaterialInbound confirm(String id, String operatorId, String operatorName);

    /**
     * 取消入库申请
     */
    MaterialInbound cancel(String id);

    /**
     * 生成入库单号
     */
    String generateInboundNo();

    /**
     * 计算入库总数量和总金额
     */
    void calculateTotal(String id);

    /**
     * 添加入库明细
     */
    MaterialInboundItem addItem(String inboundId, MaterialInboundItem item);

    /**
     * 删除入库明细
     */
    void deleteItem(String itemId);

    /**
     * 获取入库明细列表
     */
    List<MaterialInboundItem> getItems(String inboundId);
}