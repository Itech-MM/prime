package org.flexitech.projects.erp.persistence.entities.license;

import java.util.Date;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.LICENSE_TBL)
@Getter
@Setter
public class License extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "plan_id")
	private LicensePlan plan;

	@Column(name = "code", unique = true)
	private String code;

	@Column(name = "fingerprint")
	private String fingerprint;

	private Integer status;

	@Column(name = "issued_at")
	private Date issuedAt;

	@Column(name = "expires_at")
	private Date expiresAt;

	@Column(name = "grace_days")
	private Integer graceDays;
}