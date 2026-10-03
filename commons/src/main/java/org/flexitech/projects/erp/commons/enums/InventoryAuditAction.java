package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum InventoryAuditAction {

	CREATED(1, "Created"), UPDATED(2, "Updated"), SUBMITTED(3, "Submitted"), APPROVED(4, "Approved"),
	POSTED(5, "Posted"), CANCELLED(6, "Cancelled"), DELETED(7, "Deleted");

	private final Integer code;
	private final String desc;

	InventoryAuditAction(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (InventoryAuditAction a : values()) {
			result.add(new CommonEnumObject(a.code, a.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (InventoryAuditAction a : values()) {
			if (a.code.equals(code))
				return a.desc;
		}
		return null;
	}
}