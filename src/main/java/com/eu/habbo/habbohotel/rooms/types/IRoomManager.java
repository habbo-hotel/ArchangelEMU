package com.eu.habbo.habbohotel.rooms.types;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IRoomManager {
    @Getter
    protected final Room room;

    protected void sendComposer(ServerMessage message){
        room.sendComposer(message);
    }
}
