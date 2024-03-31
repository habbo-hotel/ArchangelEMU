package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corps.Corporation;
import com.eu.habbo.roleplay.corps.CorporationManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorpInfoComposer extends MessageComposer {
    private final int corpID;

    @Override
    protected ServerMessage composeInternal() {
        Corporation matchingCorp = CorporationManager.getInstance().getCorporationByID(this.corpID);
        this.response.init(Outgoing.corpInfoComposer);
        this.response.appendInt(matchingCorp.getGuild().getId());;
        this.response.appendInt(matchingCorp.getGuild().getOwnerId());;
        this.response.appendInt(matchingCorp.getGuild().getRoomId());
        this.response.appendString(matchingCorp.getGuild().getName());
        this.response.appendString(matchingCorp.getGuild().getDescription());
        this.response.appendString(matchingCorp.getGuild().getBadge());
        return this.response;
    }
}
