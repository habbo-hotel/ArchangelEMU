package com.eu.habbo.habbohotel.guilds;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SettingsState {
    EVERYONE(0),
    MEMBERS(1),
    ADMINS(2),
    OWNER(3);

    private final int state;

    public static SettingsState fromValue(int state) {
        try {
            switch (state) {
                case 0:
                    return EVERYONE;
                case 1:
                    return MEMBERS;
                case 2:
                    return ADMINS;
                case 3:
                    return OWNER;
            }
        } catch (Exception ignored) {
        }

        return EVERYONE;
    }
}