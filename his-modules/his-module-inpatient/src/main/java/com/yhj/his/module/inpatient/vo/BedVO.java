package com.yhj.his.module.inpatient.vo;

import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 床位信息VO
 */
@Data
@Schema(description = "床位信息")
public class BedVO {

    @Schema(description = "床位ID")
    private String bedId;

    @Schema(description = "床位号")
    private String bedNo;

    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "病区名称")
    private String wardName;

    @Schema(description = "病房号")
    private String roomNo;

    @Schema(description = "床位类型")
    private BedType bedType;

    @Schema(description = "床位等级")
    private String bedLevel;

    @Schema(description = "床位费/天")
    private BigDecimal dailyRate;

    @Schema(description = "状态")
    private BedStatus status;

    @Schema(description = "住院ID")
    private String admissionId;

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "设施配置")
    private List<String> facilities;
}