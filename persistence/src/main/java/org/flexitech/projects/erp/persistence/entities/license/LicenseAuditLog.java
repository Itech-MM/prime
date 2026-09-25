package org.flexitech.projects.erp.persistence.entities.license;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.LICENSE_AUDIT_LOG_TBL)
@Getter
@Setter
public class LicenseAuditLog extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "license_id")
	private License license;

	@Column(name = "event_type")
	private String eventType;

	@Column(name = "event_time")
	private Date eventTime;

	@Lob
	private String details;
}