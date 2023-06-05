package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.NotificationDialogMessageComposer;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CannonKickAction implements Runnable {
    private final InteractionCannon cannon;
    private final Room room;
    private final GameClient client;

    @Override
    public void run() {
        if(this.cannon == null || this.room == null || this.room.getLayout() == null) return;

        if (this.client != null) {
            this.client.getHabbo().getRoomUnit().setCanWalk(true);
        }
        THashMap<String, String> dater = new THashMap<>();
        dater.put("title", "${notification.room.kick.cannonball.title}");
        dater.put("message", "${notification.room.kick.cannonball.message}");

        int rotation = this.cannon.getRotation();
        List<RoomTile> tiles = this.room.getLayout().getTilesInFront(this.room.getLayout().getTile(this.cannon.getX(), this.cannon.getY()), rotation + 6, 3);

        ServerMessage message = new NotificationDialogMessageComposer("cannon.png", dater).compose();

        for (RoomTile t : tiles) {
            for (Habbo habbo : this.room.getHabbosAt(t.getX(), t.getY())) {
                if (!habbo.hasRight(Permission.ACC_UNKICKABLE) && !this.room.isOwner(habbo)) {
                    Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this.room);
                    habbo.getClient().sendResponse(message); //kicked composer
                }
            }
        }
    }
}
