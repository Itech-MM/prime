package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long>, JpaSpecificationExecutor<StockBatch> {
    Optional<StockBatch> findByItemIdAndBatchNo(Long itemId, String batchNo);
    List<StockBatch> findByItemId(Long itemId);
    List<StockBatch> findByExpiryDateBefore(Date date);
    boolean existsByItemId(Long itemId);
    boolean existsBySupplierId(Long supplierId);
}