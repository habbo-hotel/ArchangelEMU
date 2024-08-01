package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

@AllArgsConstructor
public class TurfCaptureTimeLeftComposer extends MessageComposer {
    private final Room room;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.turfCaptureTimeLeftComposer);

        HashMap<Integer, Integer> gangsInRoom = getGangsInRoom();

        this.response.appendInt(this.room.getRoomTurfManager().getSecondsLeft());
        this.response.appendBoolean(!this.room.getRoomTurfManager().isBlocked());
        this.response.appendInt(gangsInRoom.size());
        gangsInRoom.forEach((gangId, userCount) -> {
            this.response.appendString(gangId + ";" + userCount);
        });
        return this.response;
    }

    private HashMap<Integer, Integer> getGangsInRoom() {
        Collection<Habbo> usersInRoom = this.room.getRoomUnitManager().getCurrentHabbos().values();

        HashMap<Integer, Integer> gangsInRoom = new HashMap<>();

        for (Habbo user : usersInRoom) {
            Integer gangID = user.getHabboRoleplayStats().getGang() != null
                    ? user.getHabboRoleplayStats().getGang().getId()
                    : 0;

            if (gangsInRoom.containsKey(gangID)) {
                gangsInRoom.put(gangID, gangsInRoom.get(gangID) + 1);
                continue;
            }

            gangsInRoom.put(gangID, 1);
        }
        return gangsInRoom;
    }
}
