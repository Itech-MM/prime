package org.flexitech.projects.erp.commons.enums;

import lombok.Getter;

@Getter
public enum StockSerialStatus {

    IN_STOCK(1, "In Stock"),
    ISSUED(2, "Issued"),
    RETURNED(3, "Returned"),
    SCRAPPED(4, "Scrapped");

    private final Integer code;
    private final String desc;

    StockSerialStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (StockSerialStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "";
    }
}