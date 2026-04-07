package com.yhj.his.module.inventory.service;

import com.yhj.his.module.inventory.entity.MaterialRequisition;
import com.yhj.his.module.inventory.entity.MaterialRequisitionItem;
import com.yhj.his.module.inventory.enums.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 物资申领Service接口
 */
public interface MaterialRequisitionService {

    /**
     * 根据ID查询申领记录
     */
    Optional<MaterialRequisition> findById(String id);

    /**
     * 根据申领单号查询
     */
    Optional<MaterialRequisition> findByRequisitionNo(String requisitionNo);

    /**
     * 查询所有申领记录
     */
    List<MaterialRequisition> findAll();

    /**
     * 分页查询申领记录
     */
    Page<MaterialRequisition> findAll(Pageable pageable);

    /**
     * 根据状态查询
     */
    List<MaterialRequisition> findByStatus(RequisitionStatus status);

    /**
     * 根据库房ID查询
     */
    List<MaterialRequisition> findByWarehouseId(String warehouseId);

    /**
     * 根据科室ID查询
     */
    List<MaterialRequisition> findByDeptId(String deptId);

    /**
     * 根据申领日期查询
     */
    List<MaterialRequisition> findByRequisitionDate(LocalDate requisitionDate);

    /**
     * 根据状态分页查询
     */
    Page<MaterialRequisition> findByStatus(RequisitionStatus status, Pageable pageable);

    /**
     * 条件查询
     */
    Page<MaterialRequisition> search(String warehouseId, String deptId, RequisitionStatus status,
                                      LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 查询待审批申领单
     */
    List<MaterialRequisition> findPendingRequisitions();

    /**
     * 查询某科室的申领记录（按日期倒序）
     */
    List<MaterialRequisition> findByDeptIdOrderByDateDesc(String deptId);

    /**
     * 创建申领申请
     */
    MaterialRequisition create(MaterialRequisition materialRequisition);

    /**
     * 创建申领申请（含明细）
     */
    MaterialRequisition createWithItems(MaterialRequisition materialRequisition, List<MaterialRequisitionItem> items);

    /**
     * 更新申领记录
     */
    MaterialRequisition update(String id, MaterialRequisition materialRequisition);

    /**
     * 删除申领记录
     */
    void delete(String id);

    /**
     * 批量删除申领记录
     */
    void deleteBatch(List<String> ids);

    /**
     * 提交申领申请
     */
    MaterialRequisition submit(String id);

    /**
     * 审批申领申请
     */
    MaterialRequisition approve(String id, String approverId, String approverName, boolean approved, String approveRemark);

    /**
     * 发放物资
     */
    MaterialRequisition issue(String id, String issuerId, String issuerName);

    /**
     * 接收物资
     */
    MaterialRequisition receive(String id, String receiverId, String receiverName);

    /**
     * 取消申领申请
     */
    MaterialRequisition cancel(String id);

    /**
     * 生成申领单号
     */
    String generateRequisitionNo();

    /**
     * 计算申领总数量和总金额
     */
    void calculateTotal(String id);

    /**
     * 添加申领明细
     */
    MaterialRequisitionItem addItem(String requisitionId, MaterialRequisitionItem item);

    /**
     * 删除申领明细
     */
    void deleteItem(String itemId);

    /**
     * 获取申领明细列表
     */
    List<MaterialRequisitionItem> getItems(String requisitionId);

    /**
     * 检查库存是否充足
     */
    boolean checkStockAvailable(String requisitionId);
}