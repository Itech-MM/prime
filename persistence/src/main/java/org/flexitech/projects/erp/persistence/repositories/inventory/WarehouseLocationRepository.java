package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, Long>, JpaSpecificationExecutor<WarehouseLocation> {
    List<WarehouseLocation> findByWarehouseId(Long warehouseId);
    List<WarehouseLocation> findByParentId(Long parentId);
    Optional<WarehouseLocation> findByWarehouseIdAndCode(Long warehouseId, String code);
    Optional<WarehouseLocation> findFirstByWarehouseIdAndType(Long warehouseId, Integer type);
    boolean existsByWarehouseId(Long warehouseId);
    boolean existsByParentId(Long parentId);
    boolean existsByWarehouseIdAndCode(Long warehouseId, String code);
    boolean existsByWarehouseIdAndCodeAndIdNot(Long warehouseId, String code, Long id);
}