package org.flexitech.projects.erp.persistence.repositories.inventory;

import org.flexitech.projects.erp.persistence.entities.inventory.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long>, JpaSpecificationExecutor<InventoryAuditLog> {
}