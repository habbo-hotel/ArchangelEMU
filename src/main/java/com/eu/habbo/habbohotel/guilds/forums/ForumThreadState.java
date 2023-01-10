package com.eu.habbo.habbohotel.guilds.forums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ForumThreadState {
    OPEN(0),
    CLOSED(1),
    HIDDEN_BY_STAFF_MEMBER(10),
    HIDDEN_BY_GUILD_ADMIN(20);

    @Getter
    private final int stateId;


    public static ForumThreadState fromValue(int value) {
        for (ForumThreadState state : ForumThreadState.values()) {
            if (state.stateId == value) {
                return state;
            }
        }

        return CLOSED;
    }

}
