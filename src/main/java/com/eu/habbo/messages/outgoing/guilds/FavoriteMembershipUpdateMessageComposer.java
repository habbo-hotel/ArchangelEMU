package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FavoriteMembershipUpdateMessageComposer extends MessageComposer {
    private final RoomUnit roomUnit;
    private final Guild guild;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.favoriteMembershipUpdateMessageComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.guild != null ? this.guild.getId() : 0);
        this.response.appendInt(this.guild != null ? this.guild.getState().getState() : 3);
        this.response.appendString(this.guild != null ? this.guild.getName() : "");
        return this.response;
    }
}
