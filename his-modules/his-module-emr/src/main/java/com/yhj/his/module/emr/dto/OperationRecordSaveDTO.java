package com.yhj.his.module.emr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 手术记录保存DTO
 */
@Data
@Schema(description = "手术记录保存请求")
public class OperationRecordSaveDTO {

    @Schema(description = "记录ID(更新时必填)")
    private String id;

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID", required = true)
    private String admissionId;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true)
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "手术日期")
    private LocalDate operationDate;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "手术时长(分钟)")
    private Integer operationDuration;

    @Schema(description = "术前诊断")
    private String preOpDiagnosis;

    @Schema(description = "术后诊断")
    private String postOpDiagnosis;

    @NotBlank(message = "手术名称不能为空")
    @Schema(description = "手术名称", required = true)
    private String operationName;

    @Schema(description = "手术编码")
    private String operationCode;

    @NotBlank(message = "主刀医生ID不能为空")
    @Schema(description = "主刀医生ID", required = true)
    private String surgeonId;

    @Schema(description = "主刀医生姓名")
    private String surgeonName;

    @Schema(description = "助手列表(JSON)")
    private String assistants;

    @Schema(description = "麻醉医生ID")
    private String anesthesiologistId;

    @Schema(description = "麻醉医生姓名")
    private String anesthesiologistName;

    @Schema(description = "麻醉方式")
    private String anesthesiaMethod;

    @Schema(description = "手术室")
    private String operatingRoom;

    @Schema(description = "洗手护士")
    private String scrubNurse;

    @Schema(description = "巡回护士")
    private String circulatingNurse;

    @Schema(description = "切口描述")
    private String incision;

    @NotBlank(message = "手术过程不能为空")
    @Schema(description = "手术过程", required = true)
    private String procedureDetail;

    @Schema(description = "手术所见")
    private String operationFindings;

    @Schema(description = "标本处理")
    private String specimens;

    @Schema(description = "并发症")
    private String complications;

    @Schema(description = "出血量(ml)")
    private Integer bloodLoss;

    @Schema(description = "输血情况")
    private String transfusion;

    @Schema(description = "植入物(JSON)")
    private String implants;

    @Schema(description = "科室ID")
    private String deptId;

    @Schema(description = "科室名称")
    private String deptName;
}