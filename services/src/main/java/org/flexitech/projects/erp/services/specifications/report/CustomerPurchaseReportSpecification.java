package org.flexitech.projects.erp.services.specifications.report;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.report.CustomerPurchaseReportSearchDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CustomerPurchaseReportSpecification {
	public static Specification<License> withSearchCriteria(CustomerPurchaseReportSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (CommonValidators.validLong(searchDTO.getCustomerId())) {
				predicates.add(criteriaBuilder.equal(root.get("customerProduct").get("customer").get("id"), searchDTO.getCustomerId()));
			}

			if (CommonValidators.validLong(searchDTO.getProductId())) {
				predicates.add(criteriaBuilder.equal(root.get("plan").get("product").get("id"), searchDTO.getProductId()));
			}

			if (CommonValidators.validLong(searchDTO.getPlanId())) {
				predicates.add(criteriaBuilder.equal(root.get("plan").get("id"), searchDTO.getPlanId()));
			}

			if (CommonValidators.validString(searchDTO.getStartDate())) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("issuedAt"),
						DateUtils.stringToDate(searchDTO.getStartDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT)));
			}

			if (CommonValidators.validString(searchDTO.getEndDate())) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("issuedAt"),
						DateUtils.stringToDate(searchDTO.getEndDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT)));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}