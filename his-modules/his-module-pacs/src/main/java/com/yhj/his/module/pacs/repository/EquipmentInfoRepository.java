package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.EquipmentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentInfoRepository extends JpaRepository<EquipmentInfo, String>, JpaSpecificationExecutor<EquipmentInfo> {

    Optional<EquipmentInfo> findByEquipmentCode(String equipmentCode);
    Optional<EquipmentInfo> findByEquipmentName(String equipmentName);
    List<EquipmentInfo> findByEquipmentType(String equipmentType);
    List<EquipmentInfo> findByStatus(String status);
    Optional<EquipmentInfo> findByRoomNo(String roomNo);
    Optional<EquipmentInfo> findByAeTitle(String aeTitle);

    @Query("SELECT e FROM EquipmentInfo e WHERE e.status = '正常' ORDER BY e.sortOrder")
    List<EquipmentInfo> findNormalEquipment();

    @Query("SELECT e.equipmentType, COUNT(e) FROM EquipmentInfo e GROUP BY e.equipmentType")
    List<Object[]> countByEquipmentType();

    @Query("SELECT e FROM EquipmentInfo e WHERE " +
           "(:equipmentCode IS NULL OR e.equipmentCode LIKE :equipmentCode) AND " +
           "(:equipmentName IS NULL OR e.equipmentName LIKE :equipmentName) AND " +
           "(:equipmentType IS NULL OR e.equipmentType = :equipmentType) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:roomNo IS NULL OR e.roomNo = :roomNo)")
    Page<EquipmentInfo> findByConditions(
            @Param("equipmentCode") String equipmentCode,
            @Param("equipmentName") String equipmentName,
            @Param("equipmentType") String equipmentType,
            @Param("status") String status,
            @Param("roomNo") String roomNo,
            Pageable pageable);
}