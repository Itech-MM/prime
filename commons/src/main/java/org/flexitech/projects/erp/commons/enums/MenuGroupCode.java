package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum MenuGroupCode {
	CORE(1, "Core"), OPERATIONS(2, "Operations"), MANGEMENTS(3, "Managements"), INVENTORY(5, "Inventory"), REPORTS(4, "Reports");

	private final Integer code;
	private final String desc;

	MenuGroupCode(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (MenuGroupCode s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (MenuGroupCode s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}