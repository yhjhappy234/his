package com.yhj.his.module.inpatient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 床位分配请求DTO
 */
@Data
@Schema(description = "床位分配请求")
public class BedAssignDTO {

    @NotBlank(message = "住院ID不能为空")
    @Schema(description = "住院ID")
    private String admissionId;

    @NotBlank(message = "病区ID不能为空")
    @Schema(description = "病区ID")
    private String wardId;

    @NotBlank(message = "床位号不能为空")
    @Schema(description = "床位号")
    private String bedNo;
}