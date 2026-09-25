package org.flexitech.projects.erp.dto.system_setting;

import org.flexitech.projects.erp.dto.CommonSearchDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SystemSettingSearchDTO extends CommonSearchDTO{
	private String code;
	private Integer editableStatus;
}
