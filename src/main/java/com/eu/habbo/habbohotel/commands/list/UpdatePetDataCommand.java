package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdatePetDataCommand extends Command {
    public UpdatePetDataCommand() {
        super("cmd_update_pet_data");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getPetManager().reloadPetData();

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_pet_data"), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
