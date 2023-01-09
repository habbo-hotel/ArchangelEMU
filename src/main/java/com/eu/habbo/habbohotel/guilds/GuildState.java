package com.eu.habbo.habbohotel.guilds;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GuildState {
    OPEN(0),
    EXCLUSIVE(1),
    CLOSED(2),
    LARGE(3),
    LARGE_CLOSED(4);

    private final int state;


    public static GuildState valueOf(int state) {
        try {
            return values()[state];
        } catch (Exception e) {
            return OPEN;
        }
    }
}
