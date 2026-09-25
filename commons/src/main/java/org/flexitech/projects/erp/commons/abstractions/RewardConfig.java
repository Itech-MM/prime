package org.flexitech.projects.erp.commons.abstractions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DiscountRewardConfig.class, name = "1"),
    @JsonSubTypes.Type(value = CashbackRewardConfig.class, name = "2"),
    @JsonSubTypes.Type(value = FreeGiftRewardConfig.class, name = "3"),
    @JsonSubTypes.Type(value = TieredRewardConfig.class, name = "4")
})
public abstract class RewardConfig {
    private Integer type;
}