package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.List;

import org.flexitech.projects.erp.persistence.entities.inventory.StockLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockLedgerRepository extends JpaRepository<StockLedger, Long>, JpaSpecificationExecutor<StockLedger> {
    Page<StockLedger> findByItemId(Long itemId, Pageable pageable);
    List<StockLedger> findByRefDocTypeAndRefDocId(String refDocType, Long refDocId);
    boolean existsByItemId(Long itemId);
    boolean existsByLocationId(Long locationId);
}