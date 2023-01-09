package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class BotSkillListUpdateComposer extends MessageComposer {
    private final int unknownInt1;
    private final Map<Integer, String> unknownMap;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.botSkillListUpdateComposer);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.unknownMap.size());
        for (Map.Entry<Integer, String> entry : this.unknownMap.entrySet()) {
            this.response.appendInt(entry.getKey());
            this.response.appendString(entry.getValue());
        }
        return this.response;
    }
}