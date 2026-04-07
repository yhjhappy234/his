package com.yhj.his.module.inpatient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 病区床位统计VO
 */
@Data
@Schema(description = "病区床位统计")
public class WardBedStatisticsVO {

    @Schema(description = "病区ID")
    private String wardId;

    @Schema(description = "病区名称")
    private String wardName;

    @Schema(description = "总床位数")
    private Long total;

    @Schema(description = "空床数")
    private Long vacant;

    @Schema(description = "占用床数")
    private Long occupied;

    @Schema(description = "预留床数")
    private Long reserved;

    @Schema(description = "维修床数")
    private Long maintenance;

    @Schema(description = "床位列表")
    private List<BedVO> beds;

    @Schema(description = "床位使用率(%)")
    private Double utilizationRate;
}