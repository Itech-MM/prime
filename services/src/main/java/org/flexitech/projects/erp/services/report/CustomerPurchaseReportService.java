package org.flexitech.projects.erp.services.report;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSearchDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSummaryDTO;
import org.springframework.data.domain.Pageable;

public interface CustomerPurchaseReportService {

	SearchResultDTO<LicenseDTO> searchPurchases(CustomerPurchaseReportSearchDTO searchDTO, Pageable pageable) throws Exception;

	CustomerPurchaseReportSummaryDTO getSummary(CustomerPurchaseReportSearchDTO searchDTO) throws Exception;
}