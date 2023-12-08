package com.eu.habbo.messages.incoming.rooms.items.jukebox;

public class GetNowPlayingEvent extends JukeboxEvent {
    @Override
    public void handle() {
        updateHabboWithCurrentPlaying();
    }
}
