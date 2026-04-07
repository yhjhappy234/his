package com.yhj.his.module.system.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import com.yhj.his.module.system.enums.OperationResult;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_operation_log", indexes = {
        @Index(name = "idx_op_user_time", columnList = "user_id, operation_time"),
        @Index(name = "idx_op_module_time", columnList = "operation_module, operation_time"),
        @Index(name = "idx_op_time", columnList = "operation_time")
})
public class OperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志编号
     */
    @Column(name = "log_no", length = 30)
    private String logNo;

    /**
     * 用户ID
     */
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * 登录账号
     */
    @Column(name = "login_name", length = 30)
    private String loginName;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 科室ID
     */
    @Column(name = "dept_id", length = 36)
    private String deptId;

    /**
     * 科室名称
     */
    @Column(name = "dept_name", length = 100)
    private String deptName;

    /**
     * 操作类型
     */
    @Column(name = "operation_type", length = 20)
    private String operationType;

    /**
     * 操作模块
     */
    @Column(name = "operation_module", length = 50)
    private String operationModule;

    /**
     * 操作功能
     */
    @Column(name = "operation_func", length = 50)
    private String operationFunc;

    /**
     * 操作描述
     */
    @Column(name = "operation_desc", length = 200)
    private String operationDesc;

    /**
     * 请求方法
     */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /**
     * 请求URL
     */
    @Column(name = "request_url", length = 200)
    private String requestUrl;

    /**
     * 请求参数
     */
    @Column(name = "request_param", columnDefinition = "TEXT")
    private String requestParam;

    /**
     * 响应数据
     */
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;

    /**
     * 操作结果
     */
    @Column(name = "operation_result", length = 20)
    @Enumerated(EnumType.STRING)
    private OperationResult operationResult;

    /**
     * 错误信息
     */
    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    /**
     * 操作时间
     */
    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    /**
     * 耗时(ms)
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * 客户端IP
     */
    @Column(name = "client_ip", length = 50)
    private String clientIp;

    /**
     * 服务端IP
     */
    @Column(name = "server_ip", length = 50)
    private String serverIp;
}