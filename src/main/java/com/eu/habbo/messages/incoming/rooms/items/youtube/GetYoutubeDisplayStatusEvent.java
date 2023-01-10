package com.eu.habbo.messages.incoming.rooms.items.youtube;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;
import com.eu.habbo.messages.outgoing.rooms.items.youtube.YoutubeDisplayPlaylistsComposer;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class GetYoutubeDisplayStatusEvent extends MessageHandler {


    @Override
    public void handle() {
        int itemId = this.packet.readInt();

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

            if (item instanceof InteractionYoutubeTV tv) {

                ArrayList<YoutubeManager.YoutubePlaylist> playlists = Emulator.getGameEnvironment().getItemManager().getYoutubeManager().getPlaylistsForItemId(item.getBaseItem().getId());

                if (playlists == null) {
                    log.error("No YouTube playlists set for base item #" + item.getBaseItem().getId());
                    this.client.sendResponse(new ErrorReportComposer(1000));
                    return;
                }

                this.client.sendResponse(new YoutubeDisplayPlaylistsComposer(itemId, playlists, tv.currentPlaylist));
            }
        }
    }
}