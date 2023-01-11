package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.Map;

public class UserUpdateComposer extends MessageComposer {
    private Collection<Habbo> habbos;
    private THashSet<RoomUnit> roomUnits;
    private double overrideZ = -1;

    public UserUpdateComposer(RoomUnit roomUnit) {
        this.roomUnits = new THashSet<>();
        this.roomUnits.add(roomUnit);
    }

    public UserUpdateComposer(RoomUnit roomUnit, double overrideZ) {
        this(roomUnit);
        this.overrideZ = overrideZ;
    }

    public UserUpdateComposer(THashSet<RoomUnit> roomUnits) {
        this.roomUnits = roomUnits;
    }

    public UserUpdateComposer(Collection<Habbo> habbos) {
        this.habbos = habbos;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userUpdateComposer);
        if (this.roomUnits != null) {
            this.response.appendInt(this.roomUnits.size());
            for (RoomUnit roomUnit : this.roomUnits) {
                this.response.appendInt(roomUnit.getId());
                this.response.appendInt(roomUnit.getPreviousLocation().getX());
                this.response.appendInt(roomUnit.getPreviousLocation().getY());
                this.response.appendString((this.overrideZ != -1 ? this.overrideZ : roomUnit.getPreviousLocationZ()) + "");


                this.response.appendInt(roomUnit.getHeadRotation().getValue());
                this.response.appendInt(roomUnit.getBodyRotation().getValue());

                StringBuilder status = new StringBuilder("/");
                for (Map.Entry<RoomUnitStatus, String> entry : roomUnit.getStatus().entrySet()) {
                    status.append(entry.getKey()).append(" ").append(entry.getValue()).append("/");
                }

                this.response.appendString(status.toString());
                roomUnit.setPreviousLocation(roomUnit.getCurrentLocation());
            }
        } else {
            synchronized (this.habbos) {
                this.response.appendInt(this.habbos.size());
                for (Habbo habbo : this.habbos) {
                    this.response.appendInt(habbo.getRoomUnit().getId());
                    this.response.appendInt(habbo.getRoomUnit().getPreviousLocation().getX());
                    this.response.appendInt(habbo.getRoomUnit().getPreviousLocation().getY());
                    this.response.appendString(habbo.getRoomUnit().getPreviousLocationZ() + "");


                    this.response.appendInt(habbo.getRoomUnit().getHeadRotation().getValue());
                    this.response.appendInt(habbo.getRoomUnit().getBodyRotation().getValue());

                    StringBuilder status = new StringBuilder("/");

                    for (Map.Entry<RoomUnitStatus, String> entry : habbo.getRoomUnit().getStatus().entrySet()) {
                        status.append(entry.getKey()).append(" ").append(entry.getValue()).append("/");
                    }
                    this.response.appendString(status.toString());
                    habbo.getRoomUnit().setPreviousLocation(habbo.getRoomUnit().getCurrentLocation());
                }
            }
        }
        return this.response;
    }
}
