package org.flexitech.projects.erp.persistence.entities.system_setting;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableNames.SYSTEM_SETTING_TBL)
public class SystemSetting extends BasedEntity{
	
	@Column(unique = true, nullable = false)
	private String code;
	private String description;
	private String value;
	private Integer editableStatus;
	private Integer sequence;
	@Column(name = "input_type")
	private Integer inputType;
	
	private String icon;
	
}
