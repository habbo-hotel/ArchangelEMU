package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.RemainingMutePeriodComposer;

public class RoomUserMuteEvent extends MessageHandler {
    @Override
    public void handle() {
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        int minutes = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getActiveRoomById(roomId);

        if (room != null) {
            if (room.getRoomRightsManager().hasRights(this.client.getHabbo()) || this.client.getHabbo().canExecuteCommand("cmd_mute") || this.client.getHabbo().hasPermissionRight(Permission.ACC_AMBASSADOR)) {
                Habbo habbo = room.getRoomUnitManager().getRoomHabboById(userId);

                if (habbo != null) {
                    room.getRoomInfractionManager().muteHabbo(habbo, minutes);
                    habbo.getClient().sendResponse(new RemainingMutePeriodComposer(minutes * 60));
                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("SelfModMuteSeen"));
                }
            }
        }
    }
}
