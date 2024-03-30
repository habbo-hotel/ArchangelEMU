package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corps.Corporation;
import com.eu.habbo.roleplay.corps.CorporationManager;
import com.eu.habbo.roleplay.corps.CorporationPosition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorpPositionInfoComposer extends MessageComposer {
    private final int corpID;
    private final int corpPositionID;

    @Override
    protected ServerMessage composeInternal() {
        Corporation matchingCorp = CorporationManager.getInstance().getCorporationByID(this.corpID);
        CorporationPosition matchingPosition = matchingCorp.getPositionByID(this.corpPositionID);
        this.response.init(Outgoing.corpPositionInfoComposer);
        this.response.appendInt(matchingPosition.getId());;
        this.response.appendInt(matchingPosition.getCorporationID());;
        this.response.appendString(matchingPosition.getName());
        this.response.appendString(matchingPosition.getDescription());
        this.response.appendBoolean(matchingPosition.isCanHire());
        this.response.appendBoolean(matchingPosition.isCanFire());
        this.response.appendBoolean(matchingPosition.isCanPromote());
        this.response.appendBoolean(matchingPosition.isCanDemote());
        return this.response;
    }
}
