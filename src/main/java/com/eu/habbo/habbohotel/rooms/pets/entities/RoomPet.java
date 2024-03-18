package com.eu.habbo.habbohotel.rooms.pets.entities;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.constants.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.entities.units.types.RoomAvatar;
import com.eu.habbo.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(chain = true)
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

    /**
     * Handles the interaction between a RideablePet unit and its rider (Habbo) in the virtual room.
     * If the unit is a RideablePet and has a rider, this method updates the unit's position and status
     * to reflect the rider's movement and actions.
     *
     * @return {@code true} if the interaction was successfully handled, {@code false} pet walks normally.
    */
    public boolean handleRider() {
        if (!(this.unit instanceof RideablePet rideablePet)) {
            return false;
        }

        Habbo rider = rideablePet.getRider();

        if(rider == null) {
            return false;
        }

        RoomAvatar riderAvatar = rider.getRoomUnit();

        if(this.getNextPosition() != null) {
            this.setCurrentPosition(this.getNextPosition());
            this.setCurrentZ(this.getNextZ());
        }

        if(riderAvatar.isWalking()) {
            this.addStatus(RoomUnitStatus.MOVE, riderAvatar.getNextPosition().getX() + "," + riderAvatar.getNextPosition().getY() + "," + (riderAvatar.getNextZ() - 1.0D));
            this.setNextPosition(riderAvatar.getNextPosition());
            this.setNextZ(riderAvatar.getNextZ() - 1.0D);
            this.setRotation(riderAvatar.getBodyRotation());
        } else if(!riderAvatar.isWalking() && this.hasStatus(RoomUnitStatus.MOVE)) {
            this.stopWalking();
        }

        return true;
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.PET;
    }
}
