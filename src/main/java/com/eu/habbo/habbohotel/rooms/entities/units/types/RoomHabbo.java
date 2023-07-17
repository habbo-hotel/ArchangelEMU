package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomHabbo extends RoomAvatar {
    private Room loadingRoom;
    private Room previousRoom;

//    @Setter
//    private boolean isKicked;
    private int kickCount;
    private int idleTicks;
    private boolean isSleeping;
//    private final HashSet<Integer> overridableTiles;

    public RoomHabbo() {
        super();

//        this.isKicked = false;
//        this.overridableTiles = new HashSet<>();
    }

    public boolean cycle(Room room) {
        return super.cycle(room);
    }

    public boolean isLoadingRoom() {
        return this.loadingRoom != null;
    }

    public boolean hasPreviousRoom() {
        return this.previousRoom != null;
    }

    public void incrementKickCount() {
        this.kickCount++;
    }

    public void incrementIdleTick() {
        this.idleTicks++;
    }

    public boolean isIdle() {
        return this.idleTicks > Room.IDLE_CYCLES;
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.HABBO;
    }

    @Override
    public void clear() {
        super.clear();
    }
}
