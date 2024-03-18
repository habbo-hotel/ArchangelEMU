package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class UserSongDisksInventoryMessageComposer extends MessageComposer {
    private final List<InteractionMusicDisc> items;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userSongDisksInventoryMessageComposer);

        this.response.appendInt(this.items.size());

        for (InteractionMusicDisc item : this.items) {
            this.response.appendInt(item.getId());
            this.response.appendInt(item.getSongId());
        }

        return this.response;
    }
}
