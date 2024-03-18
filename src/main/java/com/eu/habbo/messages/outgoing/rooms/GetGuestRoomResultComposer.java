package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetGuestRoomResultComposer extends MessageComposer {
    private final Room room;
    private final Habbo habbo;
    private final boolean roomForward;
    private final boolean enterRoom;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.getGuestRoomResultComposer);
        this.response.appendBoolean(this.enterRoom);

        this.response.append(this.room);

        this.response.appendBoolean(this.roomForward);
        this.response.appendBoolean(this.room.getRoomInfo().isStaffPicked()); // staffpicked

        if(this.room.getRoomInfo().hasGuild()) {
            this.response.appendBoolean(Emulator.getGameEnvironment().getGuildManager().getGuildMember(this.room.getRoomInfo().getGuild().getId(), this.habbo.getHabboInfo().getId()) != null);
        } else {
            this.response.appendBoolean(false);
        }

        this.response.appendBoolean(this.room.getRoomChatManager().isMuted()); // isroommuted

        this.response.appendInt(this.room.getRoomInfo().getWhoCanMuteOption());
        this.response.appendInt(this.room.getRoomInfo().getWhoCanKickOption());
        this.response.appendInt(this.room.getRoomInfo().getWhoCanBanOption());

        this.response.appendBoolean(this.room.getRoomRightsManager().hasRights(this.habbo)); //mute all button

        this.response.appendInt(this.room.getRoomInfo().getChatMode());
        this.response.appendInt(this.room.getRoomInfo().getChatWeight());
        this.response.appendInt(this.room.getRoomInfo().getChatSpeed());
        this.response.appendInt(this.room.getRoomInfo().getChatDistance());
        this.response.appendInt(this.room.getRoomInfo().getChatProtection());


        return this.response;
    }
}