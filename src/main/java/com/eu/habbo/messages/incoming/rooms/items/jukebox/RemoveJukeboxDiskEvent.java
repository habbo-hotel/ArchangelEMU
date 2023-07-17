package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RemoveJukeboxDiskEvent extends MessageHandler {
    @Override
    public void handle() {
        int index = this.packet.readInt();

        InteractionMusicDisc musicDisc = this.client.getHabbo().getRoomUnit().getRoom().getTraxManager().getSongs().get(index);

        if (musicDisc != null) {
            this.client.getHabbo().getRoomUnit().getRoom().getTraxManager().removeSong(musicDisc.getId());
        }
    }
}
