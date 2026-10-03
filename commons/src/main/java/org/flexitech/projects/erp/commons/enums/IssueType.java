package org.flexitech.projects.erp.commons.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.CommonEnumObject;

import lombok.Getter;

@Getter
public enum IssueType {

    SALE(1, "Sale"),
    PROJECT(2, "Project"),
    INTERNAL_USE(3, "Internal Use"),
    DAMAGED(4, "Damaged");

    private final Integer code;
    private final String desc;

    IssueType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (IssueType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "";
    }
    
    public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (IssueType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}
}