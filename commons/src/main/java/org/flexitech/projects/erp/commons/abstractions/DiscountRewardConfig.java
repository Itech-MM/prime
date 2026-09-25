package org.flexitech.projects.erp.commons.abstractions;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountRewardConfig extends RewardConfig {
	private String currency;
    private BigDecimal cashValue;
    private Integer loyaltyPoints;
    private Integer expireInDays;
}

