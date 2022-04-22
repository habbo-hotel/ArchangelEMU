package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.rooms.TraxManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.UserSongDisksInventoryMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeboxSongDisksMessageComposer;

public class GetJukeboxPlayListEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        TraxManager traxManager = this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager();
        this.client.sendResponse(new JukeboxSongDisksMessageComposer(traxManager.getSongs(), traxManager.totalLength()));
        this.client.sendResponse(new UserSongDisksInventoryMessageComposer(traxManager.myList(this.client.getHabbo())));
        this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager().updateCurrentPlayingSong(this.client.getHabbo());
    }
}
