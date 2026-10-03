package org.flexitech.projects.erp.dto.menu;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.ActiveStatus;
import org.flexitech.projects.erp.commons.enums.MenuGroupCode;
import org.flexitech.projects.erp.dto.CommonDTO;
import org.flexitech.projects.erp.persistence.entities.menu.Menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuDTO extends CommonDTO{
	private String name;
	private String nameMm;
	private String code;
	private String description;
	private String icon;
	private String url;
	private Integer status;
	private String statusDesc;
	private Integer sequence;
	
	private String parentMenuName;
	private Long parentMenuId;
	
	private Integer menuGroupCode;
	private String menuGroupCodeDesc;
	
	private List<MenuDTO> children = new ArrayList<MenuDTO>();
	
	public MenuDTO(Menu m) {
		super(m);
		this.name = m.getName();
		this.nameMm = m.getNameMm();
		this.code = m.getCode();
		this.description = m.getDescription();
		this.url = m.getUrl();
		this.icon = m.getIcon();
		this.status = m.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.sequence = m.getSequence();
		
		this.menuGroupCode = m.getMenuGroupCode();
		this.menuGroupCodeDesc = MenuGroupCode.getDescByCode(menuGroupCode);
				
		if(CommonValidators.isValidObject(m.getParentMenu())) {
			this.parentMenuId = m.getParentMenu().getId();
			this.parentMenuName = m.getParentMenu().getName();
		}
	}
	
}
