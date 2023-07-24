package com.eu.habbo.messages.incoming.rooms.items.jukebox;

public class GetJukeboxPlayListEvent extends JukeboxEvent {
    @Override
    public void handle() {
      updateHabboWithCurrentPlaying();
    }
}
