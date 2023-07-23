package com.eu.habbo.habbohotel.rooms.entities.units.types;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.interfaces.ConditionalGate;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.entities.RoomRotation;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.UserUpdateComposer;
import com.eu.habbo.util.pathfinding.Rotation;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
public class RoomPet extends RoomUnit {
    public RoomPet() {
        super();
    }

    @Override
    public boolean cycle(Room room) {
        try {
            Pet pet = this.getRoom().getRoomUnitManager().getPetByRoomUnit(this);

            if (this.handleRider(pet, room)) {
                return this.isStatusUpdateNeeded();
            }

            if (this.removeStatus(RoomUnitStatus.SIT) != null || this.removeStatus(RoomUnitStatus.MOVE) != null || this.removeStatus(RoomUnitStatus.LAY) != null) {
                this.setStatusUpdateNeeded(true);
            }

            for (Map.Entry<RoomUnitStatus, String> set : this.getStatuses().entrySet()) {
                if (set.getKey().isRemoveWhenWalking()) {
                    this.removeStatus(set.getKey());
                }
            }

            if (this.getPath() == null || this.getPath().isEmpty()) {
                this.setStatusUpdateNeeded(true);
                return true;
            }

            RoomTile next = this.getPath().poll();
            boolean overrideChecks = next != null && this.canOverrideTile(next);

            if (this.getPath().isEmpty()) {
                this.setSitUpdate(true);

                if (next != null && next.hasUnits() && !overrideChecks) {
                    this.setStatusUpdateNeeded(false);
                    return false;
                }
            }

            Deque<RoomTile> peekPath = room.getLayout().findPath(this.getCurrentPosition(), this.getPath().peek(), this.getGoalLocation(), this);

            if (peekPath == null) peekPath = new LinkedList<>();

            if (peekPath.size() >= 3) {
                if (this.getPath().isEmpty()) {
                    this.setStatusUpdateNeeded(true);
                    return true;
                }

                this.getPath().pop();
                //peekPath.pop(); //Start
                peekPath.removeLast(); //End

                if (peekPath.peek() != next) {
                    next = peekPath.poll();
                    for (int i = 0; i < peekPath.size(); i++) {
                        this.getPath().addFirst(peekPath.removeLast());
                    }
                }
            }

            if (next == null) {
                this.setStatusUpdateNeeded(true);
                return true;
            }

            this.removeStatus(RoomUnitStatus.DEAD);

            RoomItem item = room.getRoomItemManager().getTopItemAt(next.getX(), next.getY());

            double height = next.getStackHeight() - this.getCurrentPosition().getStackHeight();
            if (!room.tileWalkable(next) || (!RoomLayout.ALLOW_FALLING && height < -RoomLayout.MAXIMUM_STEP_HEIGHT) || (next.getState() == RoomTileState.OPEN && height > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.setRoom(room);
                this.getPath().clear();
                this.findPath();

                if (this.getPath().isEmpty()) {
                    this.removeStatus(RoomUnitStatus.MOVE);
                    this.setStatusUpdateNeeded(false);
                    return false;
                }
                next = this.getPath().pop();

            }

            boolean canSitNextTile = room.canSitAt(next.getX(), next.getY());

            if (canSitNextTile) {
                RoomItem tallestChair = room.getRoomItemManager().getTallestChair(next);

                if (tallestChair != null)
                    item = tallestChair;
            }

            if (next.equals(this.getGoalLocation()) && next.getState() == RoomTileState.SIT && !overrideChecks && (item == null || item.getZ() - this.getCurrentZ() > RoomLayout.MAXIMUM_STEP_HEIGHT)) {
                this.removeStatus(RoomUnitStatus.MOVE);
                this.setStatusUpdateNeeded(false);
                return false;
            }

            double zHeight = 0.0D;

            RoomItem roomItem = room.getRoomItemManager().getTopItemAt(this.getCurrentPosition().getX(), this.getCurrentPosition().getY());
            if (roomItem != null && (roomItem != item || !RoomLayout.pointInSquare(roomItem.getX(), roomItem.getY(), roomItem.getX() + roomItem.getBaseItem().getWidth() - 1, roomItem.getY() + roomItem.getBaseItem().getLength() - 1, next.getX(), next.getY())))
                roomItem.onWalkOff(this, room, new Object[]{this.getCurrentPosition(), next});


            this.incrementTilesMoved();

            RoomRotation oldRotation = this.getBodyRotation();
            this.setRotation(RoomRotation.values()[Rotation.Calculate(this.getCurrentPosition().getX(), this.getCurrentPosition().getY(), next.getX(), next.getY())]);
            if (item != null) {
                if (item != roomItem || !RoomLayout.pointInSquare(item.getX(), item.getY(), item.getX() + item.getBaseItem().getWidth() - 1, item.getY() + item.getBaseItem().getLength() - 1, this.getCurrentPosition().getX(), this.getCurrentPosition().getY())) {
                    if (item.canWalkOn(this, room, null)) {
                        item.onWalkOn(this, room, new Object[]{this.getCurrentPosition(), next});
                    } else if (item instanceof ConditionalGate conditionalGate) {
                        this.setRotation(oldRotation);
                        this.decrementTilesMoved();
                        this.setGoalLocation(this.getCurrentPosition());
                        this.removeStatus(RoomUnitStatus.MOVE);
                        room.sendComposer(new UserUpdateComposer(this).compose());

                        this.setStatusUpdateNeeded(false);
                        return false;
                    }
                } else {
                    item.onWalk(this, room, new Object[]{this.getCurrentPosition(), next});
                }

                zHeight += item.getZ();

                if (!item.getBaseItem().allowSit() && !item.getBaseItem().allowLay()) {
                    zHeight += Item.getCurrentHeight(item);
                }
            } else {
                zHeight += room.getLayout().getHeightAtSquare(next.getX(), next.getY());
            }


            this.setPreviousLocation(this.getCurrentPosition());

            this.setStatus(RoomUnitStatus.MOVE, next.getX() + "," + next.getY() + "," + zHeight);

            this.setCurrentZ(zHeight);
            this.setCurrentPosition(room.getLayout().getTile(next.getX(), next.getY()));

            this.setStatusUpdateNeeded(false);
            return false;
        } catch (Exception e) {
            log.error("Caught exception", e);
            return false;
        }
    }

    public boolean handleRider(Pet pet, Room room) {
        Habbo rider = null;

        if (pet instanceof RideablePet rideablePet) {
            rider = rideablePet.getRider();
        }

        if(rider == null) {
            return false;
        }

        // copy things from rider
        if (this.hasStatus(RoomUnitStatus.MOVE) && !rider.getRoomUnit().hasStatus(RoomUnitStatus.MOVE)) {
            this.removeStatus(RoomUnitStatus.MOVE);
        }

        if (!this.getCurrentPosition().equals(rider.getRoomUnit().getCurrentPosition())) {
            this.setStatus(RoomUnitStatus.MOVE, rider.getRoomUnit().getCurrentPosition().getX() + "," + rider.getRoomUnit().getCurrentPosition().getY() + "," + (rider.getRoomUnit().getCurrentPosition().getStackHeight()));
            this.setPreviousLocation(rider.getRoomUnit().getPreviousLocation());
            this.setPreviousLocationZ(rider.getRoomUnit().getPreviousLocation().getStackHeight());
            this.setCurrentPosition(rider.getRoomUnit().getCurrentPosition());
            this.setCurrentZ(rider.getRoomUnit().getCurrentPosition().getStackHeight());
        }

        return true;
    }

    public RoomUnitType getRoomUnitType() {
        return RoomUnitType.PET;
    }
}
