package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.rooms.RoomTraxManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeboxSongDisksMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.UserSongDisksInventoryMessageComposer;

public abstract class JukeboxEvent  extends MessageHandler {
    protected void updateHabboWithCurrentPlaying() {
        RoomTraxManager roomTraxManager = this.client.getHabbo().getRoomUnit().getRoom().getRoomTraxManager();
        this.client.sendResponse(new JukeboxSongDisksMessageComposer(roomTraxManager.getSongs(), roomTraxManager.totalLength()));
        this.client.sendResponse(new UserSongDisksInventoryMessageComposer(roomTraxManager.myList(this.client.getHabbo())));
        this.client.getHabbo().getRoomUnit().getRoom().getRoomTraxManager().updateCurrentPlayingSong(this.client.getHabbo());
    }
}
