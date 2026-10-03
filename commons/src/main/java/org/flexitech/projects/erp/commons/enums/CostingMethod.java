package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum CostingMethod {

    FIFO(1, "FIFO"),
    WEIGHTED_AVG(2, "Weighted Average"),
    STANDARD(3, "Standard Cost");

    private final int code;
    private final String desc;

    CostingMethod(int code, String desc) {
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
        for (CostingMethod method : values()) {
            if (method.code == code) {
                return method.desc;
            }
        }
        return "";
    }

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (CostingMethod s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}