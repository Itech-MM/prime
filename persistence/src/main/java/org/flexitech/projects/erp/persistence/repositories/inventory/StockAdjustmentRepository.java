package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long>, JpaSpecificationExecutor<StockAdjustment> {
    Optional<StockAdjustment> findByDocNo(String docNo);
    boolean existsByDocNo(String docNo);
    boolean existsByWarehouseId(Long warehouseId);
}