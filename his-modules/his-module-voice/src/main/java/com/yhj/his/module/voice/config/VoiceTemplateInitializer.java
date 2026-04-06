package com.yhj.his.module.voice.config;

import com.yhj.his.module.voice.entity.VoiceTemplate;
import com.yhj.his.module.voice.enums.TemplateType;
import com.yhj.his.module.voice.repository.VoiceTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音模板初始化器
 * 启动时初始化系统预置模板
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceTemplateInitializer implements ApplicationRunner {

    private final VoiceTemplateRepository voiceTemplateRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始初始化语音模板...");

        List<VoiceTemplate> templates = createSystemTemplates();
        for (VoiceTemplate template : templates) {
            if (!voiceTemplateRepository.existsByTemplateCode(template.getTemplateCode())) {
                voiceTemplateRepository.save(template);
                log.info("初始化模板: {}", template.getTemplateCode());
            }
        }

        log.info("语音模板初始化完成");
    }

    /**
     * 创建系统预置模板列表
     */
    private List<VoiceTemplate> createSystemTemplates() {
        List<VoiceTemplate> templates = new ArrayList<>();

        // 标准叫号模板
        VoiceTemplate callStandard = new VoiceTemplate();
        callStandard.setTemplateCode(TemplateType.CALL_STANDARD.getCode());
        callStandard.setTemplateName("标准叫号模板");
        callStandard.setTemplateType(TemplateType.CALL_STANDARD);
        callStandard.setContentTemplate("请{queueNo}号患者{patientName}，到{roomNo}诊室就诊。");
        callStandard.setParamsDefine("[{\"name\":\"queueNo\",\"desc\":\"排队号\"},{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"roomNo\",\"desc\":\"诊室号\"}]");
        callStandard.setIsSystem(true);
        callStandard.setIsEnabled(true);
        callStandard.setSortOrder(1);
        templates.add(callStandard);

        // 过号重呼模板
        VoiceTemplate callRetry = new VoiceTemplate();
        callRetry.setTemplateCode(TemplateType.CALL_RETRY.getCode());
        callRetry.setTemplateName("过号重呼模板");
        callRetry.setTemplateType(TemplateType.CALL_RETRY);
        callRetry.setContentTemplate("请{queueNo}号患者{patientName}，到{roomNo}诊室就诊。请听到广播后尽快前往。");
        callRetry.setParamsDefine("[{\"name\":\"queueNo\",\"desc\":\"排队号\"},{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"roomNo\",\"desc\":\"诊室号\"}]");
        callRetry.setIsSystem(true);
        callRetry.setIsEnabled(true);
        callRetry.setSortOrder(2);
        templates.add(callRetry);

        // 复诊叫号模板
        VoiceTemplate callRecheck = new VoiceTemplate();
        callRecheck.setTemplateCode(TemplateType.CALL_RECHECK.getCode());
        callRecheck.setTemplateName("复诊叫号模板");
        callRecheck.setTemplateType(TemplateType.CALL_RECHECK);
        callRecheck.setContentTemplate("请{queueNo}号患者{patientName}，到{roomNo}诊室复诊。");
        callRecheck.setParamsDefine("[{\"name\":\"queueNo\",\"desc\":\"排队号\"},{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"roomNo\",\"desc\":\"诊室号\"}]");
        callRecheck.setIsSystem(true);
        callRecheck.setIsEnabled(true);
        callRecheck.setSortOrder(3);
        templates.add(callRecheck);

        // 检验报告模板
        VoiceTemplate reportLab = new VoiceTemplate();
        reportLab.setTemplateCode(TemplateType.REPORT_LAB.getCode());
        reportLab.setTemplateName("检验报告通知模板");
        reportLab.setTemplateType(TemplateType.REPORT_LAB);
        reportLab.setContentTemplate("{patientName}您好，您的检验报告已完成，请到{deptName}{windowNo}号窗口取报告。");
        reportLab.setParamsDefine("[{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"deptName\",\"desc\":\"科室名称\"},{\"name\":\"windowNo\",\"desc\":\"窗口号\"}]");
        reportLab.setIsSystem(true);
        reportLab.setIsEnabled(true);
        reportLab.setSortOrder(4);
        templates.add(reportLab);

        // 影像报告模板
        VoiceTemplate reportRadiology = new VoiceTemplate();
        reportRadiology.setTemplateCode(TemplateType.REPORT_RADIOLOGY.getCode());
        reportRadiology.setTemplateName("影像报告通知模板");
        reportRadiology.setTemplateType(TemplateType.REPORT_RADIOLOGY);
        reportRadiology.setContentTemplate("{patientName}您好，您的{examType}检查报告已完成，请到{deptName}报告领取处取报告。");
        reportRadiology.setParamsDefine("[{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"examType\",\"desc\":\"检查类型\"},{\"name\":\"deptName\",\"desc\":\"科室名称\"}]");
        reportRadiology.setIsSystem(true);
        reportRadiology.setIsEnabled(true);
        reportRadiology.setSortOrder(5);
        templates.add(reportRadiology);

        // 患者寻人模板
        VoiceTemplate findPatient = new VoiceTemplate();
        findPatient.setTemplateCode(TemplateType.FIND_PATIENT.getCode());
        findPatient.setTemplateName("患者寻人模板");
        findPatient.setTemplateType(TemplateType.FIND_PATIENT);
        findPatient.setContentTemplate("{personName}患者，请听到广播后，速到{targetLocation}，{reason}。");
        findPatient.setParamsDefine("[{\"name\":\"personName\",\"desc\":\"患者姓名\"},{\"name\":\"targetLocation\",\"desc\":\"目标位置\"},{\"name\":\"reason\",\"desc\":\"原因\"}]");
        findPatient.setIsSystem(true);
        findPatient.setIsEnabled(true);
        findPatient.setSortOrder(6);
        templates.add(findPatient);

        // 家属寻人模板
        VoiceTemplate findFamily = new VoiceTemplate();
        findFamily.setTemplateCode(TemplateType.FIND_FAMILY.getCode());
        findFamily.setTemplateName("家属寻人模板");
        findFamily.setTemplateType(TemplateType.FIND_FAMILY);
        findFamily.setContentTemplate("{personName}的家属，请到{targetLocation}{deptName}，{personName}正在等候。");
        findFamily.setParamsDefine("[{\"name\":\"personName\",\"desc\":\"患者姓名\"},{\"name\":\"targetLocation\",\"desc\":\"目标位置\"},{\"name\":\"deptName\",\"desc\":\"科室名称\"}]");
        findFamily.setIsSystem(true);
        findFamily.setIsEnabled(true);
        findFamily.setSortOrder(7);
        templates.add(findFamily);

        // 医护寻人模板
        VoiceTemplate findStaff = new VoiceTemplate();
        findStaff.setTemplateCode(TemplateType.FIND_STAFF.getCode());
        findStaff.setTemplateName("医护寻人模板");
        findStaff.setTemplateType(TemplateType.FIND_STAFF);
        findStaff.setContentTemplate("请{deptName}{doctorName}医生，速到{targetLocation}，有紧急会诊。");
        findStaff.setParamsDefine("[{\"name\":\"deptName\",\"desc\":\"科室名称\"},{\"name\":\"doctorName\",\"desc\":\"医生姓名\"},{\"name\":\"targetLocation\",\"desc\":\"目标位置\"}]");
        findStaff.setIsSystem(true);
        findStaff.setIsEnabled(true);
        findStaff.setSortOrder(8);
        templates.add(findStaff);

        // 系统公告模板
        VoiceTemplate systemNotice = new VoiceTemplate();
        systemNotice.setTemplateCode(TemplateType.SYSTEM_NOTICE.getCode());
        systemNotice.setTemplateName("系统公告模板");
        systemNotice.setTemplateType(TemplateType.SYSTEM_NOTICE);
        systemNotice.setContentTemplate("各位患者、医护人员请注意，{content}");
        systemNotice.setParamsDefine("[{\"name\":\"content\",\"desc\":\"通知内容\"}]");
        systemNotice.setIsSystem(true);
        systemNotice.setIsEnabled(true);
        systemNotice.setSortOrder(9);
        templates.add(systemNotice);

        // 紧急通知模板
        VoiceTemplate emergencyNotice = new VoiceTemplate();
        emergencyNotice.setTemplateCode(TemplateType.EMERGENCY_NOTICE.getCode());
        emergencyNotice.setTemplateName("紧急通知模板");
        emergencyNotice.setTemplateType(TemplateType.EMERGENCY_NOTICE);
        emergencyNotice.setContentTemplate("紧急通知：{content}，请各位{targetGroup}注意{precaution}。");
        emergencyNotice.setParamsDefine("[{\"name\":\"content\",\"desc\":\"事件内容\"},{\"name\":\"targetGroup\",\"desc\":\"相关人员\"},{\"name\":\"precaution\",\"desc\":\"注意事项\"}]");
        emergencyNotice.setIsSystem(true);
        emergencyNotice.setIsEnabled(true);
        emergencyNotice.setSortOrder(10);
        templates.add(emergencyNotice);

        // 危急值通知模板
        VoiceTemplate criticalValue = new VoiceTemplate();
        criticalValue.setTemplateCode(TemplateType.CRITICAL_VALUE.getCode());
        criticalValue.setTemplateName("危急值通知模板");
        criticalValue.setTemplateType(TemplateType.CRITICAL_VALUE);
        criticalValue.setContentTemplate("危急值通知：{deptName}{bedNo}床患者{patientName}，{itemName}结果{value}，请立即处理。");
        criticalValue.setParamsDefine("[{\"name\":\"deptName\",\"desc\":\"科室名称\"},{\"name\":\"bedNo\",\"desc\":\"床号\"},{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"itemName\",\"desc\":\"项目名称\"},{\"name\":\"value\",\"desc\":\"数值\"}]");
        criticalValue.setIsSystem(true);
        criticalValue.setIsEnabled(true);
        criticalValue.setSortOrder(11);
        templates.add(criticalValue);

        // 取药提醒模板
        VoiceTemplate medication = new VoiceTemplate();
        medication.setTemplateCode(TemplateType.MEDICATION.getCode());
        medication.setTemplateName("取药提醒模板");
        medication.setTemplateType(TemplateType.MEDICATION);
        medication.setContentTemplate("请{patientName}到{pharmacyName}{windowNo}号窗口取药。");
        medication.setParamsDefine("[{\"name\":\"patientName\",\"desc\":\"患者姓名\"},{\"name\":\"pharmacyName\",\"desc\":\"药房名称\"},{\"name\":\"windowNo\",\"desc\":\"窗口号\"}]");
        medication.setIsSystem(true);
        medication.setIsEnabled(true);
        medication.setSortOrder(12);
        templates.add(medication);

        return templates;
    }
}