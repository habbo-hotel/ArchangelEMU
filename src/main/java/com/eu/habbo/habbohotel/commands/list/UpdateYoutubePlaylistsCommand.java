package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

public class UpdateYoutubePlaylistsCommand extends Command {
    public UpdateYoutubePlaylistsCommand() {
        super("cmd_update_youtube_playlists");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getGameEnvironment().getItemManager().getYoutubeManager().load();

        gameClient.getHabbo().whisper(getTextsValue("commands.succes.cmd_update_youtube_playlists"), RoomChatMessageBubbles.ALERT);

        return true;
    }
}
