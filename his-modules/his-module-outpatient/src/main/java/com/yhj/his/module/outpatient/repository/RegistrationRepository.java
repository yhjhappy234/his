package com.yhj.his.module.outpatient.repository;

import com.yhj.his.module.outpatient.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 挂号记录Repository
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String>, JpaSpecificationExecutor<Registration> {

    /**
     * 根据患者ID查询挂号记录列表
     */
    List<Registration> findByPatientIdOrderByScheduleDateDesc(String patientId);

    /**
     * 根据患者ID和状态查询
     */
    List<Registration> findByPatientIdAndStatus(String patientId, String status);

    /**
     * 根据医生ID和日期查询挂号记录
     */
    List<Registration> findByDoctorIdAndScheduleDate(String doctorId, LocalDate scheduleDate);

    /**
     * 根据医生ID和日期和就诊状态查询挂号记录
     */
    List<Registration> findByDoctorIdAndScheduleDateAndVisitStatus(String doctorId, LocalDate scheduleDate, String visitStatus);

    /**
     * 根据科室ID和日期查询挂号记录
     */
    List<Registration> findByDeptIdAndScheduleDate(String deptId, LocalDate scheduleDate);

    /**
     * 根据排班ID查询挂号记录
     */
    List<Registration> findByScheduleId(String scheduleId);

    /**
     * 根据就诊序号查询
     */
    Optional<Registration> findByVisitNo(String visitNo);

    /**
     * 获取当日最大排队号
     */
    @Query("SELECT MAX(r.queueNo) FROM Registration r WHERE r.scheduleId = :scheduleId")
    Optional<Integer> findMaxQueueNoByScheduleId(@Param("scheduleId") String scheduleId);

    /**
     * 统计医生当日待诊患者数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.doctorId = :doctorId AND r.scheduleDate = :scheduleDate AND r.visitStatus IN ('待诊', '就诊中')")
    int countPendingPatients(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 统计医生当日已完成患者数量
     */
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.doctorId = :doctorId AND r.scheduleDate = :scheduleDate AND r.visitStatus = '已完成'")
    int countCompletedPatients(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 查询医生当日候诊患者列表(按排队号排序)
     */
    @Query("SELECT r FROM Registration r WHERE r.doctorId = :doctorId AND r.scheduleDate = :scheduleDate AND r.visitStatus = '待诊' AND r.status = '已签到' ORDER BY r.queueNo ASC")
    List<Registration> findWaitingPatients(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);

    /**
     * 查询医生当日就诊中患者
     */
    @Query("SELECT r FROM Registration r WHERE r.doctorId = :doctorId AND r.scheduleDate = :scheduleDate AND r.visitStatus = '就诊中'")
    Optional<Registration> findCurrentPatient(@Param("doctorId") String doctorId, @Param("scheduleDate") LocalDate scheduleDate);
}