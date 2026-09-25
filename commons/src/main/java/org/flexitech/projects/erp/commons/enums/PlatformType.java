package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum PlatformType {
	WEBSITE(1, "Website"), MOBILE(2, "Mobile");

	private final Integer code;
	private final String desc;

	PlatformType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PlatformType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (PlatformType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
