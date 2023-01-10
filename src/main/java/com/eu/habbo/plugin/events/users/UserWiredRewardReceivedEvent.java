package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

@Getter
public class UserWiredRewardReceivedEvent extends UserEvent {
    private final WiredEffectGiveReward wiredEffectGiveReward;
    private final String type;
    private final String value;


    public UserWiredRewardReceivedEvent(Habbo habbo, WiredEffectGiveReward wiredEffectGiveReward, String type, String value) {
        super(habbo);

        this.wiredEffectGiveReward = wiredEffectGiveReward;
        this.type = type;
        this.value = value;
    }
}
