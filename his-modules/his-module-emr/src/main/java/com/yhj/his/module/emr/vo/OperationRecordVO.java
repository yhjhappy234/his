package com.yhj.his.module.emr.vo;

import com.yhj.his.module.emr.enums.EmrStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 手术记录VO
 */
@Data
@Schema(description = "手术记录详情")
public class OperationRecordVO {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
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

    @Schema(description = "手术名称")
    private String operationName;

    @Schema(description = "手术编码")
    private String operationCode;

    @Schema(description = "主刀医生ID")
    private String surgeonId;

    @Schema(description = "主刀医生姓名")
    private String surgeonName;

    @Schema(description = "助手列表")
    private String assistants;

    @Schema(description = "麻醉医生姓名")
    private String anesthesiologistName;

    @Schema(description = "麻醉方式")
    private String anesthesiaMethod;

    @Schema(description = "手术室")
    private String operatingRoom;

    @Schema(description = "切口描述")
    private String incision;

    @Schema(description = "手术过程")
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

    @Schema(description = "植入物")
    private String implants;

    @Schema(description = "状态")
    private EmrStatus status;

    @Schema(description = "科室名称")
    private String deptName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}