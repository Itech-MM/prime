package org.flexitech.projects.erp.persistence.entities.inventory;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.INV_AUDIT_LOG_TBL)
@Getter
@Setter
public class InventoryAuditLog extends BasedEntity {

	@Column(name = "doc_type")
	private String docType;

	@Column(name = "doc_id")
	private Long docId;

	@Column(name = "doc_no")
	private String docNo;

	private Integer action;

	private String remarks;
}