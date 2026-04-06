package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.MedicalOrder;
import com.yhj.his.module.inpatient.entity.OrderExecution;
import com.yhj.his.module.inpatient.enums.ExecutionStatus;
import com.yhj.his.module.inpatient.enums.OrderCategory;
import com.yhj.his.module.inpatient.enums.OrderStatus;
import com.yhj.his.module.inpatient.enums.OrderType;
import com.yhj.his.module.inpatient.repository.MedicalOrderRepository;
import com.yhj.his.module.inpatient.repository.OrderExecutionRepository;
import com.yhj.his.module.inpatient.service.impl.MedicalOrderServiceImpl;
import com.yhj.his.module.inpatient.vo.MedicalOrderVO;
import com.yhj.his.module.inpatient.vo.OrderExecutionVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 医嘱管理服务单元测试
 *
 * 测试覆盖范围：
 * - 医嘱开立
 * - 医嘱审核
 * - 医嘱执行
 * - 医嘱停止
 * - 医嘱作废
 * - 医嘱查询
 * - 执行记录管理
 */
@ExtendWith(MockitoExtension.class)
class MedicalOrderServiceTest {

    @Mock
    private MedicalOrderRepository orderRepository;

    @Mock
    private OrderExecutionRepository executionRepository;

    @InjectMocks
    private MedicalOrderServiceImpl medicalOrderService;

    private MockedStatic<com.yhj.his.common.core.util.SequenceGenerator> sequenceGeneratorMock;

    @BeforeEach
    void setUp() {
        sequenceGeneratorMock = mockStatic(com.yhj.his.common.core.util.SequenceGenerator.class);
        sequenceGeneratorMock.when(() -> com.yhj.his.common.core.util.SequenceGenerator.generateWithTime(anyString()))
                .thenReturn("MO20260406001");
    }

    @AfterEach
    void tearDown() {
        sequenceGeneratorMock.close();
    }

    // ==================== 医嘱开立测试 ====================

    @Test
    @DisplayName("医嘱开立 - 成功开立长期医嘱")
    void testCreateOrder_SuccessLongTermOrder() {
        // 准备数据
        OrderCreateDTO dto = createOrderCreateDTO();
        dto.setOrderType(OrderType.LONG_TERM);
        dto.setFrequency("qd"); // 每日一次

        MedicalOrder savedOrder = createMedicalOrder();
        savedOrder.setOrderType(OrderType.LONG_TERM);
        savedOrder.setStatus(OrderStatus.NEW);

        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(savedOrder);

        // 执行
        String orderId = medicalOrderService.create(dto);

        // 验证
        assertNotNull(orderId);
        verify(orderRepository).save(any(MedicalOrder.class));

        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        MedicalOrder capturedOrder = orderCaptor.getValue();
        assertEquals(OrderType.LONG_TERM, capturedOrder.getOrderType());
        assertEquals(OrderStatus.NEW, capturedOrder.getStatus());
        assertNotNull(capturedOrder.getOrderNo());
        assertNotNull(capturedOrder.getOrderTime());
    }

    @Test
    @DisplayName("医嘱开立 - 成功开立临时医嘱")
    void testCreateOrder_SuccessTemporaryOrder() {
        // 准备数据
        OrderCreateDTO dto = createOrderCreateDTO();
        dto.setOrderType(OrderType.TEMPORARY);
        dto.setFrequency(null); // 临时医嘱无频次

        MedicalOrder savedOrder = createMedicalOrder();
        savedOrder.setOrderType(OrderType.TEMPORARY);

        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(savedOrder);

        // 执行
        String orderId = medicalOrderService.create(dto);

        // 验证
        assertNotNull(orderId);
    }

    @Test
    @DisplayName("医嘱开立 - 成功开立成组医嘱")
    void testCreateOrder_SuccessGroupOrder() {
        // 准备数据
        OrderCreateDTO dto = createOrderCreateDTO();
        dto.setGroupNo(1); // 组号

        MedicalOrder savedOrder = createMedicalOrder();
        savedOrder.setGroupNo(1);

        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(savedOrder);

        // 执行
        String orderId = medicalOrderService.create(dto);

        // 验证
        assertNotNull(orderId);
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(1, orderCaptor.getValue().getGroupNo());
    }

    @Test
    @DisplayName("医嘱开立 - 指定开始时间")
    void testCreateOrder_WithStartTime() {
        // 准备数据
        OrderCreateDTO dto = createOrderCreateDTO();
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        dto.setStartTime(startTime);

        MedicalOrder savedOrder = createMedicalOrder();

        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(savedOrder);

        // 执行
        medicalOrderService.create(dto);

        // 验证
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(startTime, orderCaptor.getValue().getStartTime());
    }

    @Test
    @DisplayName("医嘱开立 - 未指定开始时间时使用当前时间")
    void testCreateOrder_WithoutStartTime() {
        // 准备数据
        OrderCreateDTO dto = createOrderCreateDTO();
        dto.setStartTime(null);

        MedicalOrder savedOrder = createMedicalOrder();

        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(savedOrder);

        // 执行
        medicalOrderService.create(dto);

        // 验证
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertNotNull(orderCaptor.getValue().getStartTime());
    }

    // ==================== 医嘱审核测试 ====================

    @Test
    @DisplayName("医嘱审核 - 审核通过")
    void testAuditOrder_ApproveSuccess() {
        // 准备数据
        OrderAuditDTO dto = createOrderAuditDTO();
        dto.setAuditResult("通过");

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.audit(dto);

        // 验证
        assertTrue(result);
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.AUDITED, orderCaptor.getValue().getStatus());
        assertNotNull(orderCaptor.getValue().getAuditTime());
        assertEquals(dto.getNurseId(), orderCaptor.getValue().getNurseId());
    }

    @Test
    @DisplayName("医嘱审核 - 审核驳回")
    void testAuditOrder_RejectSuccess() {
        // 准备数据
        OrderAuditDTO dto = createOrderAuditDTO();
        dto.setAuditResult("驳回");
        dto.setAuditRemark("医嘱内容不完整");

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.audit(dto);

        // 验证
        assertTrue(result);
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.REJECTED, orderCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("医嘱审核 - 医嘱不存在")
    void testAuditOrder_OrderNotFound() {
        OrderAuditDTO dto = createOrderAuditDTO();

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.audit(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("医嘱不存在", exception.getMessage());
    }

    @Test
    @DisplayName("医嘱审核 - 医嘱状态不允许审核（已审核）")
    void testAuditOrder_AlreadyAudited() {
        OrderAuditDTO dto = createOrderAuditDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.AUDITED); // 已经审核过

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.audit(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("医嘱状态不允许审核", exception.getMessage());
    }

    @Test
    @DisplayName("医嘱审核 - 医嘱状态不允许审核（正在执行）")
    void testAuditOrder_AlreadyExecuting() {
        OrderAuditDTO dto = createOrderAuditDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.EXECUTING);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.audit(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("医嘱审核 - 审核结果无效")
    void testAuditOrder_InvalidAuditResult() {
        OrderAuditDTO dto = createOrderAuditDTO();
        dto.setAuditResult("无效结果"); // 无效的审核结果

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.audit(dto));

        assertEquals(ErrorCode.PARAM_ERROR.getCode(), exception.getCode());
        assertEquals("审核结果无效", exception.getMessage());
    }

    // ==================== 医嘱执行测试 ====================

    @Test
    @DisplayName("医嘱执行 - 成功执行（首次执行）")
    void testExecuteOrder_FirstExecution() {
        // 准备数据
        OrderExecuteDTO dto = createOrderExecuteDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.AUDITED);

        OrderExecution execution = createOrderExecution();

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(executionRepository.save(any(OrderExecution.class))).thenReturn(execution);
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        String executionId = medicalOrderService.execute(dto);

        // 验证
        assertNotNull(executionId);

        // 验证执行记录被保存
        ArgumentCaptor<OrderExecution> executionCaptor = ArgumentCaptor.forClass(OrderExecution.class);
        verify(executionRepository).save(executionCaptor.capture());
        assertEquals(ExecutionStatus.EXECUTED, executionCaptor.getValue().getStatus());

        // 验证医嘱状态更新为执行中
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.EXECUTING, orderCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("医嘱执行 - 成功执行（继续执行）")
    void testExecuteOrder_ContinueExecution() {
        // 准备数据
        OrderExecuteDTO dto = createOrderExecuteDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.EXECUTING); // 已经在执行中

        OrderExecution execution = createOrderExecution();

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(executionRepository.save(any(OrderExecution.class))).thenReturn(execution);

        // 执行
        String executionId = medicalOrderService.execute(dto);

        // 验证
        assertNotNull(executionId);
        // 继续执行时不更新医嘱状态
        verify(orderRepository, never()).save(any(MedicalOrder.class));
    }

    @Test
    @DisplayName("医嘱执行 - 医嘱不存在")
    void testExecuteOrder_OrderNotFound() {
        OrderExecuteDTO dto = createOrderExecuteDTO();

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.execute(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("医嘱执行 - 医嘱状态不允许执行（新开状态）")
    void testExecuteOrder_NewStatusNotAllowed() {
        OrderExecuteDTO dto = createOrderExecuteDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW); // 新开状态未审核，不能执行

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.execute(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("医嘱状态不允许执行", exception.getMessage());
    }

    @Test
    @DisplayName("医嘱执行 - 医嘱状态不允许执行（已停止）")
    void testExecuteOrder_StoppedNotAllowed() {
        OrderExecuteDTO dto = createOrderExecuteDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.STOPPED);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.execute(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
    }

    // ==================== 医嘱停止测试 ====================

    @Test
    @DisplayName("医嘱停止 - 成功停止执行中的医嘱")
    void testStopOrder_SuccessFromExecuting() {
        // 准备数据
        OrderStopDTO dto = createOrderStopDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.EXECUTING);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.stop(dto);

        // 验证
        assertTrue(result);
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.STOPPED, orderCaptor.getValue().getStatus());
        assertNotNull(orderCaptor.getValue().getStopTime());
        assertEquals(dto.getStopReason(), orderCaptor.getValue().getStopReason());
        assertEquals(dto.getStopDoctorId(), orderCaptor.getValue().getStopDoctorId());
    }

    @Test
    @DisplayName("医嘱停止 - 成功停止已审核的医嘱")
    void testStopOrder_SuccessFromAudited() {
        // 准备数据
        OrderStopDTO dto = createOrderStopDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.AUDITED);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.stop(dto);

        // 验证
        assertTrue(result);
    }

    @Test
    @DisplayName("医嘱停止 - 医嘱不存在")
    void testStopOrder_OrderNotFound() {
        OrderStopDTO dto = createOrderStopDTO();

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.stop(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("医嘱停止 - 医嘱状态不允许停止（新开状态）")
    void testStopOrder_NewStatusNotAllowed() {
        OrderStopDTO dto = createOrderStopDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.stop(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("医嘱状态不允许停止", exception.getMessage());
    }

    @Test
    @DisplayName("医嘱停止 - 医嘱状态不允许停止（已作废）")
    void testStopOrder_CancelledNotAllowed() {
        OrderStopDTO dto = createOrderStopDTO();

        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.stop(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
    }

    // ==================== 医嘱作废测试 ====================

    @Test
    @DisplayName("医嘱作废 - 成功作废新开医嘱")
    void testCancelOrder_SuccessNewOrder() {
        // 准备数据
        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.findById("order-001")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.cancel("order-001", "医生发现错误");

        // 验证
        assertTrue(result);
        ArgumentCaptor<MedicalOrder> orderCaptor = ArgumentCaptor.forClass(MedicalOrder.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatus.CANCELLED, orderCaptor.getValue().getStatus());
        assertEquals("医生发现错误", orderCaptor.getValue().getStopReason());
        assertNotNull(orderCaptor.getValue().getStopTime());
    }

    @Test
    @DisplayName("医嘱作废 - 成功作废已审核医嘱")
    void testCancelOrder_SuccessAuditedOrder() {
        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.AUDITED);

        when(orderRepository.findById("order-001")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(MedicalOrder.class))).thenReturn(order);

        // 执行
        boolean result = medicalOrderService.cancel("order-001", "患者拒绝执行");

        // 验证
        assertTrue(result);
    }

    @Test
    @DisplayName("医嘱作废 - 医嘱不存在")
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.cancel("invalid-id", "作废原因"));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("医嘱作废 - 正在执行的医嘱不能作废")
    void testCancelOrder_ExecutingNotAllowed() {
        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.EXECUTING);

        when(orderRepository.findById("order-001")).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.cancel("order-001", "尝试作废"));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("正在执行或已完成的医嘱不能作废", exception.getMessage());
    }

    @Test
    @DisplayName("医嘱作废 - 已完成的医嘱不能作废")
    void testCancelOrder_CompletedNotAllowed() {
        MedicalOrder order = createMedicalOrder();
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById("order-001")).thenReturn(Optional.of(order));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.cancel("order-001", "尝试作废"));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
    }

    // ==================== 医嘱查询测试 ====================

    @Test
    @DisplayName("查询医嘱详情 - 成功查询")
    void testGetOrderById_Success() {
        MedicalOrder order = createMedicalOrder();

        when(orderRepository.findById("order-001")).thenReturn(Optional.of(order));
        when(executionRepository.countExecutedByOrderId("order-001")).thenReturn(3L);

        // 执行
        MedicalOrderVO result = medicalOrderService.getById("order-001");

        // 验证
        assertNotNull(result);
        assertEquals("order-001", result.getOrderId());
        assertEquals("MO20260406001", result.getOrderNo());
        assertEquals(3L, result.getExecuteCount());
    }

    @Test
    @DisplayName("查询医嘱详情 - 医嘱不存在")
    void testGetOrderById_NotFound() {
        when(orderRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> medicalOrderService.getById("invalid-id"));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("查询住院医嘱列表 - 成功查询")
    void testListByAdmission_Success() {
        List<MedicalOrder> orders = new ArrayList<>();
        orders.add(createMedicalOrder());
        orders.add(createMedicalOrder());

        when(orderRepository.findByAdmissionId("admission-001")).thenReturn(orders);
        when(executionRepository.countExecutedByOrderId(anyString())).thenReturn(0L);

        // 执行
        List<MedicalOrderVO> result = medicalOrderService.listByAdmission("admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("查询正在执行的医嘱 - 成功查询")
    void testListActiveOrders_Success() {
        MedicalOrder order1 = createMedicalOrder();
        order1.setStatus(OrderStatus.EXECUTING);
        order1.setOrderType(OrderType.LONG_TERM);

        MedicalOrder order2 = createMedicalOrder();
        order2.setStatus(OrderStatus.AUDITED);
        order2.setOrderType(OrderType.LONG_TERM);

        List<MedicalOrder> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderRepository.findActiveLongTermOrders("admission-001")).thenReturn(orders);
        when(executionRepository.countExecutedByOrderId(anyString())).thenReturn(1L);

        // 执行
        List<MedicalOrderVO> result = medicalOrderService.listActiveOrders("admission-001");

        // 验证
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(o -> o.getStatus() == OrderStatus.EXECUTING));
        assertTrue(result.stream().anyMatch(o -> o.getStatus() == OrderStatus.AUDITED));
    }

    @Test
    @DisplayName("分页查询医嘱 - 成功分页")
    void testPageOrders_Success() {
        List<MedicalOrder> orders = new ArrayList<>();
        orders.add(createMedicalOrder());
        orders.add(createMedicalOrder());

        Page<MedicalOrder> page = new PageImpl<>(orders);
        when(orderRepository.findByAdmissionId(eq("admission-001"), any(PageRequest.class))).thenReturn(page);
        when(executionRepository.countExecutedByOrderId(anyString())).thenReturn(0L);

        // 执行
        PageResult<MedicalOrderVO> result = medicalOrderService.page(1, 10, "admission-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("分页查询医嘱 - 查询所有医嘱（不指定住院ID）")
    void testPageOrders_AllOrders() {
        List<MedicalOrder> orders = new ArrayList<>();
        orders.add(createMedicalOrder());

        Page<MedicalOrder> page = new PageImpl<>(orders);
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(executionRepository.countExecutedByOrderId(anyString())).thenReturn(0L);

        // 执行
        PageResult<MedicalOrderVO> result = medicalOrderService.page(1, 10, null);

        // 验证
        assertEquals(1, result.getTotal());
        verify(orderRepository).findAll(any(PageRequest.class));
    }

    // ==================== 执行记录查询测试 ====================

    @Test
    @DisplayName("查询医嘱执行记录 - 成功查询")
    void testListExecutions_Success() {
        List<OrderExecution> executions = new ArrayList<>();
        executions.add(createOrderExecution());
        executions.add(createOrderExecution());

        when(executionRepository.findByOrderId("order-001")).thenReturn(executions);

        // 执行
        List<OrderExecutionVO> result = medicalOrderService.listExecutions("order-001");

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("已执行", result.get(0).getStatus());
    }

    @Test
    @DisplayName("查询医嘱执行记录 - 无执行记录")
    void testListExecutions_Empty() {
        when(executionRepository.findByOrderId("order-001")).thenReturn(Collections.emptyList());

        // 执行
        List<OrderExecutionVO> result = medicalOrderService.listExecutions("order-001");

        // 验证
        assertTrue(result.isEmpty());
    }

    // ==================== 辅助方法 ====================

    private OrderCreateDTO createOrderCreateDTO() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setAdmissionId("admission-001");
        dto.setPatientId("P001");
        dto.setOrderType(OrderType.LONG_TERM);
        dto.setOrderCategory(OrderCategory.DRUG);
        dto.setOrderContent("阿司匹林肠溶片 100mg 口服 每日一次");
        dto.setOrderDetail("{\"drugId\": \"D001\", \"drugName\": \"阿司匹林肠溶片\", \"spec\": \"100mg\", \"dose\": \"100mg\", \"route\": \"口服\"}");
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusDays(7));
        dto.setExecuteTime("08:00");
        dto.setFrequency("qd");
        dto.setDoctorId("DOC001");
        dto.setDoctorName("李医生");
        dto.setGroupNo(null);
        return dto;
    }

    private MedicalOrder createMedicalOrder() {
        MedicalOrder order = new MedicalOrder();
        order.setId("order-001");
        order.setOrderNo("MO20260406001");
        order.setAdmissionId("admission-001");
        order.setPatientId("P001");
        order.setOrderType(OrderType.LONG_TERM);
        order.setOrderCategory(OrderCategory.DRUG);
        order.setOrderContent("阿司匹林肠溶片 100mg 口服 每日一次");
        order.setOrderDetail("{\"drugId\": \"D001\"}");
        order.setStartTime(LocalDateTime.now());
        order.setEndTime(LocalDateTime.now().plusDays(7));
        order.setExecuteTime("08:00");
        order.setFrequency("qd");
        order.setDoctorId("DOC001");
        order.setDoctorName("李医生");
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    private OrderAuditDTO createOrderAuditDTO() {
        OrderAuditDTO dto = new OrderAuditDTO();
        dto.setOrderId("order-001");
        dto.setNurseId("NUR001");
        dto.setNurseName("王护士");
        dto.setAuditResult("通过");
        dto.setAuditRemark("医嘱内容正确");
        return dto;
    }

    private OrderExecuteDTO createOrderExecuteDTO() {
        OrderExecuteDTO dto = new OrderExecuteDTO();
        dto.setOrderId("order-001");
        dto.setAdmissionId("admission-001");
        dto.setPatientId("P001");
        dto.setExecuteTime(LocalDateTime.now());
        dto.setExecuteNurseId("NUR001");
        dto.setExecuteNurseName("王护士");
        dto.setExecuteResult("执行完成");
        dto.setExecuteDetail("{\"actualTime\": \"08:05\"}");
        return dto;
    }

    private OrderStopDTO createOrderStopDTO() {
        OrderStopDTO dto = new OrderStopDTO();
        dto.setOrderId("order-001");
        dto.setStopDoctorId("DOC001");
        dto.setStopDoctorName("李医生");
        dto.setStopReason("患者病情好转，停药");
        return dto;
    }

    private OrderExecution createOrderExecution() {
        OrderExecution execution = new OrderExecution();
        execution.setId("execution-001");
        execution.setOrderId("order-001");
        execution.setAdmissionId("admission-001");
        execution.setPatientId("P001");
        execution.setExecuteTime(LocalDateTime.now());
        execution.setExecuteNurseId("NUR001");
        execution.setExecuteNurseName("王护士");
        execution.setExecuteResult("执行完成");
        execution.setStatus(ExecutionStatus.EXECUTED);
        return execution;
    }
}