package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class UpdateHotelViewCommand extends Command {
    public UpdateHotelViewCommand() {
        super("cmd_update_hotel_view");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getHotelViewManager().getNewsList().reload();
        Emulator.getGameEnvironment().getHotelViewManager().getHallOfFame().reload();

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_hotel_view"));

        return true;
    }
}