package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.items.entities.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class AddJukeboxDiskEvent extends MessageHandler {
    @Override
    public void handle() {
        if (!this.client.getHabbo().getRoomUnit().getRoom().getRoomRightsManager().hasRights(this.client.getHabbo())) return;

        int itemId = this.packet.readInt();
        int slotId = this.packet.readInt();

        Habbo habbo = this.client.getHabbo();

        if (habbo != null) {
            RoomItem item = habbo.getInventory().getItemsComponent().getHabboItem(itemId);

            if (item instanceof InteractionMusicDisc interactionMusicDisc && item.getRoomId() == 0) {
                this.client.getHabbo().getRoomUnit().getRoom().getRoomTraxManager().addSong(interactionMusicDisc, habbo);
            }
        }
    }
}