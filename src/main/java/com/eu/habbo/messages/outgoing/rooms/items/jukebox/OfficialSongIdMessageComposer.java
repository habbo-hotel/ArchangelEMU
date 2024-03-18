package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OfficialSongIdMessageComposer extends MessageComposer {
    private final SoundTrack track;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.officialSongIdMessageComposer);
        this.response.appendString(this.track.getCode());
        this.response.appendInt(this.track.getId());
        return this.response;
    }
}
