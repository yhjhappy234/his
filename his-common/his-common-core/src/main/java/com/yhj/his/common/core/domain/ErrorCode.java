package com.yhj.his.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码定义
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 成功
    SUCCESS(0, "成功"),

    // 客户端错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    PARAM_MISSING(1002, "参数缺失"),
    PARAM_FORMAT_ERROR(1003, "参数格式错误"),
    PARAM_VALIDATE_ERROR(1004, "参数校验失败"),

    // 业务错误 2xxx
    BIZ_ERROR(2001, "业务异常"),
    DATA_NOT_FOUND(2002, "数据不存在"),
    DATA_ALREADY_EXISTS(2003, "数据已存在"),
    DATA_STATUS_ERROR(2004, "数据状态异常"),
    OPERATION_NOT_ALLOWED(2005, "操作不允许"),
    DUPLICATE_OPERATION(2006, "重复操作"),

    // 认证授权错误 3xxx
    AUTH_ERROR(3001, "认证失败"),
    TOKEN_INVALID(3002, "Token无效"),
    TOKEN_EXPIRED(3003, "Token已过期"),
    PERMISSION_DENIED(3004, "权限不足"),
    ACCOUNT_DISABLED(3005, "账号已禁用"),
    ACCOUNT_LOCKED(3006, "账号已锁定"),
    PASSWORD_ERROR(3007, "密码错误"),
    LOGIN_REQUIRED(3008, "请先登录"),

    // 系统错误 5xxx
    SYS_ERROR(5001, "系统异常"),
    DB_ERROR(5002, "数据库异常"),
    NETWORK_ERROR(5003, "网络异常"),
    SERVICE_UNAVAILABLE(5004, "服务不可用"),
    INTERNAL_ERROR(5005, "内部错误");

    private final Integer code;
    private final String message;
}