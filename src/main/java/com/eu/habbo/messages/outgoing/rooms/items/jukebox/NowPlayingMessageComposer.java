package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NowPlayingMessageComposer extends MessageComposer {
    private final SoundTrack track;
    private final int playListId;
    private final int msPlayed;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.nowPlayingMessageComposer);

        if (this.track != null) {
            this.response.appendInt(this.track.getId());
            this.response.appendInt(this.playListId);
            this.response.appendInt(this.track.getId());
            this.response.appendInt(this.track.getLength());
            this.response.appendInt(this.msPlayed);
        } else {
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
            this.response.appendInt(-1);
        }
        return this.response;
    }
}
