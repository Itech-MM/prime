package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum StockMovementType {

    RECEIPT(1, "Receipt"),
    ISSUE(2, "Issue"),
    TRANSFER_OUT(3, "Transfer Out"),
    TRANSFER_IN(4, "Transfer In"),
    ADJUST_IN(5, "Adjustment In"),
    ADJUST_OUT(6, "Adjustment Out"),
    RETURN_IN(7, "Return In"),
    RETURN_OUT(8, "Return Out"),
    SCRAP(9, "Scrap");

    private final Integer code;
    private final String desc;

    StockMovementType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (StockMovementType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "";
    }
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (StockMovementType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}