package com.eu.habbo.habbohotel.commands.list.credits;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class RoomCreditsCommand extends BaseCreditsCommand {
    public RoomCreditsCommand() {
        super("cmd_room_credits");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            int amount;

            try {
                amount = Integer.parseInt(params[1]);
            } catch (Exception e) {
                gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masscredits.invalid_amount"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (amount != 0) {
                final String message = replaceAmount(getTextsValue("commands.generic.cmd_credits.received"), amount + "");
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
                    habbo.giveCredits(amount);
                    habbo.whisper(message, RoomChatMessageBubbles.ALERT);
                });
            }
            return true;
        }
        gameClient.getHabbo().whisper(getTextsValue("commands.error.cmd_masscredits.invalid_amount"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}