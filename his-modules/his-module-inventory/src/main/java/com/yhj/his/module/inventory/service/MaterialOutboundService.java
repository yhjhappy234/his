package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialOutbound;
import com.yhj.his.module.inventory.entity.MaterialOutboundItem;
import com.yhj.his.module.inventory.enums.OutboundStatus;
import com.yhj.his.module.inventory.enums.OutboundType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 出库记录Service接口
 */
public interface MaterialOutboundService {

    /**
     * 根据ID查询出库记录
     */
    Optional<MaterialOutbound> findById(String id);

    /**
     * 根据出库单号查询
     */
    Optional<MaterialOutbound> findByOutboundNo(String outboundNo);

    /**
     * 查询所有出库记录
     */
    List<MaterialOutbound> findAll();

    /**
     * 分页查询出库记录
     */
    Page<MaterialOutbound> findAll(Pageable pageable);

    /**
     * 根据状态查询
     */
    List<MaterialOutbound> findByStatus(OutboundStatus status);

    /**
     * 根据出库类型查询
     */
    List<MaterialOutbound> findByOutboundType(OutboundType outboundType);

    /**
     * 根据库房ID查询
     */
    List<MaterialOutbound> findByWarehouseId(String warehouseId);

    /**
     * 根据目标科室ID查询
     */
    List<MaterialOutbound> findByTargetDeptId(String targetDeptId);

    /**
     * 根据出库日期查询
     */
    List<MaterialOutbound> findByOutboundDate(LocalDate outboundDate);

    /**
     * 根据状态分页查询
     */
    Page<MaterialOutbound> findByStatus(OutboundStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    Page<MaterialOutbound> search(String warehouseId, OutboundStatus status, OutboundType outboundType,
                                  String targetDeptId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询待审核出库单
     */
    List<MaterialOutbound> findPendingOutbounds();

    /**
     * 创建出库申请
     */
    MaterialOutbound create(MaterialOutbound materialOutbound);

    /**
     * 创建出库申请（含明细）
     */
    MaterialOutbound createWithItems(MaterialOutbound materialOutbound, List<MaterialOutboundItem> items);

    /**
     * 更新出库记录
     */
    MaterialOutbound update(String id, MaterialOutbound materialOutbound);

    /**
     * 删除出库记录
     */
    void delete(String id);

    /**
     * 批量删除出库记录
     */
    void deleteBatch(List<String> ids);

    /**
     * 提交出库申请
     */
    MaterialOutbound submit(String id);

    /**
     * 审核出库申请
     */
    MaterialOutbound audit(String id, String auditorId, String auditorName, boolean approved, String auditRemark);

    /**
     * 发放出库
     */
    MaterialOutbound issue(String id, String operatorId, String operatorName);

    /**
     * 确认出库
     */
    MaterialOutbound confirm(String id, String receiverId, String receiverName);

    /**
     * 取消出库申请
     */
    MaterialOutbound cancel(String id);

    /**
     * 生成出库单号
     */
    String generateOutboundNo();

    /**
     * 计算出库总数量和总金额
     */
    void calculateTotal(String id);

    /**
     * 添加出库明细
     */
    MaterialOutboundItem addItem(String outboundId, MaterialOutboundItem item);

    /**
     * 删除出库明细
     */
    void deleteItem(String itemId);

    /**
     * 获取出库明细列表
     */
    List<MaterialOutboundItem> getItems(String outboundId);

    /**
     * 检查库存是否充足
     */
    boolean checkStockAvailable(String outboundId);
}