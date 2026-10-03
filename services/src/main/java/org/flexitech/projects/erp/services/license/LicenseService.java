package org.flexitech.projects.erp.services.license;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.license.LicenseSearchDTO;
import org.springframework.data.domain.Pageable;

public interface LicenseService {

	LicenseDTO createLicenseForCustomerProduct(Long customerProductId, Long planId) throws Exception;

	LicenseDTO getLicenseById(Long id) throws Exception;

	LicenseDTO getLicenseByCustomerProductId(Long customerProductId) throws Exception;

	SearchResultDTO<LicenseDTO> searchLicenses(LicenseSearchDTO searchDTO, Pageable pageable) throws Exception;

	LicenseDTO reissueToken(Long licenseId, String fingerprint) throws Exception;
}