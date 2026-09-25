package org.flexitech.projects.erp.commons.abstractions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreeGiftRewardConfig extends RewardConfig {
	private String giftItemSku;
    private Integer quantity;
    private Boolean allowOutOfStockReplacement;
}