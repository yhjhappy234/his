package com.yhj.his.module.inpatient.service.impl;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.common.core.util.SequenceGenerator;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.MedicalOrder;
import com.yhj.his.module.inpatient.entity.OrderExecution;
import com.yhj.his.module.inpatient.enums.ExecutionStatus;
import com.yhj.his.module.inpatient.enums.OrderStatus;
import com.yhj.his.module.inpatient.repository.MedicalOrderRepository;
import com.yhj.his.module.inpatient.repository.OrderExecutionRepository;
import com.yhj.his.module.inpatient.service.MedicalOrderService;
import com.yhj.his.module.inpatient.vo.MedicalOrderVO;
import com.yhj.his.module.inpatient.vo.OrderExecutionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医嘱管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalOrderServiceImpl implements MedicalOrderService {

    private final MedicalOrderRepository orderRepository;
    private final OrderExecutionRepository executionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(OrderCreateDTO dto) {
        MedicalOrder order = new MedicalOrder();
        order.setOrderNo(SequenceGenerator.generateWithTime("MO"));
        order.setAdmissionId(dto.getAdmissionId());
        order.setPatientId(dto.getPatientId());
        order.setOrderType(dto.getOrderType());
        order.setOrderCategory(dto.getOrderCategory());
        order.setOrderContent(dto.getOrderContent());
        order.setOrderDetail(dto.getOrderDetail());
        order.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : LocalDateTime.now());
        order.setEndTime(dto.getEndTime());
        order.setExecuteTime(dto.getExecuteTime());
        order.setFrequency(dto.getFrequency());
        order.setDoctorId(dto.getDoctorId());
        order.setDoctorName(dto.getDoctorName());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        order.setGroupNo(dto.getGroupNo());

        order = orderRepository.save(order);
        log.info("医嘱开立成功，医嘱编号：{}", order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(OrderAuditDTO dto) {
        MedicalOrder order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "医嘱不存在"));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "医嘱状态不允许审核");
        }

        if ("通过".equals(dto.getAuditResult())) {
            order.setStatus(OrderStatus.AUDITED);
        } else if ("驳回".equals(dto.getAuditResult())) {
            order.setStatus(OrderStatus.REJECTED);
        } else {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "审核结果无效");
        }

        order.setNurseId(dto.getNurseId());
        order.setNurseName(dto.getNurseName());
        order.setAuditTime(LocalDateTime.now());
        orderRepository.save(order);

        log.info("医嘱审核成功，医嘱ID：{}，审核结果：{}", dto.getOrderId(), dto.getAuditResult());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String execute(OrderExecuteDTO dto) {
        MedicalOrder order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "医嘱不存在"));

        if (order.getStatus() != OrderStatus.AUDITED && order.getStatus() != OrderStatus.EXECUTING) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "医嘱状态不允许执行");
        }

        // 创建执行记录
        OrderExecution execution = new OrderExecution();
        execution.setOrderId(dto.getOrderId());
        execution.setAdmissionId(dto.getAdmissionId());
        execution.setPatientId(dto.getPatientId());
        execution.setExecuteTime(dto.getExecuteTime());
        execution.setExecuteNurseId(dto.getExecuteNurseId());
        execution.setExecuteNurseName(dto.getExecuteNurseName());
        execution.setExecuteResult(dto.getExecuteResult());
        execution.setExecuteDetail(dto.getExecuteDetail());
        execution.setStatus(ExecutionStatus.EXECUTED);

        execution = executionRepository.save(execution);

        // 更新医嘱状态
        if (order.getStatus() == OrderStatus.AUDITED) {
            order.setStatus(OrderStatus.EXECUTING);
            orderRepository.save(order);
        }

        log.info("医嘱执行成功，医嘱ID：{}，执行时间：{}", dto.getOrderId(), dto.getExecuteTime());
        return execution.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean stop(OrderStopDTO dto) {
        MedicalOrder order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "医嘱不存在"));

        if (order.getStatus() != OrderStatus.EXECUTING && order.getStatus() != OrderStatus.AUDITED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "医嘱状态不允许停止");
        }

        order.setStatus(OrderStatus.STOPPED);
        order.setStopDoctorId(dto.getStopDoctorId());
        order.setStopDoctorName(dto.getStopDoctorName());
        order.setStopTime(LocalDateTime.now());
        order.setStopReason(dto.getStopReason());
        orderRepository.save(order);

        log.info("医嘱停止成功，医嘱ID：{}，停止原因：{}", dto.getOrderId(), dto.getStopReason());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(String orderId, String reason) {
        MedicalOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "医嘱不存在"));

        if (order.getStatus() == OrderStatus.EXECUTING || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "正在执行或已完成的医嘱不能作废");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setStopReason(reason);
        order.setStopTime(LocalDateTime.now());
        orderRepository.save(order);

        log.info("医嘱作废成功，医嘱ID：{}，作废原因：{}", orderId, reason);
        return true;
    }

    @Override
    public MedicalOrderVO getById(String orderId) {
        MedicalOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "医嘱不存在"));
        return convertToVO(order);
    }

    @Override
    public List<MedicalOrderVO> listByAdmission(String admissionId) {
        List<MedicalOrder> orders = orderRepository.findByAdmissionId(admissionId);
        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<MedicalOrderVO> listActiveOrders(String admissionId) {
        List<MedicalOrder> orders = orderRepository.findActiveLongTermOrders(admissionId);
        return orders.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<MedicalOrderVO> page(Integer pageNum, Integer pageSize, String admissionId) {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "orderTime"));

        Page<MedicalOrder> page;
        if (admissionId != null) {
            page = orderRepository.findByAdmissionId(admissionId, pageRequest);
        } else {
            page = orderRepository.findAll(pageRequest);
        }

        List<MedicalOrderVO> list = page.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public List<OrderExecutionVO> listExecutions(String orderId) {
        List<OrderExecution> executions = executionRepository.findByOrderId(orderId);
        return executions.stream().map(this::convertExecutionToVO).collect(Collectors.toList());
    }

    /**
     * 转换医嘱为VO
     */
    private MedicalOrderVO convertToVO(MedicalOrder order) {
        MedicalOrderVO vo = new MedicalOrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setAdmissionId(order.getAdmissionId());
        vo.setPatientId(order.getPatientId());
        vo.setOrderType(order.getOrderType());
        vo.setOrderCategory(order.getOrderCategory());
        vo.setOrderContent(order.getOrderContent());
        vo.setOrderDetail(order.getOrderDetail());
        vo.setStartTime(order.getStartTime());
        vo.setEndTime(order.getEndTime());
        vo.setExecuteTime(order.getExecuteTime());
        vo.setFrequency(order.getFrequency());
        vo.setDoctorName(order.getDoctorName());
        vo.setOrderTime(order.getOrderTime());
        vo.setNurseName(order.getNurseName());
        vo.setAuditTime(order.getAuditTime());
        vo.setStatus(order.getStatus());
        vo.setStopDoctorName(order.getStopDoctorName());
        vo.setStopTime(order.getStopTime());
        vo.setStopReason(order.getStopReason());
        vo.setGroupNo(order.getGroupNo());

        // 统计执行次数
        vo.setExecuteCount(executionRepository.countExecutedByOrderId(order.getId()));

        return vo;
    }

    /**
     * 转换执行记录为VO
     */
    private OrderExecutionVO convertExecutionToVO(OrderExecution execution) {
        OrderExecutionVO vo = new OrderExecutionVO();
        vo.setExecutionId(execution.getId());
        vo.setOrderId(execution.getOrderId());
        vo.setAdmissionId(execution.getAdmissionId());
        vo.setPatientId(execution.getPatientId());
        vo.setExecuteTime(execution.getExecuteTime());
        vo.setExecuteNurseName(execution.getExecuteNurseName());
        vo.setExecuteResult(execution.getExecuteResult());
        vo.setExecuteDetail(execution.getExecuteDetail());
        vo.setStatus(execution.getStatus().getCode());
        return vo;
    }
}