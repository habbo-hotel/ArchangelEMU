package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.messages.outgoing.rooms.users.AvatarEffectMessageComposer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendRoomUnitEffectComposer implements Runnable {
    private final Room room;
    private final RoomAvatar roomAvatar;


    @Override
    public void run() {
        if (this.room != null && this.roomAvatar != null) {
            this.room.sendComposer(new AvatarEffectMessageComposer(roomAvatar).compose());
        }
    }
}