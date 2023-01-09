package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserChangeMessageComposer extends MessageComposer {
    private final Habbo habbo;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userChangeMessageComposer);
        this.response.appendInt(this.habbo.getRoomUnit() == null ? -1 : this.habbo.getRoomUnit().getId());
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        this.response.appendString(this.habbo.getHabboInfo().getGender().name() + "");
        this.response.appendString(this.habbo.getHabboInfo().getMotto());
        this.response.appendInt(this.habbo.getHabboStats().getAchievementScore());
        return this.response;
    }
}
