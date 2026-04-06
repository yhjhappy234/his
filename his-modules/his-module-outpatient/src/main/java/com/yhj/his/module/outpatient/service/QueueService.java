package com.yhj.his.module.outpatient.service;

import com.yhj.his.module.outpatient.dto.CallPatientRequest;
import com.yhj.his.module.outpatient.entity.Queue;
import com.yhj.his.module.outpatient.vo.QueueInfoVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 排队服务接口
 */
public interface QueueService {

    /**
     * 叫号
     */
    Queue callPatient(CallPatientRequest request);

    /**
     * 根据ID查询排队信息
     */
    Optional<Queue> findById(String id);

    /**
     * 根据挂号ID查询排队信息
     */
    Optional<Queue> findByRegistrationId(String registrationId);

    /**
     * 获取排队详情
     */
    Queue getQueueDetail(String id);

    /**
     * 获取诊室排队信息
     */
    QueueInfoVO getQueueInfo(String doctorId, LocalDate date);

    /**
     * 查询等候列表
     */
    List<Queue> listWaitingQueue(String doctorId, LocalDate date);

    /**
     * 查询医生当日排队列表
     */
    List<Queue> listDoctorQueue(String doctorId, LocalDate date);

    /**
     * 开始就诊
     */
    Queue startVisit(String queueId, String doctorId, String clinicRoom);

    /**
     * 结束就诊
     */
    Queue endVisit(String queueId);

    /**
     * 过号处理
     */
    Queue markAsPassed(String queueId);

    /**
     * 复诊入队
     */
    Queue requeue(String queueId);

    /**
     * 获取当前就诊患者
     */
    Optional<Queue> getCurrentPatient(String doctorId, LocalDate date);

    /**
     * 统计等候人数
     */
    int countWaiting(String doctorId, LocalDate date);

    /**
     * 统计过号人数
     */
    int countPassed(String doctorId, LocalDate date);

    /**
     * 设置优先级
     */
    Queue setPriority(String queueId, int priority);

    /**
     * 下一个患者
     */
    Queue nextPatient(String doctorId, LocalDate date);
}