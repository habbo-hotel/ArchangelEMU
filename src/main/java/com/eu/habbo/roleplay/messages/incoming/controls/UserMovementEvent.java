package com.eu.habbo.roleplay.messages.incoming.controls;

import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class UserMovementEvent extends MessageHandler {

    @Override
    public void handle() {
        String movementDirectionKey = this.packet.readString();

        if (movementDirectionKey == null) {
            return;
        }

        MovementDirection direction = MovementDirection.fromKey(movementDirectionKey);

        if (direction == null) {
            return;
        }

        Habbo habbo = this.client.getHabbo();
        RoomUnit roomUnit = habbo.getRoomUnit();

        if (direction == MovementDirection.STOP) {
            roomUnit.stopMoving(); // Stop movement
        } else {
            roomUnit.startMoving(direction); // Start movement in the specified direction
        }
    }
}
