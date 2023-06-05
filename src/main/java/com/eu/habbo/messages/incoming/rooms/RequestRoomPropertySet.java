package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomPropertyMessageComposer;

public class RequestRoomPropertySet extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || room.hasRights(this.client.getHabbo()) || this.client.getHabbo().hasRight(Permission.ACC_PLACEFURNI)) {
            int itemId = this.packet.readInt();
            HabboItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemId);

            if (item == null) {
                this.client.sendResponse(new FurniListRemoveComposer(itemId));
                return;
            }

            switch (item.getBaseItem().getName()) {
                case "floor":
                    room.setFloorPaint(item.getExtradata());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFloor"));
                    break;
                case "wallpaper":
                    room.setWallPaint(item.getExtradata());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoWallpaper"));
                    break;
                case "landscape":
                    room.setBackgroundPaint(item.getExtradata());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoLandscape"));
                    break;
                default:
                    return;
            }

            this.client.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
            room.setNeedsUpdate(true);
            room.sendComposer(new RoomPropertyMessageComposer(item.getBaseItem().getName(), item.getExtradata()).compose());
            item.needsDelete(true);
            Emulator.getThreading().run(item);
            this.client.sendResponse(new FurniListRemoveComposer(itemId));
        }
    }
}
