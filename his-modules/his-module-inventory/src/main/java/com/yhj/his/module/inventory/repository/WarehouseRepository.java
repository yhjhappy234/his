package com.yhj.his.module.inventory.repository;

import com.yhj.his.module.inventory.entity.Warehouse;
import com.yhj.his.module.inventory.enums.WarehouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 库房信息Repository
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    /**
     * 根据库房编码查询
     */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    /**
     * 根据库房名称查询
     */
    List<Warehouse> findByWarehouseNameAndDeletedFalse(String warehouseName);

    /**
     * 根据库房类型查询
     */
    List<Warehouse> findByWarehouseTypeAndDeletedFalse(WarehouseType warehouseType);

    /**
     * 根据科室ID查询
     */
    List<Warehouse> findByDeptIdAndDeletedFalse(String deptId);

    /**
     * 根据状态查询
     */
    List<Warehouse> findByStatusAndDeletedFalse(Integer status);

    /**
     * 根据管理员ID查询
     */
    List<Warehouse> findByManagerIdAndDeletedFalse(String managerId);

    /**
     * 检查库房编码是否存在
     */
    boolean existsByWarehouseCodeAndDeletedFalse(String warehouseCode);

    /**
     * 查询所有启用的库房
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deleted = false AND w.status = 1")
    List<Warehouse> findAllActive();

    /**
     * 分页查询未删除的库房
     */
    Page<Warehouse> findByDeletedFalse(Pageable pageable);
}