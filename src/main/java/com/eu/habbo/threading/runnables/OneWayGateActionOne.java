package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OneWayGateActionOne implements Runnable {
    private final GameClient client;
    private final Room room;
    private final RoomItem oneWayGate;


    @Override
    public void run() {
        this.room.sendComposer(new UserUpdateComposer(this.client.getHabbo().getRoomUnit()).compose());

        RoomTile t = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(this.oneWayGate.getCurrentPosition().getX(), this.oneWayGate.getCurrentPosition().getY()), (this.oneWayGate.getRotation() + 4) % 8);

        if (t.isWalkable()) {
            if (this.room.getLayout().tileWalkable(t) && this.client.getHabbo().getRoomUnit().getCurrentPosition().getX() == this.oneWayGate.getCurrentPosition().getX() && this.client.getHabbo().getRoomUnit().getCurrentPosition().getY() == this.oneWayGate.getCurrentPosition().getY()) {
                this.client.getHabbo().getRoomUnit().walkTo(t);

                if (!this.oneWayGate.getExtraData().equals("0")) {
                    Emulator.getThreading().run(new HabboItemNewState(this.oneWayGate, this.room, "0"), 1000);
                }
            }
            //else if (this.client.getHabbo().getRoomUnit().getX() == this.oneWayGate.getX() && this.client.getHabbo().getRoomUnit().getY() == this.oneWayGate.getY())
            //{

            //}
            else {
                if (!this.oneWayGate.getExtraData().equals("0")) {
                    Emulator.getThreading().run(new HabboItemNewState(this.oneWayGate, this.room, "0"), 1000);
                }
            }
        }
    }
}
