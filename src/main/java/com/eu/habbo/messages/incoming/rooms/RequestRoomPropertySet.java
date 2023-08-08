package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.FurniListRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomPropertyMessageComposer;

public class RequestRoomPropertySet extends MessageHandler {
    @Override
    public void handle() {
        if (this.client.getHabbo().getRoomUnit().getRoom() == null)
            return;

        Room room = this.client.getHabbo().getRoomUnit().getRoom();

        if (room.getRoomInfo().getOwnerInfo().getId() == this.client.getHabbo().getHabboInfo().getId() || room.getRoomRightsManager().hasRights(this.client.getHabbo()) || this.client.getHabbo().hasPermissionRight(Permission.ACC_PLACEFURNI)) {
            int itemId = this.packet.readInt();
            RoomItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemId);

            if (item == null) {
                this.client.sendResponse(new FurniListRemoveComposer(itemId));
                return;
            }

            switch (item.getBaseItem().getName()) {
                case "floor":
                    room.getRoomInfo().setFloorPaint(item.getExtraData());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFloor"));
                    break;
                case "wallpaper":
                    room.getRoomInfo().setWallPaint(item.getExtraData());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoWallpaper"));
                    break;
                case "landscape":
                    room.getRoomInfo().setLandscapePaint(item.getExtraData());

                    AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoLandscape"));
                    break;
                default:
                    return;
            }

            this.client.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
            room.setNeedsUpdate(true);
            room.sendComposer(new RoomPropertyMessageComposer(item.getBaseItem().getName(), item.getExtraData()).compose());
            item.setSqlDeleteNeeded(true);
            Emulator.getThreading().run(item);
            this.client.sendResponse(new FurniListRemoveComposer(itemId));
        }
    }
}
