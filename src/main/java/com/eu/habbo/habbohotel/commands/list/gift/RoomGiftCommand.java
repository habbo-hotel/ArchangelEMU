package com.eu.habbo.habbohotel.commands.list.gift;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.wired.WiredRewardResultMessageComposer;

public class RoomGiftCommand extends BaseGiftCommand {
    public RoomGiftCommand() {
        super("cmd_roomgift");
    }

    @Override
    public boolean handle(final GameClient gameClient, String[] params) {
        if (params.length >= 2) {
            if (!validateGiftCommand(gameClient, params)) {
                return true;
            }

            final String finalMessage = getFinalMessage(params);

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos().forEach(habbo -> {
                        createGift(finalMessage, habbo, params);
                        habbo.getClient().sendResponse(new WiredRewardResultMessageComposer(WiredRewardResultMessageComposer.REWARD_RECEIVED_ITEM));
                    }
            );

            return true;
        }

        return false;
    }


}
