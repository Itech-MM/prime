package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.InventoryAuditLogDTO;
import org.flexitech.projects.erp.dto.inventory.search.InventoryAuditLogSearchDTO;
import org.springframework.data.domain.Pageable;

public interface InventoryAuditLogService {

	void log(String docType, Long docId, String docNo, Integer action, String remarks);
	
	SearchResultDTO<InventoryAuditLogDTO> searchAuditLog(InventoryAuditLogSearchDTO searchDTO, Pageable pageable) throws Exception;

}