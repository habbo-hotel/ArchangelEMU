package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.entities.units.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;

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
                this.response.appendInt(roomUnit.getVirtualId());
                this.response.appendInt(roomUnit.getCurrentPosition().getX());
                this.response.appendInt(roomUnit.getCurrentPosition().getY());
                this.response.appendString(String.valueOf(this.overrideZ != -1 ? this.overrideZ : roomUnit.getCurrentZ()));

                this.response.appendInt(roomUnit.getHeadRotation().getValue());
                this.response.appendInt(roomUnit.getBodyRotation().getValue());

                this.response.appendString(roomUnit.getCurrentStatuses());
            }
        } else {
            synchronized (this.habbos) {
                this.response.appendInt(this.habbos.size());
                for (Habbo habbo : this.habbos) {
                    this.response.appendInt(habbo.getRoomUnit().getVirtualId());
                    this.response.appendInt(habbo.getRoomUnit().getCurrentPosition().getX());
                    this.response.appendInt(habbo.getRoomUnit().getCurrentPosition().getY());
                    this.response.appendString(String.valueOf(habbo.getRoomUnit().getCurrentZ()));


                    this.response.appendInt(habbo.getRoomUnit().getHeadRotation().getValue());
                    this.response.appendInt(habbo.getRoomUnit().getBodyRotation().getValue());

                    this.response.appendString(habbo.getRoomUnit().getCurrentStatuses());
                }
            }
        }
        return this.response;
    }
}
