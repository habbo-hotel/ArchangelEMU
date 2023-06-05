package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class BotsCommand extends Command {
    public BotsCommand() {
        super("cmd_bots");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null || !gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo()))
            return false;

        StringBuilder data = new StringBuilder(getTextsValue("total") + ": " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentBots().values().length);

        for (Bot bot : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentBots().valueCollection()) {
            data.append("\r");
            data.append("<b>");
            data.append(Emulator.getTexts().getValue("generic.bot.name"));
            data.append("</b>: ");
            data.append(bot.getName());
            data.append(" <b>");
            data.append(Emulator.getTexts().getValue("generic.bot.id"));
            data.append("</b>: ");
            data.append(bot.getId());
        }

        gameClient.getHabbo().alert(data.toString());

        return true;
    }
}
