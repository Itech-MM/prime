package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, Long>, JpaSpecificationExecutor<GoodsReceipt> {
    Optional<GoodsReceipt> findByDocNo(String docNo);
    boolean existsByDocNo(String docNo);
    boolean existsBySupplierId(Long supplierId);
    boolean existsByWarehouseId(Long warehouseId);
}