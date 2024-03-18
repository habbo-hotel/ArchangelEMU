package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
public class YoutubeDisplayPlaylistsComposer extends MessageComposer {
    private final int itemId;
    private final ArrayList<YoutubeManager.YoutubePlaylist> playlists;
    private final YoutubeManager.YoutubePlaylist currentPlaylist;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.youtubeDisplayPlaylistsComposer);
        this.response.appendInt(this.itemId);
        this.response.appendInt(this.playlists.size());

        for (YoutubeManager.YoutubePlaylist item : this.playlists) {
            this.response.appendString(item.getId()); // playlist ID
            this.response.appendString(item.getName()); // playlist title
            this.response.appendString(item.getDescription()); // playlist description
        }

        this.response.appendString(this.currentPlaylist == null ? "" : this.currentPlaylist.getId()); // current playlist ID
        return this.response;
    }
}