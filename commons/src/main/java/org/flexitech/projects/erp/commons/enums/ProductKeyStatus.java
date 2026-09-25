package org.flexitech.projects.erp.commons.enums;

public enum ProductKeyStatus {

	ACTIVE(1, "Active"),
	ROTATED(2, "Rotated"),
	REVOKED(3, "Revoked");

	private final Integer code;
	private final String desc;

	ProductKeyStatus(Integer code, String desc) {
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
		for (ProductKeyStatus status : values()) {
			if (status.getCode().equals(code)) {
				return status.getDesc();
			}
		}
		return null;
	}
}