package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.threading.runnables.HabboItemNewState;

class TeleportActionFive implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionFive(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        this.client.getHabbo().getRoomUnit().isTeleporting = false;
        this.client.getHabbo().getRoomUnit().setCanWalk(true);

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room)
            return;

        //if (!(this.currentTeleport instanceof InteractionTeleportTile))

        RoomTile currentLocation = this.room.getLayout().getTile(this.currentTeleport.getX(), this.currentTeleport.getY());
        RoomTile tile = this.room.getLayout().getTileInFront(currentLocation, this.currentTeleport.getRotation());

        if (tile != null)
        {
            this.client.getHabbo().getRoomUnit().setGoalLocation(tile);
            this.client.getHabbo().getRoomUnit().statusUpdate(true);
        }


        this.currentTeleport.setExtradata("1");
        this.room.updateItem(this.currentTeleport);

        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 1000);
    }
}
