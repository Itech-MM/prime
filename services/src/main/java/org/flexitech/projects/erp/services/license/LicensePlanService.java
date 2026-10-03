package org.flexitech.projects.erp.services.license;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanDTO;
import org.flexitech.projects.erp.dto.license.LicensePlanSearchDTO;
import org.springframework.data.domain.Pageable;

public interface LicensePlanService {

	LicensePlanDTO managePlan(LicensePlanDTO planDTO) throws Exception;

	LicensePlanDTO getPlanById(Long id) throws Exception;

	SearchResultDTO<LicensePlanDTO> searchPlans(LicensePlanSearchDTO searchDTO, Pageable pageable) throws Exception;

	boolean deletePlan(Long id) throws Exception;
}