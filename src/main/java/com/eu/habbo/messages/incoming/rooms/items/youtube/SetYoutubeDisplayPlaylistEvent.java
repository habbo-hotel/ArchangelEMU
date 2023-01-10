package com.eu.habbo.messages.incoming.rooms.items.youtube;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.youtube.YoutubeDisplayVideoMessageComposer;
import com.eu.habbo.threading.runnables.YoutubeAdvanceVideo;

import java.util.Optional;

public class SetYoutubeDisplayPlaylistEvent extends YoutubeEvent {
    @Override
    public void handle() {
        int itemId = this.packet.readInt();
        String playlistId = this.packet.readString();

        Habbo habbo = this.client.getHabbo();

        if (!validate(habbo)) return;

        Room room = habbo.getHabboInfo().getCurrentRoom();


        HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

        if (!(item instanceof InteractionYoutubeTV)) return;

        Optional<YoutubeManager.YoutubePlaylist> playlist = Emulator.getGameEnvironment().getItemManager().getYoutubeManager().getPlaylistsForItemId(item.getBaseItem().getId()).stream().filter(p -> p.getId().equals(playlistId)).findAny();

        if (playlist.isPresent()) {
            YoutubeManager.YoutubeVideo video = playlist.get().getVideos().get(0);
            if (video == null) return;

            ((InteractionYoutubeTV) item).currentVideo = video;
            ((InteractionYoutubeTV) item).currentPlaylist = playlist.get();

            ((InteractionYoutubeTV) item).cancelAdvancement();

            room.updateItem(item);
            room.sendComposer(new YoutubeDisplayVideoMessageComposer(itemId, video, true, 0).compose());
            ((InteractionYoutubeTV) item).autoAdvance = Emulator.getThreading().run(new YoutubeAdvanceVideo((InteractionYoutubeTV) item), video.getDuration() * 1000L);

            item.needsUpdate(true);
        }
    }
}