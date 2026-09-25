package org.flexitech.projects.erp.persistence.entities.license;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.LICENSE_ACTIVATION_TBL)
@Getter
@Setter
public class LicenseActivation extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "license_id")
	private License license;

	@Column(name = "machine_fingerprint")
	private String machineFingerprint;

	@Column(name = "activated_at")
	private Date activatedAt;

	@Column(name = "last_checkin_at")
	private Date lastCheckinAt;

	private Integer status;
}