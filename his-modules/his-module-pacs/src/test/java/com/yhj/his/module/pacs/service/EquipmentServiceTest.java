package com.yhj.his.module.pacs.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.common.core.exception.BusinessException;
import com.yhj.his.module.pacs.dto.EquipmentDTO;
import com.yhj.his.module.pacs.dto.RoomScheduleDTO;
import com.yhj.his.module.pacs.entity.EquipmentInfo;
import com.yhj.his.module.pacs.entity.RoomSchedule;
import com.yhj.his.module.pacs.repository.EquipmentInfoRepository;
import com.yhj.his.module.pacs.repository.RoomScheduleRepository;
import com.yhj.his.module.pacs.service.impl.EquipmentServiceImpl;
import com.yhj.his.module.pacs.vo.EquipmentVO;
import com.yhj.his.module.pacs.vo.RoomScheduleVO;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * EquipmentService Unit Tests
 * Tests for equipment status management and room schedule management
 */
@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentInfoRepository equipmentInfoRepository;

    @Mock
    private RoomScheduleRepository roomScheduleRepository;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private EquipmentDTO equipmentDTO;
    private EquipmentInfo equipmentInfo;
    private RoomScheduleDTO roomScheduleDTO;
    private RoomSchedule roomSchedule;

    @BeforeEach
    void setUp() {
        // Setup EquipmentInfo
        equipmentInfo = new EquipmentInfo();
        equipmentInfo.setId("equip-001");
        equipmentInfo.setEquipmentCode("CT001");
        equipmentInfo.setEquipmentName("CT设备1");
        equipmentInfo.setEquipmentType("CT");
        equipmentInfo.setModel("Optima CT660");
        equipmentInfo.setManufacturer("GE");
        equipmentInfo.setSerialNumber("SN123456");
        equipmentInfo.setAeTitle("CT001_AE");
        equipmentInfo.setIpAddress("192.168.1.100");
        equipmentInfo.setPort(104);
        equipmentInfo.setRoomNo("R001");
        equipmentInfo.setRoomName("CT机房1");
        equipmentInfo.setPurchaseDate("2025-01-01");
        equipmentInfo.setEnableDate("2025-03-01");
        equipmentInfo.setStatus("正常");
        equipmentInfo.setManagerId("M001");
        equipmentInfo.setManagerName("管理员张");
        equipmentInfo.setSortOrder(1);

        // Setup EquipmentDTO
        equipmentDTO = new EquipmentDTO();
        equipmentDTO.setEquipmentCode("CT001");
        equipmentDTO.setEquipmentName("CT设备1");
        equipmentDTO.setEquipmentType("CT");
        equipmentDTO.setModel("Optima CT660");
        equipmentDTO.setManufacturer("GE");
        equipmentDTO.setSerialNumber("SN123456");
        equipmentDTO.setAeTitle("CT001_AE");
        equipmentDTO.setIpAddress("192.168.1.100");
        equipmentDTO.setPort(104);
        equipmentDTO.setRoomNo("R001");
        equipmentDTO.setRoomName("CT机房1");
        equipmentDTO.setStatus("正常");

        // Setup RoomSchedule
        roomSchedule = new RoomSchedule();
        roomSchedule.setId("schedule-001");
        roomSchedule.setRoomNo("R001");
        roomSchedule.setRoomName("CT机房1");
        roomSchedule.setEquipmentId("equip-001");
        roomSchedule.setEquipmentName("CT设备1");
        roomSchedule.setScheduleDate(LocalDate.of(2026, 4, 7));
        roomSchedule.setShift("上午");
        roomSchedule.setStartTime(LocalTime.of(8, 0));
        roomSchedule.setEndTime(LocalTime.of(12, 0));
        roomSchedule.setTotalQuota(10);
        roomSchedule.setScheduledCount(3);
        roomSchedule.setAvailableQuota(7);
        roomSchedule.setExamTypeLimit("CT");
        roomSchedule.setDoctorId("DOC001");
        roomSchedule.setDoctorName("医生李");
        roomSchedule.setTechnicianId("TECH001");
        roomSchedule.setTechnicianName("技师张");
        roomSchedule.setStatus("开放");

        // Setup RoomScheduleDTO
        roomScheduleDTO = new RoomScheduleDTO();
        roomScheduleDTO.setRoomNo("R001");
        roomScheduleDTO.setRoomName("CT机房1");
        roomScheduleDTO.setEquipmentId("equip-001");
        roomScheduleDTO.setEquipmentName("CT设备1");
        roomScheduleDTO.setScheduleDate(LocalDate.of(2026, 4, 7));
        roomScheduleDTO.setShift("上午");
        roomScheduleDTO.setStartTime(LocalTime.of(8, 0));
        roomScheduleDTO.setEndTime(LocalTime.of(12, 0));
        roomScheduleDTO.setTotalQuota(10);
        roomScheduleDTO.setExamTypeLimit("CT");
        roomScheduleDTO.setDoctorId("DOC001");
        roomScheduleDTO.setDoctorName("医生李");
        roomScheduleDTO.setTechnicianId("TECH001");
        roomScheduleDTO.setTechnicianName("技师张");
        roomScheduleDTO.setStatus("开放");
    }

    @Nested
    @DisplayName("Create Equipment Tests")
    class CreateEquipmentTests {

        @Test
        @DisplayName("Should create equipment successfully")
        void createEquipment_Success() {
            // Arrange
            when(equipmentInfoRepository.findByEquipmentCode(anyString())).thenReturn(Optional.empty());
            when(equipmentInfoRepository.save(any(EquipmentInfo.class))).thenAnswer(invocation -> {
                EquipmentInfo saved = invocation.getArgument(0);
                saved.setId("new-equip-id");
                return saved;
            });

            // Act
            EquipmentVO result = equipmentService.createEquipment(equipmentDTO);

            // Assert
            assertNotNull(result);
            assertEquals("new-equip-id", result.getId());
            assertEquals("CT001", result.getEquipmentCode());
            assertEquals("CT设备1", result.getEquipmentName());
            assertEquals("CT", result.getEquipmentType());
            assertEquals("正常", result.getStatus());

            verify(equipmentInfoRepository).findByEquipmentCode("CT001");
            verify(equipmentInfoRepository).save(any(EquipmentInfo.class));
        }

        @Test
        @DisplayName("Should throw exception when equipment code already exists")
        void createEquipment_CodeExists() {
            // Arrange
            when(equipmentInfoRepository.findByEquipmentCode(anyString())).thenReturn(Optional.of(equipmentInfo));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.createEquipment(equipmentDTO));

            assertEquals("设备编码已存在", exception.getMessage());
            verify(equipmentInfoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create equipment without code check when code is null")
        void createEquipment_NullCode() {
            // Arrange
            equipmentDTO.setEquipmentCode(null);
            when(equipmentInfoRepository.save(any(EquipmentInfo.class))).thenAnswer(invocation -> {
                EquipmentInfo saved = invocation.getArgument(0);
                saved.setId("new-equip-id");
                return saved;
            });

            // Act
            EquipmentVO result = equipmentService.createEquipment(equipmentDTO);

            // Assert
            assertNotNull(result);
            verify(equipmentInfoRepository, never()).findByEquipmentCode(anyString());
        }
    }

    @Nested
    @DisplayName("Update Equipment Tests")
    class UpdateEquipmentTests {

        @Test
        @DisplayName("Should update equipment successfully")
        void updateEquipment_Success() {
            // Arrange
            equipmentDTO.setId("equip-001");
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.of(equipmentInfo));
            when(equipmentInfoRepository.save(any(EquipmentInfo.class))).thenAnswer(invocation -> {
                EquipmentInfo saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            EquipmentVO result = equipmentService.updateEquipment(equipmentDTO);

            // Assert
            assertNotNull(result);
            assertEquals("equip-001", result.getId());
            assertEquals("CT设备1", result.getEquipmentName());

            verify(equipmentInfoRepository).findById("equip-001");
            verify(equipmentInfoRepository).save(any(EquipmentInfo.class));
        }

        @Test
        @DisplayName("Should throw exception when equipment not found for update")
        void updateEquipment_NotFound() {
            // Arrange
            equipmentDTO.setId("non-existent-id");
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.updateEquipment(equipmentDTO));

            assertEquals("设备不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Equipment Tests")
    class DeleteEquipmentTests {

        @Test
        @DisplayName("Should delete equipment successfully")
        void deleteEquipment_Success() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.of(equipmentInfo));
            doNothing().when(equipmentInfoRepository).delete(any(EquipmentInfo.class));

            // Act
            equipmentService.deleteEquipment("equip-001");

            // Assert
            verify(equipmentInfoRepository).findById("equip-001");
            verify(equipmentInfoRepository).delete(equipmentInfo);
        }

        @Test
        @DisplayName("Should throw exception when equipment not found for deletion")
        void deleteEquipment_NotFound() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.deleteEquipment("non-existent-id"));

            assertEquals("设备不存在", exception.getMessage());
            verify(equipmentInfoRepository, never()).deleteById(anyString());
        }
    }

    @Nested
    @DisplayName("Get Equipment Tests")
    class GetEquipmentTests {

        @Test
        @DisplayName("Should get equipment by ID successfully")
        void getEquipmentById_Success() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.of(equipmentInfo));

            // Act
            EquipmentVO result = equipmentService.getEquipmentById("equip-001");

            // Assert
            assertNotNull(result);
            assertEquals("equip-001", result.getId());
            assertEquals("CT001", result.getEquipmentCode());
            assertEquals("CT设备1", result.getEquipmentName());
        }

        @Test
        @DisplayName("Should throw exception when equipment not found by ID")
        void getEquipmentById_NotFound() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.getEquipmentById("non-existent-id"));

            assertEquals("设备不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get equipment by code successfully")
        void getEquipmentByCode_Success() {
            // Arrange
            when(equipmentInfoRepository.findByEquipmentCode(anyString())).thenReturn(Optional.of(equipmentInfo));

            // Act
            EquipmentVO result = equipmentService.getEquipmentByCode("CT001");

            // Assert
            assertNotNull(result);
            assertEquals("CT001", result.getEquipmentCode());
        }

        @Test
        @DisplayName("Should throw exception when equipment not found by code")
        void getEquipmentByCode_NotFound() {
            // Arrange
            when(equipmentInfoRepository.findByEquipmentCode(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.getEquipmentByCode("non-existent-code"));

            assertEquals("设备不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get equipment by room number")
        void getEquipmentByRoomNo_Success() {
            // Arrange
            when(equipmentInfoRepository.findByRoomNo(anyString())).thenReturn(Optional.of(equipmentInfo));

            // Act
            EquipmentVO result = equipmentService.getEquipmentByRoomNo("R001");

            // Assert
            assertNotNull(result);
            assertEquals("R001", result.getRoomNo());
        }

        @Test
        @DisplayName("Should return null when no equipment found for room number")
        void getEquipmentByRoomNo_NotFound() {
            // Arrange
            when(equipmentInfoRepository.findByRoomNo(anyString())).thenReturn(Optional.empty());

            // Act
            EquipmentVO result = equipmentService.getEquipmentByRoomNo("R999");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should get equipment by type")
        void getEquipmentByType_Success() {
            // Arrange
            when(equipmentInfoRepository.findByEquipmentType(anyString())).thenReturn(List.of(equipmentInfo));

            // Act
            List<EquipmentVO> results = equipmentService.getEquipmentByType("CT");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("CT", results.get(0).getEquipmentType());
        }

        @Test
        @DisplayName("Should get normal equipment")
        void getNormalEquipment_Success() {
            // Arrange
            when(equipmentInfoRepository.findNormalEquipment()).thenReturn(List.of(equipmentInfo));

            // Act
            List<EquipmentVO> results = equipmentService.getNormalEquipment();

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("正常", results.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Query Equipment Tests")
    class QueryEquipmentTests {

        @Test
        @DisplayName("Should query equipment with pagination")
        void queryEquipment_Success() {
            // Arrange
            Page<EquipmentInfo> page = new PageImpl<>(List.of(equipmentInfo));
            when(equipmentInfoRepository.findByConditions(
                    any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<EquipmentVO> result = equipmentService.queryEquipment(
                    null, null, "CT", "正常", "R001", 1, 10);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should return empty page when no equipment found")
        void queryEquipment_EmptyPage() {
            // Arrange
            Page<EquipmentInfo> page = new PageImpl<>(Collections.emptyList());
            when(equipmentInfoRepository.findByConditions(
                    any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<EquipmentVO> result = equipmentService.queryEquipment(
                    null, null, null, null, null, 1, 10);

            // Assert
            assertNotNull(result);
            assertTrue(result.getList().isEmpty());
            assertEquals(0L, result.getTotal());
        }
    }

    @Nested
    @DisplayName("Update Equipment Status Tests")
    class UpdateEquipmentStatusTests {

        @Test
        @DisplayName("Should update equipment status to maintenance")
        void updateEquipmentStatus_Maintenance() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.of(equipmentInfo));
            when(equipmentInfoRepository.save(any(EquipmentInfo.class))).thenAnswer(invocation -> {
                EquipmentInfo saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            EquipmentVO result = equipmentService.updateEquipmentStatus("equip-001", "维护");

            // Assert
            assertNotNull(result);
            assertEquals("维护", result.getStatus());
        }

        @Test
        @DisplayName("Should update equipment status to offline")
        void updateEquipmentStatus_Offline() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.of(equipmentInfo));
            when(equipmentInfoRepository.save(any(EquipmentInfo.class))).thenAnswer(invocation -> {
                EquipmentInfo saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            EquipmentVO result = equipmentService.updateEquipmentStatus("equip-001", "离线");

            // Assert
            assertNotNull(result);
            assertEquals("离线", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when equipment not found for status update")
        void updateEquipmentStatus_NotFound() {
            // Arrange
            when(equipmentInfoRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.updateEquipmentStatus("non-existent-id", "维护"));

            assertEquals("设备不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Create Schedule Tests")
    class CreateScheduleTests {

        @Test
        @DisplayName("Should create schedule successfully")
        void createSchedule_Success() {
            // Arrange
            when(roomScheduleRepository.save(any(RoomSchedule.class))).thenAnswer(invocation -> {
                RoomSchedule saved = invocation.getArgument(0);
                saved.setId("new-schedule-id");
                return saved;
            });

            // Act
            RoomScheduleVO result = equipmentService.createSchedule(roomScheduleDTO);

            // Assert
            assertNotNull(result);
            assertEquals("new-schedule-id", result.getId());
            assertEquals("R001", result.getRoomNo());
            assertEquals(LocalDate.of(2026, 4, 7), result.getScheduleDate());
            assertEquals(10, result.getTotalQuota());
            assertEquals(0, result.getScheduledCount());
            assertEquals(10, result.getAvailableQuota());
            assertEquals("开放", result.getStatus());

            verify(roomScheduleRepository).save(any(RoomSchedule.class));
        }
    }

    @Nested
    @DisplayName("Update Schedule Tests")
    class UpdateScheduleTests {

        @Test
        @DisplayName("Should update schedule successfully")
        void updateSchedule_Success() {
            // Arrange
            roomScheduleDTO.setId("schedule-001");
            roomSchedule.setScheduledCount(3);

            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));
            when(roomScheduleRepository.save(any(RoomSchedule.class))).thenAnswer(invocation -> {
                RoomSchedule saved = invocation.getArgument(0);
                return saved;
            });

            // Act
            RoomScheduleVO result = equipmentService.updateSchedule(roomScheduleDTO);

            // Assert
            assertNotNull(result);
            assertEquals("schedule-001", result.getId());
            assertEquals(10, result.getTotalQuota());
            assertEquals(7, result.getAvailableQuota()); // 10 - 3 = 7

            verify(roomScheduleRepository).findById("schedule-001");
            verify(roomScheduleRepository).save(any(RoomSchedule.class));
        }

        @Test
        @DisplayName("Should throw exception when schedule not found for update")
        void updateSchedule_NotFound() {
            // Arrange
            roomScheduleDTO.setId("non-existent-id");
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.updateSchedule(roomScheduleDTO));

            assertEquals("排班不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Schedule Tests")
    class DeleteScheduleTests {

        @Test
        @DisplayName("Should delete schedule successfully when no appointments")
        void deleteSchedule_Success() {
            // Arrange
            roomSchedule.setScheduledCount(0);
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));
            doNothing().when(roomScheduleRepository).delete(any(RoomSchedule.class));

            // Act
            equipmentService.deleteSchedule("schedule-001");

            // Assert
            verify(roomScheduleRepository).findById("schedule-001");
            verify(roomScheduleRepository).delete(roomSchedule);
        }

        @Test
        @DisplayName("Should throw exception when schedule has appointments")
        void deleteSchedule_HasAppointments() {
            // Arrange
            roomSchedule.setScheduledCount(5);
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.deleteSchedule("schedule-001"));

            assertEquals("排班已有预约，无法删除", exception.getMessage());
            verify(roomScheduleRepository, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("Should throw exception when schedule not found for deletion")
        void deleteSchedule_NotFound() {
            // Arrange
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.deleteSchedule("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Schedule Tests")
    class GetScheduleTests {

        @Test
        @DisplayName("Should get schedule by ID successfully")
        void getScheduleById_Success() {
            // Arrange
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));

            // Act
            RoomScheduleVO result = equipmentService.getScheduleById("schedule-001");

            // Assert
            assertNotNull(result);
            assertEquals("schedule-001", result.getId());
            assertEquals("R001", result.getRoomNo());
        }

        @Test
        @DisplayName("Should throw exception when schedule not found")
        void getScheduleById_NotFound() {
            // Arrange
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.getScheduleById("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should get schedules by room number")
        void getSchedulesByRoomNo_Success() {
            // Arrange
            when(roomScheduleRepository.findByRoomNo(anyString())).thenReturn(List.of(roomSchedule));

            // Act
            List<RoomScheduleVO> results = equipmentService.getSchedulesByRoomNo("R001");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("R001", results.get(0).getRoomNo());
        }

        @Test
        @DisplayName("Should get schedules by date")
        void getSchedulesByDate_Success() {
            // Arrange
            when(roomScheduleRepository.findByScheduleDate(any(LocalDate.class))).thenReturn(List.of(roomSchedule));

            // Act
            List<RoomScheduleVO> results = equipmentService.getSchedulesByDate("2026-04-07");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals(LocalDate.of(2026, 4, 7), results.get(0).getScheduleDate());
        }

        @Test
        @DisplayName("Should get available schedules")
        void getAvailableSchedules_Success() {
            // Arrange
            when(roomScheduleRepository.findAvailableByDateAndExamType(any(LocalDate.class), anyString()))
                    .thenReturn(List.of(roomSchedule));

            // Act
            List<RoomScheduleVO> results = equipmentService.getAvailableSchedules("2026-04-07", "CT");

            // Assert
            assertNotNull(results);
            assertEquals(1, results.size());
            assertEquals("开放", results.get(0).getStatus());
            assertEquals(7, results.get(0).getAvailableQuota());
        }
    }

    @Nested
    @DisplayName("Query Schedules Tests")
    class QuerySchedulesTests {

        @Test
        @DisplayName("Should query schedules with pagination")
        void querySchedules_Success() {
            // Arrange
            Page<RoomSchedule> page = new PageImpl<>(List.of(roomSchedule));
            when(roomScheduleRepository.findByConditions(
                    any(), any(LocalDate.class), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<RoomScheduleVO> result = equipmentService.querySchedules(
                    "R001", "2026-04-07", "上午", "开放", 1, 10);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getList().size());
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("Should query schedules with null date")
        void querySchedules_NullDate() {
            // Arrange
            Page<RoomSchedule> page = new PageImpl<>(List.of(roomSchedule));
            when(roomScheduleRepository.findByConditions(
                    any(), eq(null), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            // Act
            PageResult<RoomScheduleVO> result = equipmentService.querySchedules(
                    "R001", null, "上午", "开放", 1, 10);

            // Assert
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Open/Close Schedule Tests")
    class OpenCloseScheduleTests {

        @Test
        @DisplayName("Should open schedule successfully")
        void openSchedule_Success() {
            // Arrange
            roomSchedule.setStatus("关闭");
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));
            when(roomScheduleRepository.save(any(RoomSchedule.class))).thenAnswer(invocation -> {
                RoomSchedule saved = invocation.getArgument(0);
                saved.setStatus("开放");
                return saved;
            });

            // Act
            RoomScheduleVO result = equipmentService.openSchedule("schedule-001");

            // Assert
            assertNotNull(result);
            assertEquals("开放", result.getStatus());
        }

        @Test
        @DisplayName("Should close schedule successfully")
        void closeSchedule_Success() {
            // Arrange
            roomSchedule.setStatus("开放");
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.of(roomSchedule));
            when(roomScheduleRepository.save(any(RoomSchedule.class))).thenAnswer(invocation -> {
                RoomSchedule saved = invocation.getArgument(0);
                saved.setStatus("关闭");
                return saved;
            });

            // Act
            RoomScheduleVO result = equipmentService.closeSchedule("schedule-001");

            // Assert
            assertNotNull(result);
            assertEquals("关闭", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when schedule not found for open")
        void openSchedule_NotFound() {
            // Arrange
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.openSchedule("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when schedule not found for close")
        void closeSchedule_NotFound() {
            // Arrange
            when(roomScheduleRepository.findById(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> equipmentService.closeSchedule("non-existent-id"));

            assertEquals("排班不存在", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Batch Create Schedules Tests")
    class BatchCreateSchedulesTests {

        @Test
        @DisplayName("Should batch create schedules successfully")
        void batchCreateSchedules_Success() {
            // Arrange
            RoomScheduleDTO dto2 = new RoomScheduleDTO();
            dto2.setRoomNo("R002");
            dto2.setScheduleDate(LocalDate.of(2026, 4, 8));
            dto2.setShift("下午");
            dto2.setStartTime(LocalTime.of(14, 0));
            dto2.setEndTime(LocalTime.of(18, 0));
            dto2.setTotalQuota(15);

            List<RoomScheduleDTO> dtoList = List.of(roomScheduleDTO, dto2);

            when(roomScheduleRepository.save(any(RoomSchedule.class))).thenAnswer(invocation -> {
                RoomSchedule saved = invocation.getArgument(0);
                saved.setId("schedule-" + System.currentTimeMillis());
                return saved;
            });

            // Act
            List<RoomScheduleVO> results = equipmentService.batchCreateSchedules(dtoList);

            // Assert
            assertNotNull(results);
            assertEquals(2, results.size());
            verify(roomScheduleRepository, times(2)).save(any(RoomSchedule.class));
        }

        @Test
        @DisplayName("Should return empty list when input is empty")
        void batchCreateSchedules_EmptyList() {
            // Arrange
            List<RoomScheduleDTO> dtoList = Collections.emptyList();

            // Act
            List<RoomScheduleVO> results = equipmentService.batchCreateSchedules(dtoList);

            // Assert
            assertNotNull(results);
            assertTrue(results.isEmpty());
            verify(roomScheduleRepository, never()).save(any());
        }
    }
}