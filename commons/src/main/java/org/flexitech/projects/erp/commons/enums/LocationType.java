package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum LocationType {

    INTERNAL(1, "Internal"),
    TRANSIT(2, "In Transit"),
    SCRAP(3, "Scrap"),
    VIRTUAL_SUPPLIER(4, "Virtual Supplier"),
    VIRTUAL_CUSTOMER(5, "Virtual Customer");

    private final int code;
    private final String desc;

    LocationType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (LocationType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "";
    }
    
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (LocationType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}