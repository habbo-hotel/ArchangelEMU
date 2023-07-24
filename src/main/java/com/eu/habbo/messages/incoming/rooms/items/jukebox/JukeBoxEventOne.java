package com.eu.habbo.messages.incoming.rooms.items.jukebox;

public class JukeBoxEventOne extends JukeboxEvent {
    @Override
    public void handle() {
        updateHabboWithCurrentPlaying();
    }


}
