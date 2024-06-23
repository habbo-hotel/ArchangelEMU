package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorpInfoComposer extends MessageComposer {
    private final int corpID;

    @Override
    protected ServerMessage composeInternal() {
        Corp matchingCorp = CorpManager.getInstance().getCorpByID(this.corpID);
        this.response.init(Outgoing.corpInfoComposer);
        this.response.appendInt(matchingCorp.getGuild().getId());;
        this.response.appendInt(matchingCorp.getGuild().getOwnerId());;
        this.response.appendInt(matchingCorp.getGuild().getRoomId());
        this.response.appendString(matchingCorp.getGuild().getName());
        this.response.appendString(matchingCorp.getGuild().getDescription());
        this.response.appendString(matchingCorp.getGuild().getBadge());
        this.response.appendString(matchingCorp.getTags().toString());
        return this.response;
    }
}
