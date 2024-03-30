package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GangInfoComposer extends MessageComposer {
    private final int gangID;

    @Override
    protected ServerMessage composeInternal() {
        Gang matchingGang = GangManager.getInstance().getGangById(this.gangID);
        this.response.init(Outgoing.gangInfoComposer);
        this.response.appendInt(matchingGang.getId());;
        this.response.appendInt(matchingGang.getUserID());;
        this.response.appendInt(matchingGang.getRoomID());
        this.response.appendString(matchingGang.getName());
        this.response.appendString(matchingGang.getDescription());
        this.response.appendString(matchingGang.getBadgeCode());
        return this.response;
    }
}
