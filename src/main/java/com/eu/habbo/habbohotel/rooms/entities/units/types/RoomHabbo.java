package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.SleepMessageComposer;
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

    public void idle() {
        this.setIdle();

        if (this.getDanceType() != DanceType.NONE) {
            this.setDance(DanceType.NONE);
        }

        this.getRoom().sendComposer(new SleepMessageComposer(this).compose());

        WiredHandler.handle(WiredTriggerType.IDLES, this, this.getRoom(), new Object[]{this});
    }

    public void unIdle() {
        this.resetIdleTicks();

        this.getRoom().sendComposer(new SleepMessageComposer(this).compose());

        WiredHandler.handle(WiredTriggerType.UNIDLES, this, this.getRoom(), new Object[]{this});
    }

    public boolean isIdle() {
        return this.idleTicks > Room.IDLE_CYCLES;
    }

    public void setIdle() {
        this.idleTicks = Room.IDLE_CYCLES + 1;
    }

    public void incrementIdleTicks() {
        this.idleTicks++;
    }

    public void resetIdleTicks() {
        this.idleTicks = 0;
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.HABBO;
    }

    @Override
    public void clear() {
        super.clear();
    }
}
