package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class RoomUserQuestionAnsweredComposer extends MessageComposer {
    private final int userId;
    private final String value;
    private final Map<String, Integer> unknownMap;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.roomUserQuestionAnsweredComposer);
        this.response.appendInt(this.userId);
        this.response.appendString(this.value);
        this.response.appendInt(this.unknownMap.size());
        for (Map.Entry<String, Integer> entry : this.unknownMap.entrySet()) {
            this.response.appendString(entry.getKey());
            this.response.appendInt(entry.getValue());
        }
        return this.response;
    }
}