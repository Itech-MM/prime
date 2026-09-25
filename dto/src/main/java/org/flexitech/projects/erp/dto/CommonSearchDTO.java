package org.flexitech.projects.erp.dto;

import org.flexitech.projects.erp.commons.CommonConstants;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonSearchDTO {
	private Integer pageNo;
	private Integer limit = CommonConstants.ROW_PER_PAGE;
	private Integer status;
}
