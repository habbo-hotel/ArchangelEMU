package com.eu.habbo.habbohotel.commands.interfaces;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public interface ICommand {
    String getName();
    boolean handle(GameClient gameClient, String[] params) throws Exception;
}
