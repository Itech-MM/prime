package org.flexitech.projects.erp.commons.abstractions;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashbackRewardConfig extends RewardConfig {
    private String discountMethod; 
    private BigDecimal discountValue;
    private BigDecimal maxDiscountLimit;
}