package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendRoomUnitEffectComposer implements Runnable {
    private final Room room;
    private final RoomUnit roomUnit;


    @Override
    public void run() {
        if (this.room != null && this.roomUnit != null) {
            this.room.sendComposer(new AvatarEffectMessageComposer(roomUnit).compose());
        }
    }
}