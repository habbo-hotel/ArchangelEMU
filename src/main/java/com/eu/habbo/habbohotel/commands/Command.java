package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Command {

    public final String permission;
    public final String[] keys;


    public abstract boolean handle(GameClient gameClient, String[] params) throws Exception;
}