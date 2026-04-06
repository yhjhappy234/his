package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.TestRequestCreateDTO;
import com.yhj.his.module.lis.entity.TestItem;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.entity.TestRequestItem;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.TestItemCategory;
import com.yhj.his.module.lis.enums.TestItemStatus;
import com.yhj.his.module.lis.enums.SpecimenType;
import com.yhj.his.module.lis.enums.VisitType;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.repository.TestRequestItemRepository;
import com.yhj.his.module.lis.repository.TestItemRepository;
import com.yhj.his.module.lis.service.impl.TestRequestServiceImpl;
import com.yhj.his.module.lis.vo.TestRequestVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TestRequestService单元测试
 * 测试检验申请工作流程
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("检验申请服务测试")
class TestRequestServiceTest {

    @Mock
    private TestRequestRepository testRequestRepository;

    @Mock
    private TestRequestItemRepository testRequestItemRepository;

    @Mock
    private TestItemRepository testItemRepository;

    @Mock
    private TestRequestItemService testRequestItemService;

    @InjectMocks
    private TestRequestServiceImpl testRequestService;

    private TestRequest testRequest;
    private TestRequestItem testRequestItem;
    private TestItem testItem;
    private TestRequestCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试检验项目
        testItem = new TestItem();
        testItem.setId("item-001");
        testItem.setItemCode("GLU");
        testItem.setItemName("血糖");
        testItem.setSpecimenType(SpecimenType.BLOOD);
        testItem.setPrice(new BigDecimal("10.00"));
        testItem.setCategory(TestItemCategory.BIOCHEMISTRY);
        testItem.setStatus(TestItemStatus.NORMAL);
        testItem.setCreateTime(LocalDateTime.now());

        // 初始化检验申请
        testRequest = new TestRequest();
        testRequest.setId("request-001");
        testRequest.setRequestNo("LIS20260406120000");
        testRequest.setPatientId("patient-001");
        testRequest.setPatientName("张三");
        testRequest.setGender("男");
        testRequest.setAge(30);
        testRequest.setIdCardNo("123456789012345678");
        testRequest.setVisitType(VisitType.OUTPATIENT);
        testRequest.setVisitId("visit-001");
        testRequest.setDeptId("dept-001");
        testRequest.setDeptName("内科");
        testRequest.setDoctorId("doctor-001");
        testRequest.setDoctorName("李医生");
        testRequest.setClinicalDiagnosis("糖尿病");
        testRequest.setClinicalInfo("血糖偏高");
        testRequest.setRequestTime(LocalDateTime.now());
        testRequest.setEmergency(false);
        testRequest.setStatus(TestRequestStatus.REQUESTED);
        testRequest.setTotalAmount(new BigDecimal("10.00"));
        testRequest.setCreateTime(LocalDateTime.now());

        // 初始化申请明细
        testRequestItem = new TestRequestItem();
        testRequestItem.setId("request-item-001");
        testRequestItem.setRequestId("request-001");
        testRequestItem.setItemId("item-001");
        testRequestItem.setItemCode("GLU");
        testRequestItem.setItemName("血糖");
        testRequestItem.setSpecimenType(SpecimenType.BLOOD);
        testRequestItem.setPrice(new BigDecimal("10.00"));

        // 初始化创建DTO
        createDTO = new TestRequestCreateDTO();
        createDTO.setPatientId("patient-001");
        createDTO.setPatientName("张三");
        createDTO.setGender("男");
        createDTO.setAge(30);
        createDTO.setIdCardNo("123456789012345678");
        createDTO.setVisitType("OUTPATIENT");
        createDTO.setVisitId("visit-001");
        createDTO.setDeptId("dept-001");
        createDTO.setDeptName("内科");
        createDTO.setDoctorId("doctor-001");
        createDTO.setDoctorName("李医生");
        createDTO.setClinicalDiagnosis("糖尿病");
        createDTO.setClinicalInfo("血糖偏高");
        createDTO.setEmergency(false);

        TestRequestCreateDTO.TestRequestItemDTO itemDTO = new TestRequestCreateDTO.TestRequestItemDTO();
        itemDTO.setItemId("item-001");
        createDTO.setItems(Arrays.asList(itemDTO));
    }

    @Nested
    @DisplayName("创建检验申请测试")
    class CreateTestRequestTests {

        @Test
        @DisplayName("成功创建检验申请")
        void createSuccessfully() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);
            when(testRequestItemRepository.save(any(TestRequestItem.class))).thenReturn(testRequestItem);
            when(testRequestItemRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.create(createDTO);

            // Then
            assertNotNull(result);
            assertEquals("patient-001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals("OUTPATIENT", result.getVisitType());
            assertEquals("REQUESTED", result.getStatus());
            assertNotNull(result.getItems());
            assertEquals(1, result.getItems().size());

            verify(testItemRepository).findById("item-001");
            verify(testRequestRepository).save(any(TestRequest.class));
            verify(testRequestItemRepository).save(any(TestRequestItem.class));
        }

        @Test
        @DisplayName("创建急诊检验申请")
        void createEmergencyRequest() {
            // Given
            createDTO.setEmergency(true);
            createDTO.setEmergencyLevel("CRITICAL");

            TestRequest emergencyRequest = new TestRequest();
            emergencyRequest.setId("request-002");
            emergencyRequest.setRequestNo("LIS20260406120100");
            emergencyRequest.setPatientId("patient-001");
            emergencyRequest.setVisitType(VisitType.EMERGENCY);
            emergencyRequest.setEmergency(true);
            emergencyRequest.setEmergencyLevel("CRITICAL");
            emergencyRequest.setStatus(TestRequestStatus.REQUESTED);
            emergencyRequest.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(emergencyRequest);
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.create(createDTO);

            // Then
            assertNotNull(result);
            assertTrue(result.getEmergency());
            assertEquals("CRITICAL", result.getEmergencyLevel());
        }

        @Test
        @DisplayName("创建失败-检验项目不存在")
        void createFailedWithItemNotFound() {
            // Given
            when(testItemRepository.findById("item-001")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testRequestService.create(createDTO)
            );
            assertEquals("检验项目不存在: item-001", exception.getMessage());

            verify(testRequestRepository, never()).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("创建多项目检验申请")
        void createMultiItemRequest() {
            // Given
            TestItem item2 = new TestItem();
            item2.setId("item-002");
            item2.setItemCode("ALT");
            item2.setItemName("谷丙转氨酶");
            item2.setSpecimenType(SpecimenType.BLOOD);
            item2.setPrice(new BigDecimal("15.00"));

            TestRequestCreateDTO.TestRequestItemDTO itemDTO1 = new TestRequestCreateDTO.TestRequestItemDTO();
            itemDTO1.setItemId("item-001");
            TestRequestCreateDTO.TestRequestItemDTO itemDTO2 = new TestRequestCreateDTO.TestRequestItemDTO();
            itemDTO2.setItemId("item-002");
            createDTO.setItems(Arrays.asList(itemDTO1, itemDTO2));

            TestRequest multiRequest = new TestRequest();
            multiRequest.setId("request-003");
            multiRequest.setVisitType(VisitType.OUTPATIENT);
            multiRequest.setTotalAmount(new BigDecimal("25.00"));
            multiRequest.setStatus(TestRequestStatus.REQUESTED);
            multiRequest.setCreateTime(LocalDateTime.now());

            when(testItemRepository.findById("item-001")).thenReturn(Optional.of(testItem));
            when(testItemRepository.findById("item-002")).thenReturn(Optional.of(item2));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(multiRequest);
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(new ArrayList<>());

            // When
            TestRequestVO result = testRequestService.create(createDTO);

            // Then
            assertNotNull(result);
            assertEquals(new BigDecimal("25.00"), result.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("取消检验申请测试")
    class CancelTestRequestTests {

        @Test
        @DisplayName("成功取消检验申请")
        void cancelSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);
            when(testRequestItemRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.cancel("request-001", "患者要求取消", "user-001");

            // Then
            assertNotNull(result);
            assertEquals("CANCELLED", result.getStatus());

            verify(testRequestRepository).findById("request-001");
            verify(testRequestRepository).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("取消失败-已发布的申请不能取消")
        void cancelFailedWithPublishedRequest() {
            // Given
            testRequest.setStatus(TestRequestStatus.PUBLISHED);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testRequestService.cancel("request-001", "取消原因", "user-001")
            );
            assertEquals("已发布的申请不能取消", exception.getMessage());

            verify(testRequestRepository, never()).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("取消失败-申请不存在")
        void cancelFailedWithNotFound() {
            // Given
            when(testRequestRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> testRequestService.cancel("non-existent", "取消原因", "user-001"));
        }
    }

    @Nested
    @DisplayName("查询检验申请测试")
    class QueryTestRequestTests {

        @Test
        @DisplayName("根据ID查询检验申请")
        void getByIdSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testRequestItemRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.getById("request-001");

            // Then
            assertNotNull(result);
            assertEquals("request-001", result.getId());
            assertEquals("张三", result.getPatientName());
            assertEquals(1, result.getItems().size());
        }

        @Test
        @DisplayName("根据申请单号查询检验申请")
        void getByRequestNoSuccessfully() {
            // Given
            when(testRequestRepository.findByRequestNo("LIS20260406120000")).thenReturn(Optional.of(testRequest));
            when(testRequestItemRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.getByRequestNo("LIS20260406120000");

            // Then
            assertNotNull(result);
            assertEquals("LIS20260406120000", result.getRequestNo());
        }

        @Test
        @DisplayName("分页查询检验申请")
        void listWithPageable() {
            // Given
            List<TestRequest> requests = Arrays.asList(testRequest);
            Page<TestRequest> page = new PageImpl<>(requests);
            Pageable pageable = PageRequest.of(0, 10);

            when(testRequestRepository.findAll(pageable)).thenReturn(page);
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            PageResult<TestRequestVO> result = testRequestService.list(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("根据患者ID查询检验申请")
        void listByPatientId() {
            // Given
            when(testRequestRepository.findByPatientId("patient-001")).thenReturn(Arrays.asList(testRequest));
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            List<TestRequestVO> result = testRequestService.listByPatientId("patient-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("根据状态查询检验申请")
        void listByStatus() {
            // Given
            when(testRequestRepository.findByStatus(TestRequestStatus.REQUESTED)).thenReturn(Arrays.asList(testRequest));
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            List<TestRequestVO> result = testRequestService.listByStatus(TestRequestStatus.REQUESTED);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("REQUESTED", result.get(0).getStatus());
        }

        @Test
        @DisplayName("根据就诊ID查询检验申请")
        void listByVisitId() {
            // Given
            when(testRequestRepository.findByVisitId("visit-001")).thenReturn(Arrays.asList(testRequest));
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            List<TestRequestVO> result = testRequestService.listByVisitId("visit-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("查询急诊申请")
        void listEmergencyRequests() {
            // Given
            TestRequest emergencyRequest = new TestRequest();
            emergencyRequest.setId("request-002");
            emergencyRequest.setVisitType(VisitType.EMERGENCY);
            emergencyRequest.setEmergency(true);
            emergencyRequest.setStatus(TestRequestStatus.REQUESTED);
            emergencyRequest.setCreateTime(LocalDateTime.now());

            when(testRequestRepository.findEmergencyRequests(anyList())).thenReturn(Arrays.asList(emergencyRequest));
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            List<TestRequestVO> result = testRequestService.listEmergencyRequests();

            // Then
            assertNotNull(result);
            assertTrue(result.get(0).getEmergency());
        }

        @Test
        @DisplayName("根据科室和时间范围查询")
        void listByDeptAndTime() {
            // Given
            LocalDateTime startTime = LocalDateTime.now().minusDays(1);
            LocalDateTime endTime = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            Page<TestRequest> page = new PageImpl<>(Arrays.asList(testRequest));

            when(testRequestRepository.findByDeptIdAndRequestTimeBetween(anyString(), any(LocalDateTime.class),
                    any(LocalDateTime.class), eq(pageable))).thenReturn(page);
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            PageResult<TestRequestVO> result = testRequestService.listByDeptAndTime("dept-001", startTime, endTime, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }
    }

    @Nested
    @DisplayName("更新申请状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("成功更新申请状态")
        void updateStatusSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);
            when(testRequestItemRepository.findByRequestId(anyString())).thenReturn(Arrays.asList(testRequestItem));

            // When
            TestRequestVO result = testRequestService.updateStatus("request-001", TestRequestStatus.SAMPLED);

            // Then
            assertNotNull(result);
            assertEquals("SAMPLED", result.getStatus());
        }

        @Test
        @DisplayName("更新状态失败-申请不存在")
        void updateStatusFailedWithNotFound() {
            // Given
            when(testRequestRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> testRequestService.updateStatus("non-existent", TestRequestStatus.SAMPLED));
        }
    }

    @Nested
    @DisplayName("删除检验申请测试")
    class DeleteTestRequestTests {

        @Test
        @DisplayName("成功删除检验申请")
        void deleteSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);
            doNothing().when(testRequestItemRepository).deleteByRequestId("request-001");

            // When
            testRequestService.delete("request-001");

            // Then
            verify(testRequestRepository).findById("request-001");
            verify(testRequestRepository).save(any(TestRequest.class));
            verify(testRequestItemRepository).deleteByRequestId("request-001");
        }

        @Test
        @DisplayName("删除失败-申请不存在")
        void deleteFailedWithNotFound() {
            // Given
            when(testRequestRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> testRequestService.delete("non-existent"));
        }
    }

    @Nested
    @DisplayName("统计申请数量测试")
    class CountTests {

        @Test
        @DisplayName("统计某状态的申请数量")
        void countByStatus() {
            // Given
            when(testRequestRepository.countByStatus(TestRequestStatus.REQUESTED)).thenReturn(5L);

            // When
            long count = testRequestService.countByStatus(TestRequestStatus.REQUESTED);

            // Then
            assertEquals(5L, count);
        }
    }
}