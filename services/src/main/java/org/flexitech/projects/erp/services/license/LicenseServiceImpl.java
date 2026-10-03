package org.flexitech.projects.erp.services.license;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.enums.LicenseStatus;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.license.LicenseSearchDTO;
import org.flexitech.projects.erp.persistence.entities.customer.CustomerProduct;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;
import org.flexitech.projects.erp.persistence.repositories.customer.CustomerProductRepository;
import org.flexitech.projects.erp.persistence.repositories.license.LicensePlanRepository;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.flexitech.projects.erp.services.specifications.license.LicenseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class LicenseServiceImpl implements LicenseService {

	private static final int DEFAULT_GRACE_DAYS = 7;

	private final LicenseRepository licenseRepository;
	private final CustomerProductRepository customerProductRepository;
	private final LicensePlanRepository licensePlanRepository;
	private final LicenseTokenIssuer licenseTokenIssuer;
	private final LicenseAuditLogService licenseAuditLogService;

	public LicenseServiceImpl(LicenseRepository licenseRepository, CustomerProductRepository customerProductRepository,
			LicensePlanRepository licensePlanRepository, LicenseTokenIssuer licenseTokenIssuer,
			LicenseAuditLogService licenseAuditLogService) {
		this.licenseRepository = licenseRepository;
		this.customerProductRepository = customerProductRepository;
		this.licensePlanRepository = licensePlanRepository;
		this.licenseTokenIssuer = licenseTokenIssuer;
		this.licenseAuditLogService = licenseAuditLogService;
	}

	@Override
	public LicenseDTO createLicenseForCustomerProduct(Long customerProductId, Long planId) throws Exception {
		CustomerProduct customerProduct = this.customerProductRepository.findById(customerProductId)
				.orElseThrow(() -> new Exception("Customer product not found!"));
		LicensePlan plan = this.licensePlanRepository.findById(planId)
				.orElseThrow(() -> new Exception("License plan not found!"));

		if (!plan.getProduct().getId().equals(customerProduct.getProduct().getId())) {
			throw new Exception("Selected plan does not belong to the selected product!");
		}

		License license = new License();
		license.setCustomerProduct(customerProduct);
		license.setPlan(plan);
		license.setCode(generateLicenseCode(plan));
		license.setIssuedAt(Calendar.getInstance().getTime());
		license.setStatus(LicenseStatus.NOT_ACTIVATED.getCode());
		license.setGraceDays(DEFAULT_GRACE_DAYS);
		license.setPricePaid(plan.getPrice());

		Calendar expiry = Calendar.getInstance();
		expiry.add(Calendar.DATE, plan.getDurationDays());
		license.setExpiresAt(expiry.getTime());

		License saved = this.licenseRepository.save(license);
		this.licenseAuditLogService.log(saved.getId(), "LICENSE_CREATED", "License created for code " + saved.getCode());

		return this.licenseTokenIssuer.issueToken(saved.getId());
	}

	@Override
	public LicenseDTO getLicenseById(Long id) throws Exception {
		License license = this.licenseRepository.findById(id)
				.orElseThrow(() -> new Exception("License not found!"));
		return new LicenseDTO(license);
	}

	@Override
	public LicenseDTO getLicenseByCustomerProductId(Long customerProductId) throws Exception {
		return this.licenseRepository.findByCustomerProductId(customerProductId)
				.map(LicenseDTO::new)
				.orElse(null);
	}

	@Override
	public SearchResultDTO<LicenseDTO> searchLicenses(LicenseSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<License> spec = LicenseSpecification.withSearchCriteria(searchDTO);
			Page<License> licensePage = licenseRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(licensePage);
		} catch (Exception e) {
			throw new Exception("Error searching licenses: " + e.getMessage(), e);
		}
	}

	@Override
	public LicenseDTO reissueToken(Long licenseId, String fingerprint) throws Exception {
		License license = this.licenseRepository.findById(licenseId)
				.orElseThrow(() -> new Exception("License not found!"));
		license.setFingerprint(fingerprint);
		license.setStatus(LicenseStatus.ACTIVE.getCode());
		this.licenseRepository.save(license);
		this.licenseAuditLogService.log(licenseId, "FINGERPRINT_REBOUND", "Fingerprint updated and token reissued");
		return this.licenseTokenIssuer.issueToken(licenseId);
	}

	private String generateLicenseCode(LicensePlan plan) {
		String prefix = "PRIME-" + plan.getProduct().getCode() + "-";
		long count = this.licenseRepository.countByPlanIdAndCodeStartingWith(plan.getId(), prefix) + 1;
		return prefix + String.format("%06d", count);
	}

	private SearchResultDTO<LicenseDTO> convertToCommonSearchDTO(Page<License> licensePage) {
		SearchResultDTO<LicenseDTO> result = new SearchResultDTO<>();

		result.setPageNo(licensePage.getNumber());
		result.setLimit(licensePage.getSize());
		result.setTotalPage(licensePage.getTotalPages());
		result.setTotalRecords((int) licensePage.getTotalElements());
		result.setPageCount(licensePage.getNumberOfElements());
		result.setHasNextPage(licensePage.hasNext());

		List<LicenseDTO> licenseDTOs = licensePage.getContent().stream().map(LicenseDTO::new).collect(Collectors.toList());

		result.setResults(licenseDTOs);
		return result;
	}
}