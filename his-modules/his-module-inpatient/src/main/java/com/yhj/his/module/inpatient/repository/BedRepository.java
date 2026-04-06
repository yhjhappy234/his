package com.yhj.his.module.inpatient.repository;

import com.yhj.his.module.inpatient.entity.Bed;
import com.yhj.his.module.inpatient.enums.BedStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 床位Repository
 */
@Repository
public interface BedRepository extends JpaRepository<Bed, String> {

    /**
     * 根据病区ID和床位号查询
     */
    Optional<Bed> findByWardIdAndBedNo(String wardId, String bedNo);

    /**
     * 根据病区ID查询所有床位
     */
    List<Bed> findByWardId(String wardId);

    /**
     * 根据病区ID分页查询床位
     */
    Page<Bed> findByWardId(String wardId, Pageable pageable);

    /**
     * 分页查询所有床位
     */
    Page<Bed> findAll(Pageable pageable);

    /**
     * 根据病区ID和状态查询床位
     */
    List<Bed> findByWardIdAndStatus(String wardId, BedStatus status);

    /**
     * 根据状态查询床位
     */
    List<Bed> findByStatus(BedStatus status);

    /**
     * 根据住院ID查询床位
     */
    Optional<Bed> findByAdmissionId(String admissionId);

    /**
     * 统计病区床位数量
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId")
    Long countByWardId(@Param("wardId") String wardId);

    /**
     * 统计病区各状态床位数量
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId AND b.status = :status")
    Long countByWardIdAndStatus(@Param("wardId") String wardId, @Param("status") BedStatus status);

    /**
     * 查询病区空床数量
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId AND b.status = 'VACANT'")
    Long countVacantByWardId(@Param("wardId") String wardId);

    /**
     * 查询病区占用床位数量
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.wardId = :wardId AND b.status = 'OCCUPIED'")
    Long countOccupiedByWardId(@Param("wardId") String wardId);

    /**
     * 查询病房号列表
     */
    @Query("SELECT DISTINCT b.roomNo FROM Bed b WHERE b.wardId = :wardId ORDER BY b.roomNo")
    List<String> findRoomNosByWardId(@Param("wardId") String wardId);
}