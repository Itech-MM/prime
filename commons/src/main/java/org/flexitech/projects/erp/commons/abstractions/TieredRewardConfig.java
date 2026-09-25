package org.flexitech.projects.erp.commons.abstractions;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TieredRewardConfig extends RewardConfig {
	private List<RewardTier> tiers;

    @Getter
    @Setter
    public static class RewardTier {
        private Integer minQty;
        private Integer discountPercent;
    } 
}