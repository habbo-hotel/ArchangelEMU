package com.eu.habbo.habbohotel.commands.pixels;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class RoomPixelsCommand extends BasePixelsCommand {
    public RoomPixelsCommand() {
        super("cmd_roompixels", Emulator.getTexts().getValue("commands.keys.cmd_roompixels").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            int amount;

            try {
                amount = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_massduckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (amount != 0) {
                final String message = replaceAmount(getTextsValue("commands.generic.cmd_duckets.received"), amount + "");
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
                    habbo.givePixels(amount);
                    habbo.whisper(message, RoomChatMessageBubbles.ALERT);
                });
            }
            return true;
        }
        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_massduckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}