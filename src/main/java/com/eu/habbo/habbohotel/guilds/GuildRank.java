package com.eu.habbo.habbohotel.guilds;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuildRank {
    OWNER(0),
    ADMIN(1),
    MEMBER(2),
    REQUESTED(3),
    DELETED(4);

    private final int type;

    public static GuildRank getRank(int type) {
        try {
            return values()[type];
        } catch (Exception e) {
            return MEMBER;
        }
    }
}
