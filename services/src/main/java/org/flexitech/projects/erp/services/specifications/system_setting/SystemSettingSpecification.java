package org.flexitech.projects.erp.services.specifications.system_setting;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.system_setting.SystemSettingSearchDTO;
import org.flexitech.projects.erp.persistence.entities.system_setting.SystemSetting;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class SystemSettingSpecification {
	public static Specification<SystemSetting> withSearchCriteria(SystemSettingSearchDTO searchDTO) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();


			if (CommonValidators.validString(searchDTO.getCode())) {
				predicates.add(criteriaBuilder.equal(root.get("code"), searchDTO.getCode()));
			}
			
			if(CommonValidators.validInteger(searchDTO.getEditableStatus())) {
				predicates.add(criteriaBuilder.equal(root.get("editableStatus"), searchDTO.getEditableStatus()));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
