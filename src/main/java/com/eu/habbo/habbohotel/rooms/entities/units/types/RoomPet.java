package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoomPet extends RoomUnit {
    private Pet unit;

    public RoomPet() {
        super();
    }

    @Override
    public void cycle() {
        if (!this.handleRider()) {
            super.cycle();
        }
    }

    public boolean handleRider() {
        Habbo rider = null;

        if (this.unit instanceof RideablePet rideablePet) {
            rider = rideablePet.getRider();
        }

        if(rider == null) {
            return false;
        }

        // copy things from rider
        if (this.hasStatus(RoomUnitStatus.MOVE) && !rider.getRoomUnit().hasStatus(RoomUnitStatus.MOVE) || !rider.getRoomUnit().isWalking()) {
            this.removeStatus(RoomUnitStatus.MOVE);
        }

        if (!this.currentPosition.equals(rider.getRoomUnit().getCurrentPosition())) {
            this.addStatus(RoomUnitStatus.MOVE, rider.getRoomUnit().getCurrentPosition().getX() + "," + rider.getRoomUnit().getCurrentPosition().getY() + "," + (rider.getRoomUnit().getCurrentPosition().getStackHeight()));
            this.setCurrentPosition(rider.getRoomUnit().getCurrentPosition());
            this.setCurrentZ(rider.getRoomUnit().getCurrentPosition().getStackHeight());
        }

        return true;
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.PET;
    }
}
