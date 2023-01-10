package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.rooms.TraxManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeboxSongDisksMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.UserSongDisksInventoryMessageComposer;

public class GetJukeboxPlayListEvent extends JukeboxEvent {
    @Override
    public void handle() {
      updateHabboWithCurrentPlaying();
    }
}
