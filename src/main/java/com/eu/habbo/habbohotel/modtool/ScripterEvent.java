package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.support.SupportEvent;
import lombok.Getter;

@Getter
public class ScripterEvent extends SupportEvent {
    private  final Habbo habbo;
    private  final String reason;

    public ScripterEvent(Habbo habbo, String reason) {
        super(null);

        this.habbo = habbo;
        this.reason = reason;
    }
}
