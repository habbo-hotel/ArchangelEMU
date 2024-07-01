package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpPosition;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorpPositionListComposer extends MessageComposer {
    private final Corp corp;
    @Override
    protected ServerMessage composeInternal() {
        TIntObjectHashMap<CorpPosition> corpPositions = this.corp.getPositions();

        this.response.init(Outgoing.corpPositionListComposer);
        this.response.appendInt(corp.getGuild().getId());
        this.response.appendInt(corpPositions.size());

        for (TIntObjectIterator<CorpPosition> it = corpPositions.iterator(); it.hasNext(); ) {
            it.advance();
            CorpPosition corpPosition = it.value();
            this.response.appendInt(corpPosition.getId());
        }

        return this.response;
    }
}
