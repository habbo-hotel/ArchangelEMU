package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class FurnitureAliasesMessageComposer extends MessageComposer {
    private final Map<String, String> unknownMap;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.furnitureAliasesMessageComposer);
        this.response.appendInt(this.unknownMap.size());
        for (Map.Entry<String, String> entry : this.unknownMap.entrySet()) {
            this.response.appendString(entry.getKey());
            this.response.appendString(entry.getValue());
        }
        return this.response;
    }
}