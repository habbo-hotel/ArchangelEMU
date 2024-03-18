package com.eu.habbo.plugin.events.sanctions;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.support.SupportEvent;
import lombok.Getter;

@Getter
public class SanctionEvent extends SupportEvent {
    private final Habbo target;

    private final int sanctionLevel;

    public SanctionEvent(Habbo moderator, Habbo target, int sanctionLevel) {
        super(moderator);

        this.target = target;
        this.sanctionLevel = sanctionLevel;
    }
}
