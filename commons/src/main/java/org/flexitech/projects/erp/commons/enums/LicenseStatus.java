package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum LicenseStatus {

	NOT_ACTIVATED(0, "Not Activated"),
	ACTIVE(1, "Active"),
	GRACE(2, "Grace Period"),
	EXPIRED(3, "Expired"),
	REVOKED(4, "Revoked"),
	INVALID(5, "Invalid");

	private final Integer code;
	private final String desc;

	LicenseStatus(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static String getDescByCode(Integer code) {
		for (LicenseStatus status : values()) {
			if (status.getCode().equals(code)) {
				return status.getDesc();
			}
		}
		return null;
	}
	
	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (LicenseStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}