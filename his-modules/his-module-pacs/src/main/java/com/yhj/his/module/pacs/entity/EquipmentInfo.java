package com.yhj.his.module.pacs.entity;

import com.yhj.his.common.db.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment_info")
public class EquipmentInfo extends BaseEntity {

    @Column(name = "equipment_code", length = 20, unique = true)
    private String equipmentCode;

    @Column(name = "equipment_name", length = 100, nullable = false)
    private String equipmentName;

    @Column(name = "equipment_type", length = 20, nullable = false)
    private String equipmentType;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    @Column(name = "ae_title", length = 16)
    private String aeTitle;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "port")
    private Integer port;

    @Column(name = "room_no", length = 20)
    private String roomNo;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "purchase_date")
    private String purchaseDate;

    @Column(name = "enable_date")
    private String enableDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "正常";

    @Column(name = "manager_id", length = 20)
    private String managerId;

    @Column(name = "manager_name", length = 50)
    private String managerName;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "remark", length = 500)
    private String remark;
}