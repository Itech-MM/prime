package org.flexitech.projects.erp.persistence.entities.menu;

import org.flexitech.projects.erp.commons.TableNames;
import org.flexitech.projects.erp.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableNames.MENU_TBL)
public class Menu extends BasedEntity{
	private String name;
	@Column(name = "name_mm")
	private String nameMm;
	private String code;
	private String description;
	private String icon;
	private String url;
	private Integer status;
	private Integer sequence;
	
	@Column(name = "menu_group_code")
	private Integer menuGroupCode;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Menu parentMenu;
	
	@Column(name = "display_status")
	private Integer displayStatus;
}
