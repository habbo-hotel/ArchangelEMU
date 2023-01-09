package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class JukeboxSongDisksMessageComposer extends MessageComposer {
    private final List<InteractionMusicDisc> songs;
    private final int totalLength;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.jukeboxSongDisksMessageComposer);
        this.response.appendInt(this.totalLength); //Dunno //TODO Total play length?
        this.response.appendInt(this.songs.size());
        for (InteractionMusicDisc soundTrack : this.songs) {
            this.response.appendInt(soundTrack.getId());
            this.response.appendInt(soundTrack.getSongId());
        }
        return this.response;
    }
}
