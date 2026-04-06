package com.yhj.his.module.emr.service;

import com.yhj.his.module.emr.dto.EmrTemplateSaveDTO;
import com.yhj.his.module.emr.entity.EmrTemplate;
import com.yhj.his.module.emr.enums.TemplateType;
import com.yhj.his.module.emr.repository.EmrTemplateRepository;
import com.yhj.his.module.emr.service.impl.EmrTemplateServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 病历模板服务单元测试
 * 覆盖模板管理核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmrTemplateService单元测试")
class EmrTemplateServiceTest {

    @Mock
    private EmrTemplateRepository templateRepository;

    @InjectMocks
    private EmrTemplateServiceImpl templateService;

    private EmrTemplate testTemplate;
    private EmrTemplateSaveDTO testSaveDTO;

    @BeforeEach
    void setUp() {
        testTemplate = createTestTemplate();
        testSaveDTO = createTestSaveDTO();
    }

    @Nested
    @DisplayName("模板创建测试")
    class CreateTemplateTests {

        @Test
        @DisplayName("成功创建模板 - 基本信息完整")
        void createTemplate_success() {
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplate result = templateService.createTemplate(testSaveDTO);

            assertNotNull(result);
            assertEquals("门诊初诊模板", result.getTemplateName());
            assertEquals(TemplateType.OUTPATIENT_FIRST, result.getTemplateType());
            assertEquals("内科", result.getCategory());
            assertEquals("DEPT001", result.getDeptId());
            assertEquals("DOC001", result.getCreatorId());
            assertTrue(result.getIsEnabled());
            assertFalse(result.getIsPublic());
            assertEquals(0, result.getUseCount());

            verify(templateRepository).save(any(EmrTemplate.class));
        }

        @Test
        @DisplayName("创建模板 - 设置公开状态")
        void createTemplate_withPublicTrue() {
            testSaveDTO.setIsPublic(true);
            when(templateRepository.save(any(EmrTemplate.class))).thenAnswer(invocation -> {
                EmrTemplate saved = invocation.getArgument(0);
                saved.setId("TEMPLATE001");
                return saved;
            });

            EmrTemplate result = templateService.createTemplate(testSaveDTO);

            assertTrue(result.getIsPublic());
            verify(templateRepository).save(any(EmrTemplate.class));
        }

        @Test
        @DisplayName("创建模板 - 默认启用状态")
        void createTemplate_defaultEnabled() {
            testSaveDTO.setIsEnabled(null);
            when(templateRepository.save(any(EmrTemplate.class))).thenAnswer(invocation -> {
                EmrTemplate saved = invocation.getArgument(0);
                saved.setId("TEMPLATE001");
                return saved;
            });

            EmrTemplate result = templateService.createTemplate(testSaveDTO);

            assertTrue(result.getIsEnabled());
        }

        @Test
        @DisplayName("创建模板 - 默认非公开")
        void createTemplate_defaultNotPublic() {
            testSaveDTO.setIsPublic(null);
            when(templateRepository.save(any(EmrTemplate.class))).thenAnswer(invocation -> {
                EmrTemplate saved = invocation.getArgument(0);
                return saved;
            });

            EmrTemplate result = templateService.createTemplate(testSaveDTO);

            assertFalse(result.getIsPublic());
        }
    }

    @Nested
    @DisplayName("模板更新测试")
    class UpdateTemplateTests {

        @Test
        @DisplayName("成功更新模板")
        void updateTemplate_success() {
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplateSaveDTO updateDTO = new EmrTemplateSaveDTO();
            updateDTO.setTemplateName("更新后的模板");
            updateDTO.setCategory("外科");
            updateDTO.setTemplateContent("新的模板内容");
            updateDTO.setDeptId("DEPT002");
            updateDTO.setDeptName("外科");
            updateDTO.setIsPublic(true);
            updateDTO.setIsEnabled(false);

            EmrTemplate result = templateService.updateTemplate("TEMPLATE001", updateDTO);

            assertEquals("更新后的模板", result.getTemplateName());
            assertEquals("外科", result.getCategory());
            assertTrue(result.getIsPublic());
            assertFalse(result.getIsEnabled());

            verify(templateRepository).findById("TEMPLATE001");
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("更新模板 - 模板不存在抛出异常")
        void updateTemplate_notFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> templateService.updateTemplate("NONEXISTENT", testSaveDTO));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
            verify(templateRepository, never()).save(any());
        }

        @Test
        @DisplayName("更新模板 - 被删除的模板抛出异常")
        void updateTemplate_deleted_throwException() {
            testTemplate.setDeleted(true);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> templateService.updateTemplate("TEMPLATE001", testSaveDTO));

            assertEquals("模板不存在: TEMPLATE001", exception.getMessage());
        }

        @Test
        @DisplayName("更新模板 - 不更新模板类型")
        void updateTemplate_notUpdateType() {
            testTemplate.setTemplateType(TemplateType.ADMISSION);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplateSaveDTO updateDTO = new EmrTemplateSaveDTO();
            updateDTO.setTemplateName("新模板名");
            // 不设置模板类型

            EmrTemplate result = templateService.updateTemplate("TEMPLATE001", updateDTO);

            assertEquals(TemplateType.ADMISSION, result.getTemplateType());
        }

        @Test
        @DisplayName("更新模板 - 更新模板类型")
        void updateTemplate_updateType() {
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplateSaveDTO updateDTO = new EmrTemplateSaveDTO();
            updateDTO.setTemplateName("新模板名");
            updateDTO.setTemplateType(TemplateType.PROGRESS);

            EmrTemplate result = templateService.updateTemplate("TEMPLATE001", updateDTO);

            assertEquals(TemplateType.PROGRESS, result.getTemplateType());
        }
    }

    @Nested
    @DisplayName("模板删除测试")
    class DeleteTemplateTests {

        @Test
        @DisplayName("成功删除模板 - 逻辑删除")
        void deleteTemplate_success() {
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            templateService.deleteTemplate("TEMPLATE001");

            assertTrue(testTemplate.getDeleted());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("删除模板 - 模板不存在抛出异常")
        void deleteTemplate_notFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> templateService.deleteTemplate("NONEXISTENT"));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
            verify(templateRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("模板查询测试")
    class QueryTemplateTests {

        @Test
        @DisplayName("根据ID获取模板成功")
        void getTemplateById_success() {
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));

            EmrTemplate result = templateService.getTemplateById("TEMPLATE001");

            assertNotNull(result);
            assertEquals("门诊初诊模板", result.getTemplateName());
            verify(templateRepository).findById("TEMPLATE001");
        }

        @Test
        @DisplayName("根据ID获取模板 - 不存在抛出异常")
        void getTemplateById_notFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> templateService.getTemplateById("NONEXISTENT"));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
        }

        @Test
        @DisplayName("分页查询模板列表")
        void listTemplates_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<EmrTemplate> templates = Arrays.asList(testTemplate, createAnotherTemplate());
            Page<EmrTemplate> page = new PageImpl<>(templates);

            when(templateRepository.findByDeletedFalse(pageable)).thenReturn(page);

            Page<EmrTemplate> result = templateService.listTemplates(pageable);

            assertEquals(2, result.getContent().size());
            verify(templateRepository).findByDeletedFalse(pageable);
        }

        @Test
        @DisplayName("根据名称模糊查询模板")
        void searchByTemplateName_success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<EmrTemplate> templates = List.of(testTemplate);
            Page<EmrTemplate> page = new PageImpl<>(templates);

            when(templateRepository.findByTemplateNameContainingAndDeletedFalse("门诊", pageable))
                    .thenReturn(page);

            Page<EmrTemplate> result = templateService.searchByTemplateName("门诊", pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("门诊初诊模板", result.getContent().get(0).getTemplateName());
            verify(templateRepository).findByTemplateNameContainingAndDeletedFalse("门诊", pageable);
        }

        @Test
        @DisplayName("根据模板类型查询可用模板")
        void getTemplatesByType_success() {
            List<EmrTemplate> templates = List.of(testTemplate);

            when(templateRepository.findByTemplateTypeAndIsEnabledTrueAndDeletedFalse(TemplateType.OUTPATIENT_FIRST))
                    .thenReturn(templates);

            List<EmrTemplate> result = templateService.getTemplatesByType(TemplateType.OUTPATIENT_FIRST);

            assertEquals(1, result.size());
            verify(templateRepository).findByTemplateTypeAndIsEnabledTrueAndDeletedFalse(TemplateType.OUTPATIENT_FIRST);
        }

        @Test
        @DisplayName("根据科室查询模板")
        void getTemplatesByDeptId_success() {
            List<EmrTemplate> templates = List.of(testTemplate);

            when(templateRepository.findByDeptIdAndIsEnabledTrueAndDeletedFalse("DEPT001"))
                    .thenReturn(templates);

            List<EmrTemplate> result = templateService.getTemplatesByDeptId("DEPT001");

            assertEquals(1, result.size());
            assertEquals("DEPT001", result.get(0).getDeptId());
            verify(templateRepository).findByDeptIdAndIsEnabledTrueAndDeletedFalse("DEPT001");
        }

        @Test
        @DisplayName("查询公开模板")
        void getPublicTemplates_success() {
            List<EmrTemplate> templates = List.of(testTemplate);

            when(templateRepository.findByIsPublicTrueAndIsEnabledTrueAndDeletedFalse())
                    .thenReturn(templates);

            List<EmrTemplate> result = templateService.getPublicTemplates();

            assertNotNull(result);
            verify(templateRepository).findByIsPublicTrueAndIsEnabledTrueAndDeletedFalse();
        }

        @Test
        @DisplayName("根据创建人查询模板")
        void getTemplatesByCreatorId_success() {
            List<EmrTemplate> templates = List.of(testTemplate);

            when(templateRepository.findByCreatorIdAndDeletedFalse("DOC001"))
                    .thenReturn(templates);

            List<EmrTemplate> result = templateService.getTemplatesByCreatorId("DOC001");

            assertEquals(1, result.size());
            assertEquals("DOC001", result.get(0).getCreatorId());
            verify(templateRepository).findByCreatorIdAndDeletedFalse("DOC001");
        }

        @Test
        @DisplayName("查询可用模板 - 按类型、科室、创建人")
        void getAvailableTemplates_success() {
            List<EmrTemplate> templates = List.of(testTemplate);

            when(templateRepository.findAvailableTemplates(TemplateType.OUTPATIENT_FIRST, "DEPT001", "DOC001"))
                    .thenReturn(templates);

            List<EmrTemplate> result = templateService.getAvailableTemplates(
                    TemplateType.OUTPATIENT_FIRST, "DEPT001", "DOC001");

            assertEquals(1, result.size());
            verify(templateRepository).findAvailableTemplates(TemplateType.OUTPATIENT_FIRST, "DEPT001", "DOC001");
        }
    }

    @Nested
    @DisplayName("模板状态管理测试")
    class TemplateStatusTests {

        @Test
        @DisplayName("启用模板")
        void toggleTemplateStatus_enable() {
            testTemplate.setIsEnabled(false);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplate result = templateService.toggleTemplateStatus("TEMPLATE001", true);

            assertTrue(result.getIsEnabled());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("禁用模板")
        void toggleTemplateStatus_disable() {
            testTemplate.setIsEnabled(true);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplate result = templateService.toggleTemplateStatus("TEMPLATE001", false);

            assertFalse(result.getIsEnabled());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("设置模板公开状态 - 公开")
        void toggleTemplatePublic_makePublic() {
            testTemplate.setIsPublic(false);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplate result = templateService.toggleTemplatePublic("TEMPLATE001", true);

            assertTrue(result.getIsPublic());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("设置模板公开状态 - 取消公开")
        void toggleTemplatePublic_makePrivate() {
            testTemplate.setIsPublic(true);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            EmrTemplate result = templateService.toggleTemplatePublic("TEMPLATE001", false);

            assertFalse(result.getIsPublic());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("增加使用次数")
        void incrementUseCount_success() {
            testTemplate.setUseCount(5);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            templateService.incrementUseCount("TEMPLATE001");

            assertEquals(6, testTemplate.getUseCount());
            verify(templateRepository).save(testTemplate);
        }

        @Test
        @DisplayName("增加使用次数 - 首次使用")
        void incrementUseCount_firstUse() {
            testTemplate.setUseCount(0);
            when(templateRepository.findById("TEMPLATE001")).thenReturn(Optional.of(testTemplate));
            when(templateRepository.save(any(EmrTemplate.class))).thenReturn(testTemplate);

            templateService.incrementUseCount("TEMPLATE001");

            assertEquals(1, testTemplate.getUseCount());
        }

        @Test
        @DisplayName("增加使用次数 - 模板不存在抛出异常")
        void incrementUseCount_notFound_throwException() {
            when(templateRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> templateService.incrementUseCount("NONEXISTENT"));

            assertEquals("模板不存在: NONEXISTENT", exception.getMessage());
        }
    }

    // Helper methods
    private EmrTemplate createTestTemplate() {
        EmrTemplate template = new EmrTemplate();
        template.setId("TEMPLATE001");
        template.setTemplateName("门诊初诊模板");
        template.setTemplateType(TemplateType.OUTPATIENT_FIRST);
        template.setCategory("内科");
        template.setTemplateContent("模板内容示例");
        template.setDeptId("DEPT001");
        template.setDeptName("内科");
        template.setCreatorId("DOC001");
        template.setCreatorName("张医生");
        template.setIsPublic(false);
        template.setIsEnabled(true);
        template.setUseCount(0);
        template.setDeleted(false);
        return template;
    }

    private EmrTemplate createAnotherTemplate() {
        EmrTemplate template = new EmrTemplate();
        template.setId("TEMPLATE002");
        template.setTemplateName("入院记录模板");
        template.setTemplateType(TemplateType.ADMISSION);
        template.setCategory("外科");
        template.setDeptId("DEPT002");
        template.setCreatorId("DOC002");
        template.setIsEnabled(true);
        template.setDeleted(false);
        return template;
    }

    private EmrTemplateSaveDTO createTestSaveDTO() {
        EmrTemplateSaveDTO dto = new EmrTemplateSaveDTO();
        dto.setTemplateName("门诊初诊模板");
        dto.setTemplateType(TemplateType.OUTPATIENT_FIRST);
        dto.setCategory("内科");
        dto.setTemplateContent("模板内容示例");
        dto.setDeptId("DEPT001");
        dto.setDeptName("内科");
        dto.setCreatorId("DOC001");
        dto.setCreatorName("张医生");
        dto.setIsPublic(false);
        dto.setIsEnabled(true);
        dto.setRemark("测试模板");
        return dto;
    }
}