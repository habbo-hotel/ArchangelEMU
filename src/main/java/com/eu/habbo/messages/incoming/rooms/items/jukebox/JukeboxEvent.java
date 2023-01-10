package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.rooms.TraxManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeboxSongDisksMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.UserSongDisksInventoryMessageComposer;

public abstract class JukeboxEvent  extends MessageHandler {
    protected void updateHabboWithCurrentPlaying() {
        TraxManager traxManager = this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager();
        this.client.sendResponse(new JukeboxSongDisksMessageComposer(traxManager.getSongs(), traxManager.totalLength()));
        this.client.sendResponse(new UserSongDisksInventoryMessageComposer(traxManager.myList(this.client.getHabbo())));
        this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager().updateCurrentPlayingSong(this.client.getHabbo());
    }
}
