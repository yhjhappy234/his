package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.OrderExecution;
import com.yhj.his.module.inpatient.enums.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 医嘱执行记录Repository
 */
@Repository
public interface OrderExecutionRepository extends JpaRepository<OrderExecution, String> {

    /**
     * 根据医嘱ID查询执行记录
     */
    List<OrderExecution> findByOrderId(String orderId);

    /**
     * 根据住院ID查询执行记录
     */
    List<OrderExecution> findByAdmissionId(String admissionId);

    /**
     * 根据医嘱ID和状态查询执行记录
     */
    List<OrderExecution> findByOrderIdAndStatus(String orderId, ExecutionStatus status);

    /**
     * 根据执行时间范围查询
     */
    @Query("SELECT e FROM OrderExecution e WHERE e.admissionId = :admissionId AND e.executeTime BETWEEN :startTime AND :endTime ORDER BY e.executeTime")
    List<OrderExecution> findByAdmissionIdAndExecuteTimeBetween(
            @Param("admissionId") String admissionId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计医嘱执行次数
     */
    @Query("SELECT COUNT(e) FROM OrderExecution e WHERE e.orderId = :orderId AND e.status = 'EXECUTED'")
    Long countExecutedByOrderId(@Param("orderId") String orderId);

    /**
     * 查询最后一次执行记录
     */
    @Query("SELECT e FROM OrderExecution e WHERE e.orderId = :orderId ORDER BY e.executeTime DESC LIMIT 1")
    OrderExecution findLastExecution(@Param("orderId") String orderId);
}