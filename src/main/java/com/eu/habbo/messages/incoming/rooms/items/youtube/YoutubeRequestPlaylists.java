package com.eu.habbo.messages.incoming.rooms.items.youtube;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.youtube.YoutubeDisplayListComposer;

public class YoutubeRequestPlaylists extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int itemId = this.packet.readInt();

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

            if (item instanceof InteractionYoutubeTV) {
                InteractionYoutubeTV tv = (InteractionYoutubeTV) item;

                this.client.sendResponse(new YoutubeDisplayListComposer(itemId, Emulator.getGameEnvironment().getItemManager().getYoutubeManager().getPlaylistsForItemId(item.getBaseItem().getId()), tv.currentPlaylist));
            }
        }
    }
}