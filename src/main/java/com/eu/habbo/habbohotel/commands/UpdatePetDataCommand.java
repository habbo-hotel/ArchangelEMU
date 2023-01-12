package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdatePetDataCommand extends Command {
    public UpdatePetDataCommand() {
        super("cmd_update_pet_data", Emulator.getTexts().getValue("commands.keys.cmd_update_pet_data").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getPetManager().reloadPetData();

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_pet_data"), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
