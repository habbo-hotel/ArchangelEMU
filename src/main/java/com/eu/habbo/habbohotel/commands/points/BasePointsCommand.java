package com.eu.habbo.habbohotel.commands.points;

import com.eu.habbo.habbohotel.commands.Command;

public abstract class BasePointsCommand extends Command {
    public BasePointsCommand(String permission, String[] keys) {
        super(permission, keys);
    }
}
