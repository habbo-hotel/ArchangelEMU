package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomFilterSettingsMessageComposer;

public class GetCustomRoomFilterEvent extends MessageHandler {
    @Override
    public void handle() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(this.packet.readInt());

        if (room != null && room.getRoomRightsManager().hasRights(this.client.getHabbo())) {
            this.client.sendResponse(new RoomFilterSettingsMessageComposer(room));

            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("SelfModRoomFilterSeen"));
        }
    }
}
