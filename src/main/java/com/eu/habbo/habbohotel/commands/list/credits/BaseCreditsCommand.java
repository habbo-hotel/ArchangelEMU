package com.eu.habbo.habbohotel.commands.list.credits;

import com.eu.habbo.habbohotel.commands.Command;

public abstract class BaseCreditsCommand extends Command {

    public BaseCreditsCommand(String name)
    {
        super(name);
    }

    protected String replaceAmount(String input, String amount) {
        return input.replace("%amount%", amount);
    }

    protected String replaceUserAndAmount(String input, String user, String amount) {
        return replaceAmount(replaceUser(input, user), amount);
    }
}
