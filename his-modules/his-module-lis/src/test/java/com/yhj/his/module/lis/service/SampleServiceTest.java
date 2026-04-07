package com.yhj.his.module.lis.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.lis.dto.SampleCollectDTO;
import com.yhj.his.module.lis.dto.SampleReceiveDTO;
import com.yhj.his.module.lis.dto.SampleRejectDTO;
import com.yhj.his.module.lis.entity.Sample;
import com.yhj.his.module.lis.entity.TestRequest;
import com.yhj.his.module.lis.enums.SampleStatus;
import com.yhj.his.module.lis.enums.TestRequestStatus;
import com.yhj.his.module.lis.enums.VisitType;
import com.yhj.his.module.lis.enums.SpecimenType;
import com.yhj.his.module.lis.repository.SampleRepository;
import com.yhj.his.module.lis.repository.TestRequestRepository;
import com.yhj.his.module.lis.service.impl.SampleServiceImpl;
import com.yhj.his.module.lis.vo.SampleVO;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SampleService单元测试
 * 测试样本采集和追踪功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("样本服务测试")
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;

    @Mock
    private TestRequestRepository testRequestRepository;

    @InjectMocks
    private SampleServiceImpl sampleService;

    private Sample sample;
    private TestRequest testRequest;
    private SampleCollectDTO collectDTO;
    private SampleReceiveDTO receiveDTO;
    private SampleRejectDTO rejectDTO;

    @BeforeEach
    void setUp() {
        // 初始化检验申请
        testRequest = new TestRequest();
        testRequest.setId("request-001");
        testRequest.setRequestNo("LIS20260406120000");
        testRequest.setPatientId("patient-001");
        testRequest.setPatientName("张三");
        testRequest.setVisitType(VisitType.OUTPATIENT);
        testRequest.setStatus(TestRequestStatus.REQUESTED);
        testRequest.setCreateTime(LocalDateTime.now());

        // 初始化样本
        sample = new Sample();
        sample.setId("sample-001");
        sample.setSampleNo("SMP20260406120000");
        sample.setRequestId("request-001");
        sample.setPatientId("patient-001");
        sample.setPatientName("张三");
        sample.setSpecimenType(SpecimenType.BLOOD);
        sample.setCollectorId("collector-001");
        sample.setCollectorName("护士王");
        sample.setCollectionTime(LocalDateTime.now());
        sample.setCollectionLocation("采血室");
        sample.setSampleStatus(SampleStatus.COLLECTED);
        sample.setCreateTime(LocalDateTime.now());
        sample.setUpdateTime(LocalDateTime.now());

        // 初始化采集DTO
        collectDTO = new SampleCollectDTO();
        collectDTO.setRequestId("request-001");
        collectDTO.setCollectorId("collector-001");
        collectDTO.setCollectorName("护士王");
        collectDTO.setCollectionLocation("采血室");

        // 初始化核收DTO
        receiveDTO = new SampleReceiveDTO();
        receiveDTO.setSampleNo("SMP20260406120000");
        receiveDTO.setReceiverId("receiver-001");
        receiveDTO.setReceiverName("检验员李");
        receiveDTO.setStorageLocation("冰箱A");
        receiveDTO.setTestGroup("生化组");

        // 初始化拒收DTO
        rejectDTO = new SampleRejectDTO();
        rejectDTO.setSampleNo("SMP20260406120000");
        rejectDTO.setRejectReason("样本溶血");
        rejectDTO.setRejectUserId("user-001");
    }

    @Nested
    @DisplayName("样本采集测试")
    class CollectSampleTests {

        @Test
        @DisplayName("成功采集样本")
        void collectSuccessfully() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);

            // When
            SampleVO result = sampleService.collect(collectDTO);

            // Then
            assertNotNull(result);
            assertEquals("patient-001", result.getPatientId());
            assertEquals("张三", result.getPatientName());
            assertEquals("COLLECTED", result.getSampleStatus());
            assertNotNull(result.getCollectionTime());

            verify(testRequestRepository, atLeast(1)).findById("request-001");
            verify(sampleRepository).save(any(Sample.class));
            verify(testRequestRepository).save(any(TestRequest.class));
        }

        @Test
        @DisplayName("采集样本-指定采集时间")
        void collectWithCustomTime() {
            // Given
            LocalDateTime customTime = LocalDateTime.now().minusHours(1);
            collectDTO.setCollectionTime(customTime);

            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.save(any(TestRequest.class))).thenReturn(testRequest);

            // When
            SampleVO result = sampleService.collect(collectDTO);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("采集失败-申请不存在")
        void collectFailedWithRequestNotFound() {
            // Given
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sampleService.collect(collectDTO)
            );
            assertEquals("检验申请不存在: request-001", exception.getMessage());

            verify(sampleRepository, never()).save(any(Sample.class));
        }

        @Test
        @DisplayName("采集样本-更新申请状态为已采样")
        void collectUpdatesRequestStatus() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setStatus(TestRequestStatus.REQUESTED);
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals(TestRequestStatus.SAMPLED, saved.getStatus());
                assertEquals("COLLECTED", saved.getSampleStatus());
                return saved;
            });

            // When
            sampleService.collect(collectDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("样本核收测试")
    class ReceiveSampleTests {

        @Test
        @DisplayName("成功核收样本")
        void receiveSuccessfully() {
            // Given
            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            SampleVO result = sampleService.receive(receiveDTO);

            // Then
            assertNotNull(result);
            assertEquals("RECEIVED", result.getSampleStatus());
            assertEquals("receiver-001", result.getReceiverId());
            assertEquals("检验员李", result.getReceiverName());
            assertNotNull(result.getReceiveTime());

            verify(sampleRepository).findBySampleNo("SMP20260406120000");
            verify(sampleRepository).save(any(Sample.class));
        }

        @Test
        @DisplayName("核收失败-样本不存在")
        void receiveFailedWithSampleNotFound() {
            // Given
            when(sampleRepository.findBySampleNo("UNKNOWN")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    receiveDTO.setSampleNo("UNKNOWN");
                    sampleService.receive(receiveDTO);
                }
            );
            assertEquals("样本不存在: UNKNOWN", exception.getMessage());
        }

        @Test
        @DisplayName("核收失败-样本状态不是已采集")
        void receiveFailedWithWrongStatus() {
            // Given
            sample.setSampleStatus(SampleStatus.RECEIVED);
            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sampleService.receive(receiveDTO)
            );
            assertEquals("只有已采集的样本才能核收", exception.getMessage());

            verify(sampleRepository, never()).save(any(Sample.class));
        }

        @Test
        @DisplayName("核收样本-指定核收时间")
        void receiveWithCustomTime() {
            // Given
            LocalDateTime customTime = LocalDateTime.now().minusMinutes(30);
            receiveDTO.setReceiveTime(customTime);

            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            SampleVO result = sampleService.receive(receiveDTO);

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("核收样本-更新申请状态为已核收")
        void receiveUpdatesRequestStatus() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setStatus(TestRequestStatus.SAMPLED);
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals(TestRequestStatus.RECEIVED, saved.getStatus());
                return saved;
            });

            // When
            sampleService.receive(receiveDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("样本拒收测试")
    class RejectSampleTests {

        @Test
        @DisplayName("成功拒收样本")
        void rejectSuccessfully() {
            // Given
            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            SampleVO result = sampleService.reject(rejectDTO);

            // Then
            assertNotNull(result);
            assertEquals("REJECTED", result.getSampleStatus());
            assertEquals("样本溶血", result.getRejectReason());
            assertNotNull(result.getRejectTime());

            verify(sampleRepository).findBySampleNo("SMP20260406120000");
            verify(sampleRepository).save(any(Sample.class));
        }

        @Test
        @DisplayName("拒收失败-样本不存在")
        void rejectFailedWithSampleNotFound() {
            // Given
            when(sampleRepository.findBySampleNo("UNKNOWN")).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    rejectDTO.setSampleNo("UNKNOWN");
                    sampleService.reject(rejectDTO);
                }
            );
            assertEquals("样本不存在: UNKNOWN", exception.getMessage());
        }

        @Test
        @DisplayName("拒收样本-更新申请样本状态")
        void rejectUpdatesRequestSampleStatus() {
            // Given
            TestRequest requestToUpdate = new TestRequest();
            requestToUpdate.setId("request-001");
            requestToUpdate.setCreateTime(LocalDateTime.now());

            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(requestToUpdate));
            when(testRequestRepository.save(any(TestRequest.class))).thenAnswer(invocation -> {
                TestRequest saved = invocation.getArgument(0);
                assertEquals("REJECTED", saved.getSampleStatus());
                return saved;
            });

            // When
            sampleService.reject(rejectDTO);

            // Then
            verify(testRequestRepository).save(any(TestRequest.class));
        }
    }

    @Nested
    @DisplayName("查询样本测试")
    class QuerySampleTests {

        @Test
        @DisplayName("根据ID查询样本")
        void getByIdSuccessfully() {
            // Given
            when(sampleRepository.findById("sample-001")).thenReturn(Optional.of(sample));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            SampleVO result = sampleService.getById("sample-001");

            // Then
            assertNotNull(result);
            assertEquals("sample-001", result.getId());
            assertEquals("SMP20260406120000", result.getSampleNo());
        }

        @Test
        @DisplayName("根据ID查询失败-样本不存在")
        void getByIdNotFound() {
            // Given
            when(sampleRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> sampleService.getById("non-existent"));
        }

        @Test
        @DisplayName("根据样本编号查询样本")
        void getBySampleNoSuccessfully() {
            // Given
            when(sampleRepository.findBySampleNo("SMP20260406120000")).thenReturn(Optional.of(sample));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            SampleVO result = sampleService.getBySampleNo("SMP20260406120000");

            // Then
            assertNotNull(result);
            assertEquals("SMP20260406120000", result.getSampleNo());
        }

        @Test
        @DisplayName("分页查询样本")
        void listWithPageable() {
            // Given
            List<Sample> samples = Arrays.asList(sample);
            Page<Sample> page = new PageImpl<>(samples);
            Pageable pageable = PageRequest.of(0, 10);

            when(sampleRepository.findAll(pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            PageResult<SampleVO> result = sampleService.list(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("根据申请ID查询样本")
        void listByRequestId() {
            // Given
            when(sampleRepository.findByRequestId("request-001")).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listByRequestId("request-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("根据患者ID查询样本")
        void listByPatientId() {
            // Given
            when(sampleRepository.findByPatientId("patient-001")).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listByPatientId("patient-001");

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("patient-001", result.get(0).getPatientId());
        }

        @Test
        @DisplayName("根据状态查询样本")
        void listByStatus() {
            // Given
            when(sampleRepository.findBySampleStatus(SampleStatus.COLLECTED)).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listByStatus(SampleStatus.COLLECTED);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("COLLECTED", result.get(0).getSampleStatus());
        }

        @Test
        @DisplayName("查询待采集样本")
        void listPendingSamples() {
            // Given
            when(sampleRepository.findPendingSamples()).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listPendingSamples();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("查询待核收样本")
        void listCollectedSamples() {
            // Given
            when(sampleRepository.findCollectedSamples()).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listCollectedSamples();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("查询急诊样本")
        void listEmergencySamples() {
            // Given
            when(sampleRepository.findEmergencyCollectedSamples()).thenReturn(Arrays.asList(sample));
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            List<SampleVO> result = sampleService.listEmergencySamples();

            // Then
            assertNotNull(result);
        }

        @Test
        @DisplayName("根据采集时间范围查询")
        void listByCollectionTime() {
            // Given
            LocalDateTime startTime = LocalDateTime.now().minusDays(1);
            LocalDateTime endTime = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Sample> page = new PageImpl<>(Arrays.asList(sample));

            when(sampleRepository.findByCollectionTimeBetween(startTime, endTime, pageable)).thenReturn(page);
            when(testRequestRepository.findById(anyString())).thenReturn(Optional.of(testRequest));

            // When
            PageResult<SampleVO> result = sampleService.listByCollectionTime(startTime, endTime, pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getList().size());
        }
    }

    @Nested
    @DisplayName("更新样本状态测试")
    class UpdateStatusTests {

        @Test
        @DisplayName("成功更新样本状态")
        void updateStatusSuccessfully() {
            // Given
            when(sampleRepository.findById("sample-001")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);

            // When
            SampleVO result = sampleService.updateStatus("sample-001", SampleStatus.TESTING);

            // Then
            assertNotNull(result);
            assertEquals("TESTING", result.getSampleStatus());
        }

        @Test
        @DisplayName("更新状态失败-样本不存在")
        void updateStatusFailedWithNotFound() {
            // Given
            when(sampleRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                () -> sampleService.updateStatus("non-existent", SampleStatus.TESTING));
        }
    }

    @Nested
    @DisplayName("删除样本测试")
    class DeleteSampleTests {

        @Test
        @DisplayName("成功删除样本")
        void deleteSuccessfully() {
            // Given
            when(sampleRepository.findById("sample-001")).thenReturn(Optional.of(sample));
            when(sampleRepository.save(any(Sample.class))).thenReturn(sample);

            // When
            sampleService.delete("sample-001");

            // Then
            verify(sampleRepository).findById("sample-001");
            verify(sampleRepository).save(any(Sample.class));
        }

        @Test
        @DisplayName("删除失败-样本不存在")
        void deleteFailedWithNotFound() {
            // Given
            when(sampleRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> sampleService.delete("non-existent"));
        }
    }

    @Nested
    @DisplayName("统计样本数量测试")
    class CountTests {

        @Test
        @DisplayName("统计某状态的样本数量")
        void countByStatus() {
            // Given
            when(sampleRepository.countBySampleStatus(SampleStatus.COLLECTED)).thenReturn(10L);

            // When
            long count = sampleService.countByStatus(SampleStatus.COLLECTED);

            // Then
            assertEquals(10L, count);
        }
    }

    @Nested
    @DisplayName("样本标签生成测试")
    class LabelGenerationTests {

        @Test
        @DisplayName("成功生成样本标签")
        void generateLabelSuccessfully() {
            // Given
            when(sampleRepository.findById("sample-001")).thenReturn(Optional.of(sample));
            when(testRequestRepository.findById("request-001")).thenReturn(Optional.of(testRequest));

            // When
            String label = sampleService.generateLabel("sample-001");

            // Then
            assertNotNull(label);
            assertTrue(label.contains("样本编号: SMP20260406120000"));
            assertTrue(label.contains("患者: 张三"));
            assertTrue(label.contains("申请单号: LIS20260406120000"));
            assertTrue(label.contains("采集人: 护士王"));
        }

        @Test
        @DisplayName("生成标签失败-样本不存在")
        void generateLabelFailedWithNotFound() {
            // Given
            when(sampleRepository.findById("non-existent")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> sampleService.generateLabel("non-existent"));
        }
    }
}