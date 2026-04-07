package com.yhj.his.module.inpatient.dto;

import com.yhj.his.module.inpatient.enums.BedStatus;
import com.yhj.his.module.inpatient.enums.BedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 床位查询DTO
 */
@Data
@Schema(description = "床位查询条件")
public class BedQueryDTO {

    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "病房号")
    private String roomNo;

    @Schema(description = "床位状态")
    private BedStatus status;

    @Schema(description = "床位类型")
    private BedType bedType;
}