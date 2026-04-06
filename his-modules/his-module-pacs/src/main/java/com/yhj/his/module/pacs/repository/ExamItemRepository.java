package com.yhj.his.module.pacs.repository;

import com.yhj.his.module.pacs.entity.ExamItem;
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
public interface ExamItemRepository extends JpaRepository<ExamItem, String>, JpaSpecificationExecutor<ExamItem> {

    Optional<ExamItem> findByItemCode(String itemCode);
    Optional<ExamItem> findByItemName(String itemName);
    List<ExamItem> findByExamType(String examType);
    List<ExamItem> findByStatus(String status);
    List<ExamItem> findByEquipmentType(String equipmentType);

    @Query("SELECT i FROM ExamItem i WHERE i.status = '启用' ORDER BY i.sortOrder")
    List<ExamItem> findActiveItems();

    @Query("SELECT i FROM ExamItem i WHERE i.needContrast = true AND i.status = '启用'")
    List<ExamItem> findContrastItems();

    @Query("SELECT i FROM ExamItem i WHERE " +
           "(:itemCode IS NULL OR i.itemCode LIKE :itemCode) AND " +
           "(:itemName IS NULL OR i.itemName LIKE :itemName) AND " +
           "(:examType IS NULL OR i.examType = :examType) AND " +
           "(:examPart IS NULL OR i.examPart = :examPart) AND " +
           "(:status IS NULL OR i.status = :status)")
    Page<ExamItem> findByConditions(
            @Param("itemCode") String itemCode,
            @Param("itemName") String itemName,
            @Param("examType") String examType,
            @Param("examPart") String examPart,
            @Param("status") String status,
            Pageable pageable);
}