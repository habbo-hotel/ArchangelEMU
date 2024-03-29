package com.eu.habbo.roleplay.messages.outgoing.gang;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.gangs.Gang;
import com.eu.habbo.roleplay.gangs.GangManager;
import com.eu.habbo.roleplay.gangs.GangPosition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GangPositionInfoComposer extends MessageComposer {
    private  final int gangID;
    private final int gangPositionID;

    @Override
    protected ServerMessage composeInternal() {
        Gang matchingGang = GangManager.getInstance().getGangById(this.gangID);
        GangPosition matchingPosition = matchingGang.getPositionByID(this.gangPositionID);
        this.response.init(Outgoing.gangPositionInfoComposer);
        this.response.appendInt(matchingPosition.getId());;
        this.response.appendInt(matchingPosition.getGangID());;
        this.response.appendString(matchingPosition.getName());
        this.response.appendString(matchingPosition.getDescription());
        this.response.appendBoolean(matchingPosition.isCanKick());
        this.response.appendBoolean(matchingPosition.isCanInvite());
        return this.response;
    }
}
