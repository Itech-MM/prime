package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum InventoryDocStatus {

    DRAFT(1, "Draft"),
    SUBMITTED(2, "Submitted"),
    APPROVED(3, "Approved"),
    POSTED(4, "Posted"),
    CANCELLED(5, "Cancelled");

    private final Integer code;
    private final String desc;

    InventoryDocStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (InventoryDocStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "";
    }
    
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (InventoryDocStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}