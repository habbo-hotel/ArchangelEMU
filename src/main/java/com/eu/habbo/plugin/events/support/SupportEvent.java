package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class SupportEvent extends Event {
    protected final Habbo moderator;
}