package com.yhj.his.module.pharmacy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 处方审核结果VO
 */
@Data
@Schema(description = "处方审核结果响应")
public class AuditResultVO {

    @Schema(description = "审核是否通过")
    private Boolean passed;

    @Schema(description = "警告信息列表")
    private List<AuditMessage> warnings;

    @Schema(description = "错误信息列表")
    private List<AuditMessage> errors;

    @Data
    @Schema(description = "审核信息")
    public static class AuditMessage {

        @Schema(description = "药品ID")
        private String drugId;

        @Schema(description = "药品名称")
        private String drugName;

        @Schema(description = "消息类型")
        private String messageType;

        @Schema(description = "消息内容")
        private String message;

        @Schema(description = "严重程度")
        private String severity;
    }

    public static AuditResultVO success() {
        AuditResultVO result = new AuditResultVO();
        result.setPassed(true);
        result.setWarnings(List.of());
        result.setErrors(List.of());
        return result;
    }

    public static AuditResultVO withWarnings(List<AuditMessage> warnings) {
        AuditResultVO result = new AuditResultVO();
        result.setPassed(true);
        result.setWarnings(warnings);
        result.setErrors(List.of());
        return result;
    }

    public static AuditResultVO withErrors(List<AuditMessage> errors) {
        AuditResultVO result = new AuditResultVO();
        result.setPassed(false);
        result.setWarnings(List.of());
        result.setErrors(errors);
        return result;
    }
}