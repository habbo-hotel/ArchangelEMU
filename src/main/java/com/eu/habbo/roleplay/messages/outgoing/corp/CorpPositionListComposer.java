package com.eu.habbo.roleplay.messages.outgoing.corp;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpPosition;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class CorpPositionListComposer extends MessageComposer {
    private final Corp corp;
    @Override
    protected ServerMessage composeInternal() {
        TIntObjectHashMap<CorpPosition> corpPositions = this.corp.getPositions();

        this.response.init(Outgoing.corpPositionListComposer);
        this.response.appendInt(corp.getGuild().getId());
        this.response.appendInt(corpPositions.size());

        List<CorpPosition> positionsList = new ArrayList<>();

        // Collect all positions into a list
        for (TIntObjectIterator<CorpPosition> it = corpPositions.iterator(); it.hasNext();) {
            it.advance();
            positionsList.add(it.value());
        }

        // Sort the list by order ID
        Collections.sort(positionsList, Comparator.comparingInt(CorpPosition::getOrderID));

        // Append the sorted positions to the response
        for (CorpPosition corpPosition : positionsList) {
            this.response.appendString(
                    corpPosition.getId()
                            + ";" + corpPosition.getName()
                            + ";" + corpPosition.getSalary()
                            + ";" + corpPosition.getMaleFigure()
                            + ";" + corpPosition.getFemaleFigure()
            );
        }

        return this.response;
    }
}
