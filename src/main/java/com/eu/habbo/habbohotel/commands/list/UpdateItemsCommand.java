package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.HeightMapComposer;

public class UpdateItemsCommand extends Command {
    public UpdateItemsCommand() {
        super("cmd_update_items");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getItemManager().loadItems();
        Emulator.getGameEnvironment().getItemManager().loadCrackable();
        Emulator.getGameEnvironment().getItemManager().loadSoundTracks();

        synchronized (Emulator.getGameEnvironment().getRoomManager().getActiveRooms()) {
            Emulator.getGameEnvironment().getRoomManager().getActiveRooms().stream()
                    .filter(room -> room.isLoaded() && room.getUserCount() > 0 && room.getLayout() != null)
                    .forEach(room -> room.sendComposer(new HeightMapComposer(room).compose()));
        }

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_items"), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
