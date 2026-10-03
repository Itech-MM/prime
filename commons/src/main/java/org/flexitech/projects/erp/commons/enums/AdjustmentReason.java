package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

public enum AdjustmentReason {

    COUNT_DIFF(1, "Stock Count Difference"),
    DAMAGE(2, "Damage"),
    LOSS(3, "Loss"),
    FOUND(4, "Found"),
    OPENING_BALANCE(5, "Opening Balance");

    private final int code;
    private final String desc;

    AdjustmentReason(int code, String desc) {
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
        for (AdjustmentReason reason : values()) {
            if (reason.code == code) {
                return reason.desc;
            }
        }
        return "";
    }
    
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (AdjustmentReason s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}