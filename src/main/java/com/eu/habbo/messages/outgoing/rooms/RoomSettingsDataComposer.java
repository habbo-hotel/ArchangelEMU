package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RoomSettingsDataComposer extends MessageComposer {
    private final Room room;
    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomSettingsDataComposer);
        this.response.appendInt(this.room.getRoomInfo().getId());
        this.response.appendString(this.room.getRoomInfo().getName());
        this.response.appendString(this.room.getRoomInfo().getDescription());
        this.response.appendInt(this.room.getRoomInfo().getState().ordinal());
        this.response.appendInt(this.room.getRoomInfo().getCategory().getId());
        this.response.appendInt(this.room.getRoomInfo().getMaxUsers());
        this.response.appendInt(this.room.getRoomInfo().getMaxUsers());

        if (!this.room.getRoomInfo().getTags().isEmpty()) {
            this.response.appendInt(this.room.getRoomInfo().getTags().split(";").length);
            for (String tag : this.room.getRoomInfo().getTags().split(";")) {
                this.response.appendString(tag);
            }
        } else {
            this.response.appendInt(0);
        }

        //this.response.appendInt(this.room.getRights().size());
        this.response.appendInt(this.room.getRoomInfo().getTradeMode()); //Trade Mode
        this.response.appendInt(this.room.getRoomInfo().isAllowPets() ? 1 : 0);
        this.response.appendInt(this.room.getRoomInfo().isAllowPetsEat() ? 1 : 0);
        this.response.appendInt(this.room.getRoomInfo().isAllowWalkthrough() ? 1 : 0);
        this.response.appendInt(this.room.getRoomInfo().isHideWalls() ? 1 : 0);
        this.response.appendInt(this.room.getRoomInfo().getWallThickness());
        this.response.appendInt(this.room.getRoomInfo().getFloorThickness());

        this.response.appendInt(this.room.getRoomInfo().getChatMode());
        this.response.appendInt(this.room.getRoomInfo().getChatWeight());
        this.response.appendInt(this.room.getRoomInfo().getChatSpeed());
        this.response.appendInt(this.room.getRoomInfo().getChatDistance());
        this.response.appendInt(this.room.getRoomInfo().getChatProtection());

        this.response.appendBoolean(false); //IDK?

        this.response.appendInt(this.room.getRoomInfo().getWhoCanMuteOption());
        this.response.appendInt(this.room.getRoomInfo().getWhoCanKickOption());
        this.response.appendInt(this.room.getRoomInfo().getWhoCanBanOption());
        return this.response;
    }
}
