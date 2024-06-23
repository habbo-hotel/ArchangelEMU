package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.constants.RoomConfiguration;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUnitOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.users.SleepMessageComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class RoomHabbo extends RoomAvatar {
    private Habbo unit;
    private Room loadingRoom;
    private Room previousRoom;

    private boolean cmdTeleportEnabled;

    private int kickCount;
    private int idleTicks;
    private boolean isSleeping;

    @Getter
    private RoomTile lastRoomTile;
    private int ticksSinceLastReplenish = 0;

    public RoomHabbo() {
        super();
        this.cmdTeleportEnabled = false;
    }

    @Override
    public void cycle() {
        super.cycle();

        if (this.getCurrentPosition() == this.lastRoomTile) {
            this.incrementIdleTicks();
        } else {
            this.resetIdleTicks();
        }

        this.lastRoomTile = this.getCurrentPosition();

        int energyReplenishIdleTime = Emulator.getConfig().getInt("roleplay.energy.replenish_idle_time", 4);

        if (this.idleTicks >= energyReplenishIdleTime) {
            this.ticksSinceLastReplenish += 1;

            if (this.ticksSinceLastReplenish >= energyReplenishIdleTime) {
                this.ticksSinceLastReplenish = 0;

                if (this.unit.getHabboRoleplayStats().getEnergyNow() < this.unit.getHabboRoleplayStats().getEnergyMax()) {
                    this.unit.getHabboRoleplayStats().addEnergy(Emulator.getConfig().getInt("roleplay.energy.replenish_amount", 5), "resting");
                }
            }
        } else {
            this.ticksSinceLastReplenish = 0;
        }
    }

    @Override
    public boolean walkTo(RoomTile goalLocation) {
        if(this.rideLocked || this.isTeleporting() || this.isKicked()) {
            return false;
        }

        if(this.cmdTeleportEnabled) {
            if (this.isRiding()) {
                this.room.sendComposer(new RoomUnitOnRollerComposer(this, null, this.currentPosition, this.currentZ, goalLocation, goalLocation.getStackHeight() + 1.0D, this.room).compose());
                this.room.sendComposer(new RoomUnitOnRollerComposer(this.ridingPet.getRoomUnit(), goalLocation, this.room).compose());
            } else {
                this.room.sendComposer(new RoomUnitOnRollerComposer(this, goalLocation, this.room).compose());
            }

            return false;
        }

        if (this.isRiding() && this.ridingPet.getTask() != null && this.ridingPet.getTask().equals(PetTasks.JUMP)) {
            return false;
        }

        // Reset idle status
        if (this.isIdle()) {
            UserIdleEvent event = new UserIdleEvent(this.unit, UserIdleEvent.IdleReason.WALKED, false);
            Emulator.getPluginManager().fireEvent(event);

            if (!event.isCancelled()) {
                if (!event.isIdle()) {
                    this.unIdle();
                }
            }
        }

        return super.walkTo(goalLocation);
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

        this.room.sendComposer(new SleepMessageComposer(this).compose());

        WiredHandler.handle(WiredTriggerType.IDLES, this, this.room, new Object[]{this});
    }

    public void unIdle() {
        this.resetIdleTicks();

        this.room.sendComposer(new SleepMessageComposer(this).compose());

        WiredHandler.handle(WiredTriggerType.UNIDLES, this, this.room, new Object[]{this});
    }

    public boolean isIdle() {
        return this.idleTicks > RoomConfiguration.IDLE_CYCLES;
    }

    public void setIdle() {
        this.idleTicks = RoomConfiguration.IDLE_CYCLES + 1;
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
        this.cmdTeleportEnabled = false;
    }
}
