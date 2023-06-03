package com.eu.habbo.habbohotel.commands.list.pixels;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Map;

public class MassPixelsCommand extends BasePixelsCommand {
    public MassPixelsCommand() {
        super("cmd_mass_duckets");
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
                for (Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet()) {
                    Habbo habbo = set.getValue();

                    habbo.givePixels(amount);

                    if (habbo.getHabboInfo().getCurrentRoom() != null)
                        habbo.whisper(replaceAmount(getTextsValue("commands.generic.cmd_duckets.received"), amount + ""), RoomChatMessageBubbles.ALERT);
                }
            }
            return true;
        }
        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_massduckets.invalid_amount"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
