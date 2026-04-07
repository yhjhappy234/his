package com.yhj.his.module.outpatient.service;

import com.yhj.his.common.core.domain.PageResult;
import com.yhj.his.module.outpatient.dto.AppointmentCancelRequest;
import com.yhj.his.module.outpatient.dto.AppointmentCreateRequest;
import com.yhj.his.module.outpatient.dto.CheckInRequest;
import com.yhj.his.module.outpatient.entity.Registration;
import com.yhj.his.module.outpatient.vo.AppointmentResultVO;
import com.yhj.his.module.outpatient.vo.CheckInResultVO;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 挂号服务接口
 */
public interface RegistrationService {

    /**
     * 预约挂号
     */
    AppointmentResultVO createAppointment(AppointmentCreateRequest request);

    /**
     * 取消预约
     */
    void cancelAppointment(AppointmentCancelRequest request);

    /**
     * 签到
     */
    CheckInResultVO checkIn(CheckInRequest request);

    /**
     * 根据ID查询挂号记录
     */
    Optional<Registration> findById(String id);

    /**
     * 根据就诊序号查询
     */
    Optional<Registration> findByVisitNo(String visitNo);

    /**
     * 获取挂号详情
     */
    Registration getRegistrationDetail(String id);

    /**
     * 分页查询挂号列表
     */
    PageResult<Registration> listRegistrations(String patientId, String deptId, String doctorId, String status, LocalDate date, Pageable pageable);

    /**
     * 查询患者挂号记录
     */
    List<Registration> listPatientRegistrations(String patientId);

    /**
     * 查询医生当日挂号记录
     */
    List<Registration> listDoctorRegistrations(String doctorId, LocalDate date);

    /**
     * 退号
     */
    void refundRegistration(String id, String reason);

    /**
     * 开始就诊
     */
    Registration startVisit(String registrationId, String doctorId);

    /**
     * 结束就诊
     */
    Registration endVisit(String registrationId);

    /**
     * 查询待诊患者列表
     */
    List<Registration> listWaitingPatients(String doctorId, LocalDate date);

    /**
     * 获取当前就诊患者
     */
    Optional<Registration> getCurrentPatient(String doctorId, LocalDate date);
}