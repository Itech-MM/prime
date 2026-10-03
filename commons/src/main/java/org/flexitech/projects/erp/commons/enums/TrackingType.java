package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum TrackingType {

    NONE(1, "None"),
    BATCH(2, "Batch"),
    SERIAL(3, "Serial");

    private final Integer code;
    private final String desc;

    TrackingType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (TrackingType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "";
    }

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (TrackingType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}