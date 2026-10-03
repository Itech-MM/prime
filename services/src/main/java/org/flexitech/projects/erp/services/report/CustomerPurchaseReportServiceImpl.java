package org.flexitech.projects.erp.services.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.utils.CommonUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSearchDTO;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSummaryDTO;
import org.flexitech.projects.erp.dto.report.ReportGroupDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.flexitech.projects.erp.services.specifications.report.CustomerPurchaseReportSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CustomerPurchaseReportServiceImpl implements CustomerPurchaseReportService {

	private final LicenseRepository licenseRepository;

	public CustomerPurchaseReportServiceImpl(LicenseRepository licenseRepository) {
		this.licenseRepository = licenseRepository;
	}

	@Override
	public SearchResultDTO<LicenseDTO> searchPurchases(CustomerPurchaseReportSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<License> spec = CustomerPurchaseReportSpecification.withSearchCriteria(searchDTO);
			Page<License> page = licenseRepository.findAll(spec, pageable);

			SearchResultDTO<LicenseDTO> result = new SearchResultDTO<>();
			result.setPageNo(page.getNumber());
			result.setLimit(page.getSize());
			result.setTotalPage(page.getTotalPages());
			result.setTotalRecords((int) page.getTotalElements());
			result.setPageCount(page.getNumberOfElements());
			result.setHasNextPage(page.hasNext());
			result.setResults(page.getContent().stream().map(LicenseDTO::new).collect(Collectors.toList()));
			return result;
		} catch (Exception e) {
			throw new Exception("Error searching customer purchases: " + e.getMessage(), e);
		}
	}

	@Override
	public CustomerPurchaseReportSummaryDTO getSummary(CustomerPurchaseReportSearchDTO searchDTO) throws Exception {
		Specification<License> spec = CustomerPurchaseReportSpecification.withSearchCriteria(searchDTO);
		List<License> licenses = this.licenseRepository.findAll(spec);

		CustomerPurchaseReportSummaryDTO summary = new CustomerPurchaseReportSummaryDTO();
		summary.setTotalCount(licenses.size());

		BigDecimal total = licenses.stream()
				.map(l -> l.getPricePaid() != null ? l.getPricePaid() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		summary.setTotalAmount(total);
		summary.setTotalAmountDesc(CommonUtils.formatNumber(total));

		Map<String, List<License>> byProductMap = licenses.stream()
				.collect(Collectors.groupingBy(l -> l.getPlan().getProduct().getName()));
		summary.setByProduct(toGroupList(byProductMap));

		Map<String, List<License>> byCustomerMap = licenses.stream()
				.collect(Collectors.groupingBy(l -> l.getCustomerProduct().getCustomer().getName()));
		summary.setByCustomer(toGroupList(byCustomerMap));

		return summary;
	}

	private List<ReportGroupDTO> toGroupList(Map<String, List<License>> grouped) {
		return grouped.entrySet().stream()
				.map(e -> {
					BigDecimal sum = e.getValue().stream()
							.map(l -> l.getPricePaid() != null ? l.getPricePaid() : BigDecimal.ZERO)
							.reduce(BigDecimal.ZERO, BigDecimal::add);
					return new ReportGroupDTO(e.getKey(), sum, e.getValue().size());
				})
				.sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
				.collect(Collectors.toList());
	}
}