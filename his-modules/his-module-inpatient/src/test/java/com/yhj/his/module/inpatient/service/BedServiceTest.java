package com.yhj.his.module.inpatient.service;

import com.yhj.his.common.core.domain.ErrorCode;
import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.inpatient.dto.*;
import com.yhj.his.module.inpatient.entity.Bed;
import com.yhj.his.module.inpatient.entity.InpatientAdmission;
import com.yhj.his.module.inpatient.enums.AdmissionStatus;
import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.BedType;
import com.yhj.his.module.inpatient.repository.BedRepository;
import com.yhj.his.module.inpatient.repository.InpatientAdmissionRepository;
import com.yhj.his.module.inpatient.service.impl.BedServiceImpl;
import com.yhj.his.module.inpatient.vo.BedVO;
import com.yhj.his.module.inpatient.vo.WardBedStatisticsVO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 床位管理服务单元测试
 *
 * 测试覆盖范围：
 * - 床位查询
 * - 床位分配
 * - 床位调换
 * - 床位状态更新
 * - 床位释放
 * - 病区床位统计
 */
@ExtendWith(MockitoExtension.class)
class BedServiceTest {

    @Mock
    private BedRepository bedRepository;

    @Mock
    private InpatientAdmissionRepository admissionRepository;

    @InjectMocks
    private BedServiceImpl bedService;

    // ==================== 床位查询测试 ====================

    @Test
    @DisplayName("查询床位列表 - 查询所有床位")
    void testListBeds_AllBeds() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));
        beds.add(createBed("bed-002", "02", BedStatus.OCCUPIED));
        beds.add(createBed("bed-003", "03", BedStatus.MAINTENANCE));

        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId(null);
        queryDTO.setStatus(null);

        when(bedRepository.findAll()).thenReturn(beds);

        // 执行
        List<BedVO> result = bedService.list(queryDTO);

        // 验证
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(bedRepository).findAll();
    }

    @Test
    @DisplayName("查询床位列表 - 按病区查询")
    void testListBeds_ByWardId() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));
        beds.add(createBed("bed-002", "02", BedStatus.OCCUPIED));

        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId("W001");
        queryDTO.setStatus(null);

        when(bedRepository.findByWardId("W001")).thenReturn(beds);

        // 执行
        List<BedVO> result = bedService.list(queryDTO);

        // 验证
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bedRepository).findByWardId("W001");
    }

    @Test
    @DisplayName("查询床位列表 - 按病区和状态查询")
    void testListBeds_ByWardIdAndStatus() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));

        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId("W001");
        queryDTO.setStatus(BedStatus.VACANT);

        when(bedRepository.findByWardIdAndStatus("W001", BedStatus.VACANT)).thenReturn(beds);

        // 执行
        List<BedVO> result = bedService.list(queryDTO);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BedStatus.VACANT, result.get(0).getStatus());
    }

    @Test
    @DisplayName("查询床位列表 - 仅按状态查询")
    void testListBeds_ByStatusOnly() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.OCCUPIED));

        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId(null);
        queryDTO.setStatus(BedStatus.OCCUPIED);

        when(bedRepository.findByStatus(BedStatus.OCCUPIED)).thenReturn(beds);

        // 执行
        List<BedVO> result = bedService.list(queryDTO);

        // 验证
        assertEquals(1, result.size());
        assertEquals(BedStatus.OCCUPIED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("查询床位详情 - 成功查询")
    void testGetBedById_Success() {
        // 准备数据
        Bed bed = createBed("bed-001", "01", BedStatus.VACANT);

        when(bedRepository.findById("bed-001")).thenReturn(Optional.of(bed));

        // 执行
        BedVO result = bedService.getById("bed-001");

        // 验证
        assertNotNull(result);
        assertEquals("bed-001", result.getBedId());
        assertEquals("01", result.getBedNo());
    }

    @Test
    @DisplayName("查询床位详情 - 床位不存在")
    void testGetBedById_NotFound() {
        when(bedRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.getById("invalid-id"));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("床位不存在", exception.getMessage());
    }

    // ==================== 分页查询测试 ====================

    @Test
    @DisplayName("分页查询床位 - 成功分页")
    void testPageBeds_Success() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));
        beds.add(createBed("bed-002", "02", BedStatus.OCCUPIED));

        Page<Bed> page = new PageImpl<>(beds);
        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId(null);

        when(bedRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // 执行
        PageResult<BedVO> result = bedService.page(1, 10, queryDTO);

        // 验证
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("分页查询床位 - 按病区分页")
    void testPageBeds_ByWardId() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));

        Page<Bed> page = new PageImpl<>(beds);
        BedQueryDTO queryDTO = new BedQueryDTO();
        queryDTO.setWardId("W001");

        when(bedRepository.findByWardId(eq("W001"), any(PageRequest.class))).thenReturn(page);

        // 执行
        PageResult<BedVO> result = bedService.page(1, 10, queryDTO);

        // 验证
        assertEquals(1, result.getTotal());
        verify(bedRepository).findByWardId(eq("W001"), any(PageRequest.class));
    }

    // ==================== 床位分配测试 ====================

    @Test
    @DisplayName("床位分配 - 成功分配")
    void testAssignBed_Success() {
        // 准备数据
        BedAssignDTO dto = new BedAssignDTO();
        dto.setAdmissionId("admission-001");
        dto.setWardId("W001");
        dto.setBedNo("01");

        InpatientAdmission admission = createInpatientAdmission();
        admission.setPatientId("P001");
        admission.setPatientName("张三");

        Bed bed = createBed("bed-001", "01", BedStatus.VACANT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), dto.getBedNo())).thenReturn(Optional.of(bed));
        when(bedRepository.save(any(Bed.class))).thenReturn(bed);
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(admission);

        // 执行
        boolean result = bedService.assign(dto);

        // 验证
        assertTrue(result);
        verify(bedRepository).save(any(Bed.class));
        verify(admissionRepository).save(any(InpatientAdmission.class));
    }

    @Test
    @DisplayName("床位分配 - 住院记录不存在")
    void testAssignBed_AdmissionNotFound() {
        BedAssignDTO dto = new BedAssignDTO();
        dto.setAdmissionId("invalid-id");
        dto.setWardId("W001");
        dto.setBedNo("01");

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.assign(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("住院记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("床位分配 - 床位不存在")
    void testAssignBed_BedNotFound() {
        BedAssignDTO dto = new BedAssignDTO();
        dto.setAdmissionId("admission-001");
        dto.setWardId("W001");
        dto.setBedNo("99");

        InpatientAdmission admission = createInpatientAdmission();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), dto.getBedNo())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.assign(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("床位不存在", exception.getMessage());
    }

    @Test
    @DisplayName("床位分配 - 床位状态不允许分配")
    void testAssignBed_BedNotVacant() {
        BedAssignDTO dto = new BedAssignDTO();
        dto.setAdmissionId("admission-001");
        dto.setWardId("W001");
        dto.setBedNo("01");

        InpatientAdmission admission = createInpatientAdmission();
        Bed bed = createBed("bed-001", "01", BedStatus.OCCUPIED); // 床位已被占用

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), dto.getBedNo())).thenReturn(Optional.of(bed));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.assign(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("床位状态不允许分配", exception.getMessage());
    }

    @Test
    @DisplayName("床位分配 - 床位处于维修状态")
    void testAssignBed_BedUnderMaintenance() {
        BedAssignDTO dto = new BedAssignDTO();
        dto.setAdmissionId("admission-001");
        dto.setWardId("W001");
        dto.setBedNo("01");

        InpatientAdmission admission = createInpatientAdmission();
        Bed bed = createBed("bed-001", "01", BedStatus.MAINTENANCE);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByWardIdAndBedNo(dto.getWardId(), dto.getBedNo())).thenReturn(Optional.of(bed));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.assign(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
    }

    // ==================== 床位调换测试 ====================

    @Test
    @DisplayName("床位调换 - 成功调换")
    void testChangeBed_Success() {
        // 准备数据
        BedChangeDTO dto = new BedChangeDTO();
        dto.setAdmissionId("admission-001");
        dto.setNewWardId("W001");
        dto.setNewBedNo("02");

        InpatientAdmission admission = createInpatientAdmission();
        admission.setPatientId("P001");
        admission.setPatientName("张三");

        Bed oldBed = createBed("bed-001", "01", BedStatus.OCCUPIED);
        oldBed.setAdmissionId("admission-001");
        oldBed.setPatientId("P001");
        oldBed.setPatientName("张三");

        Bed newBed = createBed("bed-002", "02", BedStatus.VACANT);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByAdmissionId(dto.getAdmissionId())).thenReturn(Optional.of(oldBed));
        when(bedRepository.findByWardIdAndBedNo(dto.getNewWardId(), dto.getNewBedNo())).thenReturn(Optional.of(newBed));
        when(bedRepository.save(any(Bed.class))).thenReturn(oldBed);
        when(admissionRepository.save(any(InpatientAdmission.class))).thenReturn(admission);

        // 执行
        boolean result = bedService.change(dto);

        // 验证
        assertTrue(result);
        // 验证原床位被释放
        ArgumentCaptor<Bed> bedCaptor = ArgumentCaptor.forClass(Bed.class);
        verify(bedRepository, times(2)).save(bedCaptor.capture());

        List<Bed> savedBeds = bedCaptor.getAllValues();
        assertEquals(BedStatus.VACANT, savedBeds.get(0).getStatus());
        assertNull(savedBeds.get(0).getAdmissionId());
        assertEquals(BedStatus.OCCUPIED, savedBeds.get(1).getStatus());
    }

    @Test
    @DisplayName("床位调换 - 住院记录不存在")
    void testChangeBed_AdmissionNotFound() {
        BedChangeDTO dto = new BedChangeDTO();
        dto.setAdmissionId("invalid-id");

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.change(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("住院记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("床位调换 - 原床位不存在")
    void testChangeBed_OldBedNotFound() {
        BedChangeDTO dto = new BedChangeDTO();
        dto.setAdmissionId("admission-001");
        dto.setNewWardId("W001");
        dto.setNewBedNo("02");

        InpatientAdmission admission = createInpatientAdmission();

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByAdmissionId(dto.getAdmissionId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.change(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("原床位记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("床位调换 - 新床位不存在")
    void testChangeBed_NewBedNotFound() {
        BedChangeDTO dto = new BedChangeDTO();
        dto.setAdmissionId("admission-001");
        dto.setNewWardId("W001");
        dto.setNewBedNo("99");

        InpatientAdmission admission = createInpatientAdmission();
        Bed oldBed = createBed("bed-001", "01", BedStatus.OCCUPIED);

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByAdmissionId(dto.getAdmissionId())).thenReturn(Optional.of(oldBed));
        when(bedRepository.findByWardIdAndBedNo(dto.getNewWardId(), dto.getNewBedNo())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.change(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
        assertEquals("新床位不存在", exception.getMessage());
    }

    @Test
    @DisplayName("床位调换 - 新床位状态不允许分配")
    void testChangeBed_NewBedNotVacant() {
        BedChangeDTO dto = new BedChangeDTO();
        dto.setAdmissionId("admission-001");
        dto.setNewWardId("W001");
        dto.setNewBedNo("02");

        InpatientAdmission admission = createInpatientAdmission();
        Bed oldBed = createBed("bed-001", "01", BedStatus.OCCUPIED);
        Bed newBed = createBed("bed-002", "02", BedStatus.OCCUPIED); // 新床位也被占用

        when(admissionRepository.findById(dto.getAdmissionId())).thenReturn(Optional.of(admission));
        when(bedRepository.findByAdmissionId(dto.getAdmissionId())).thenReturn(Optional.of(oldBed));
        when(bedRepository.findByWardIdAndBedNo(dto.getNewWardId(), dto.getNewBedNo())).thenReturn(Optional.of(newBed));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.change(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("新床位状态不允许分配", exception.getMessage());
    }

    // ==================== 床位状态更新测试 ====================

    @Test
    @DisplayName("床位状态更新 - 成功更新空床状态")
    void testUpdateBedStatus_VacantBed() {
        // 准备数据
        BedStatusUpdateDTO dto = new BedStatusUpdateDTO();
        dto.setBedId("bed-001");
        dto.setNewStatus("MAINTENANCE");
        dto.setReason("设备维修");

        Bed bed = createBed("bed-001", "01", BedStatus.VACANT);

        when(bedRepository.findById(dto.getBedId())).thenReturn(Optional.of(bed));
        when(bedRepository.save(any(Bed.class))).thenReturn(bed);

        // 执行
        boolean result = bedService.updateStatus(dto);

        // 验证
        assertTrue(result);
        ArgumentCaptor<Bed> bedCaptor = ArgumentCaptor.forClass(Bed.class);
        verify(bedRepository).save(bedCaptor.capture());
        assertEquals(BedStatus.MAINTENANCE, bedCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("床位状态更新 - 占用床位不允许变更")
    void testUpdateBedStatus_OccupiedBedNotAllowed() {
        // 准备数据
        BedStatusUpdateDTO dto = new BedStatusUpdateDTO();
        dto.setBedId("bed-001");
        dto.setNewStatus("VACANT"); // 尝试将占用床位变为空床

        Bed bed = createBed("bed-001", "01", BedStatus.OCCUPIED);
        bed.setAdmissionId("admission-001");
        bed.setPatientId("P001");
        bed.setPatientName("张三");

        when(bedRepository.findById(dto.getBedId())).thenReturn(Optional.of(bed));

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.updateStatus(dto));

        assertEquals(ErrorCode.OPERATION_NOT_ALLOWED.getCode(), exception.getCode());
        assertEquals("床位正在使用，不允许变更状态", exception.getMessage());
    }

    @Test
    @DisplayName("床位状态更新 - 床位不存在")
    void testUpdateBedStatus_BedNotFound() {
        BedStatusUpdateDTO dto = new BedStatusUpdateDTO();
        dto.setBedId("invalid-id");
        dto.setNewStatus("VACANT");

        when(bedRepository.findById(dto.getBedId())).thenReturn(Optional.empty());

        // 执行并验证异常
        BusinessException exception = assertThrows(BusinessException.class,
                () -> bedService.updateStatus(dto));

        assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), exception.getCode());
    }

    // ==================== 床位释放测试 ====================

    @Test
    @DisplayName("床位释放 - 成功释放")
    void testReleaseBed_Success() {
        // 准备数据
        Bed bed = createBed("bed-001", "01", BedStatus.OCCUPIED);
        bed.setAdmissionId("admission-001");
        bed.setPatientId("P001");
        bed.setPatientName("张三");

        when(bedRepository.findByAdmissionId("admission-001")).thenReturn(Optional.of(bed));
        when(bedRepository.save(any(Bed.class))).thenReturn(bed);

        // 执行
        boolean result = bedService.release("admission-001");

        // 验证
        assertTrue(result);
        ArgumentCaptor<Bed> bedCaptor = ArgumentCaptor.forClass(Bed.class);
        verify(bedRepository).save(bedCaptor.capture());
        assertEquals(BedStatus.VACANT, bedCaptor.getValue().getStatus());
        assertNull(bedCaptor.getValue().getAdmissionId());
        assertNull(bedCaptor.getValue().getPatientId());
        assertNull(bedCaptor.getValue().getPatientName());
    }

    @Test
    @DisplayName("床位释放 - 无床位关联（出院时无床位）")
    void testReleaseBed_NoBedAssociation() {
        when(bedRepository.findByAdmissionId("admission-001")).thenReturn(Optional.empty());

        // 执行
        boolean result = bedService.release("admission-001");

        // 验证
        assertTrue(result);
        verify(bedRepository, never()).save(any());
    }

    // ==================== 病区床位统计测试 ====================

    @Test
    @DisplayName("病区床位统计 - 成功统计")
    void testGetStatistics_Success() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));
        beds.add(createBed("bed-002", "02", BedStatus.OCCUPIED));
        beds.add(createBed("bed-003", "03", BedStatus.OCCUPIED));
        beds.add(createBed("bed-004", "04", BedStatus.RESERVED));
        beds.add(createBed("bed-005", "05", BedStatus.MAINTENANCE));

        when(bedRepository.findByWardId("W001")).thenReturn(beds);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.VACANT)).thenReturn(1L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.OCCUPIED)).thenReturn(2L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.RESERVED)).thenReturn(1L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.MAINTENANCE)).thenReturn(1L);

        // 执行
        WardBedStatisticsVO result = bedService.getStatistics("W001");

        // 验证
        assertNotNull(result);
        assertEquals("W001", result.getWardId());
        assertEquals(5L, result.getTotal());
        assertEquals(1L, result.getVacant());
        assertEquals(2L, result.getOccupied());
        assertEquals(1L, result.getReserved());
        assertEquals(1L, result.getMaintenance());
        assertEquals(40.0, result.getUtilizationRate()); // 2/5 * 100 = 40%
    }

    @Test
    @DisplayName("病区床位统计 - 空病区")
    void testGetStatistics_EmptyWard() {
        when(bedRepository.findByWardId("W002")).thenReturn(Collections.emptyList());

        // 执行
        WardBedStatisticsVO result = bedService.getStatistics("W002");

        // 验证
        assertNotNull(result);
        assertEquals("W002", result.getWardId());
        assertEquals(0L, result.getTotal());
        assertNull(result.getWardName());
        // 空病区时使用率为null（因为总床数为0，无法计算使用率）
        assertNull(result.getUtilizationRate());
    }

    @Test
    @DisplayName("病区床位统计 - 所有床位都空闲")
    void testGetStatistics_AllVacant() {
        // 准备数据
        List<Bed> beds = new ArrayList<>();
        beds.add(createBed("bed-001", "01", BedStatus.VACANT));
        beds.add(createBed("bed-002", "02", BedStatus.VACANT));

        when(bedRepository.findByWardId("W001")).thenReturn(beds);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.VACANT)).thenReturn(2L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.OCCUPIED)).thenReturn(0L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.RESERVED)).thenReturn(0L);
        when(bedRepository.countByWardIdAndStatus("W001", BedStatus.MAINTENANCE)).thenReturn(0L);

        // 执行
        WardBedStatisticsVO result = bedService.getStatistics("W001");

        // 验证
        assertEquals(0.0, result.getUtilizationRate());
    }

    // ==================== 辅助方法 ====================

    private Bed createBed(String id, String bedNo, BedStatus status) {
        Bed bed = new Bed();
        bed.setId(id);
        bed.setBedNo(bedNo);
        bed.setWardId("W001");
        bed.setWardName("内科一病区");
        bed.setRoomNo("101");
        bed.setBedType(BedType.NORMAL);
        bed.setBedLevel("普通床位");
        bed.setDailyRate(new BigDecimal("50.00"));
        bed.setStatus(status);
        bed.setFacilities("[\"电视\", \"空调\"]");
        return bed;
    }

    private InpatientAdmission createInpatientAdmission() {
        InpatientAdmission admission = new InpatientAdmission();
        admission.setId("admission-001");
        admission.setAdmissionNo("ZY20260406001");
        admission.setPatientId("P001");
        admission.setPatientName("张三");
        admission.setDeptId("D001");
        admission.setDeptName("内科");
        admission.setWardId("W001");
        admission.setWardName("内科一病区");
        admission.setStatus(AdmissionStatus.IN_HOSPITAL);
        return admission;
    }
}