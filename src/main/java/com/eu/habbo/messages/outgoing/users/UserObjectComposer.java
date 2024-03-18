package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserObjectComposer extends MessageComposer {
    private final Habbo habbo;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userObjectComposer);

        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        this.response.appendString(this.habbo.getHabboInfo().getGender().name().toUpperCase());
        this.response.appendString(this.habbo.getHabboInfo().getMotto());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendBoolean(false);
        this.response.appendInt(this.habbo.getHabboStats().getRespectPointsReceived());
        this.response.appendInt(this.habbo.getHabboStats().getRespectPointsToGive());
        this.response.appendInt(this.habbo.getHabboStats().getPetRespectPointsToGive());
        this.response.appendBoolean(false);
        this.response.appendString("01-01-1970 00:00:00");
        this.response.appendBoolean(this.habbo.getHabboStats().isAllowNameChange()); //can change name.
        this.response.appendBoolean(false); //safatey locked

        return this.response;
    }
}
