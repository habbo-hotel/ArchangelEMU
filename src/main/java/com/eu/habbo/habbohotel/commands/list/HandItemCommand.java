package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.rooms.users.CarryObjectMessageComposer;

public class HandItemCommand extends Command {
    public HandItemCommand() {
        super("cmd_hand_item");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params.length == 2) {
            try {
                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                    int effectId = Integer.parseInt(params[1]);
                    gameClient.getHabbo().getRoomUnit().setHandItem(effectId);
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new CarryObjectMessageComposer(gameClient.getHabbo().getRoomUnit()).compose());
                }
            } catch (Exception e) {
                //Don't handle incorrect parse exceptions :P
            }
        }
        return true;
    }
}
