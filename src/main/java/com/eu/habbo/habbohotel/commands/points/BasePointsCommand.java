package com.eu.habbo.habbohotel.commands.points;

import com.eu.habbo.habbohotel.commands.Command;

public abstract class BasePointsCommand extends Command {

    public BasePointsCommand(String permission, String[] keys) {
        super(permission, keys);
    }

    protected String replaceAmountAndType(String input, String amount, String type) {
        return input.replace("%amount%", amount).replace("%type%", type);
    }

    protected String replaceUserAndAmountAndType(String input, String user, String amount, String type) {
        return replaceAmountAndType(replaceUser(input, user), amount, type);
    }
}
