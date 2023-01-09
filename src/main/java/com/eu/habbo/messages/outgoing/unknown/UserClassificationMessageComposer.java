package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;

import java.util.List;

@AllArgsConstructor
public class UserClassificationMessageComposer extends MessageComposer {
    private final List<Pair<Integer, Pair<String, String>>> info;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userClassificationMessageComposer);
        this.response.appendInt(this.info.size());
        for (Pair<Integer, Pair<String, String>> set : this.info) {
            this.response.appendInt(set.getKey());
            this.response.appendString(set.getValue().getKey());
            this.response.appendString(set.getValue().getValue());
        }
        return this.response;
    }
}