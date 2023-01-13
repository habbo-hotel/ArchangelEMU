package com.eu.habbo.habbohotel.commands.pixels;

import com.eu.habbo.habbohotel.commands.Command;

public abstract class BasePixelsCommand extends Command {
    public BasePixelsCommand(String permission, String[] keys) {
        super(permission, keys);
    }

    protected String replaceAmount(String input, String amount) {
        return input.replace("%amount%", amount);
    }

    protected String replaceUserAndAmount(String input, String user, String amount) {
        return replaceAmount(replaceUser(input, user), amount);
    }
}
