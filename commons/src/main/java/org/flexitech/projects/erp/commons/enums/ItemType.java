package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum ItemType {

    STOCK(1, "Stock"),
    CONSUMABLE(2, "Consumable"),
    SERVICE(3, "Service"),
    ASSET(4, "Asset");

    private final int code;
    private final String desc;

    ItemType(int code, String desc) {
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
        for (ItemType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "";
    }
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (ItemType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}