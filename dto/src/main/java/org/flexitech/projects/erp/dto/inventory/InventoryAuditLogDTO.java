package org.flexitech.projects.erp.dto.inventory;

import org.flexitech.projects.erp.commons.enums.InventoryAuditAction;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.InventoryAuditLog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAuditLogDTO extends CommonDTO {

	private String docType;
	private Long docId;
	private String docNo;
	private Integer action;
	private String actionDesc;
	private String remarks;

	public InventoryAuditLogDTO(InventoryAuditLog entry) {
		super(entry);
		this.docType = entry.getDocType();
		this.docId = entry.getDocId();
		this.docNo = entry.getDocNo();
		this.action = entry.getAction();
		this.actionDesc = InventoryAuditAction.getDescByCode(entry.getAction());
		this.remarks = entry.getRemarks();
	}
}