package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.interfaces.ICommand;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;

public abstract class Command implements ICommand {
    @Getter
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    public abstract boolean handle(GameClient gameClient, String[] params) throws Exception;

    protected Habbo getHabbo(String username) {
        return Emulator.getGameEnvironment().getHabboManager().getHabbo(username);
    }

    protected String getTextsValue(String key) {
        return Emulator.getTexts().getValue(key);
    }

    protected String getTextsValue(String key, String defaultValue) {
        return Emulator.getTexts().getValue(key, defaultValue);
    }

    protected String replaceUser(String input, String user) {
        return input.replace("%user%", user);
    }

    protected String replaceUsername(String input, String user) {
        return input.replace("%username%", user);
    }

    protected int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    protected String booleanToIntString(boolean value) {
        return value ? "1" : "0";
    }
}
