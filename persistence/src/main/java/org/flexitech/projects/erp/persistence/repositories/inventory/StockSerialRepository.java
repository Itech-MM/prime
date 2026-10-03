package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.StockSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockSerialRepository extends JpaRepository<StockSerial, Long>, JpaSpecificationExecutor<StockSerial> {
    Optional<StockSerial> findByItemIdAndSerialNo(Long itemId, String serialNo);
    List<StockSerial> findByItemIdAndStatus(Long itemId, Integer status);
    boolean existsByItemIdAndSerialNo(Long itemId, String serialNo);
    boolean existsByItemId(Long itemId);
}