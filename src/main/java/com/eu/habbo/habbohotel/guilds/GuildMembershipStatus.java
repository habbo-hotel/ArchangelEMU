package com.eu.habbo.habbohotel.guilds;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GuildMembershipStatus {
    NOT_MEMBER(0),
    MEMBER(1),
    PENDING(2);

    @Getter
    private final int status;

}
