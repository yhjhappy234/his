package com.yhj.his.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典VO
 */
@Data
@Schema(description = "数据字典信息")
public class DataDictionaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典ID")
    private String id;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "父级编码")
    private String parentCode;

    @Schema(description = "层级")
    private Integer dictLevel;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "是否默认值")
    private Boolean isDefault;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子字典项列表")
    private java.util.List<DataDictionaryVO> children;
}