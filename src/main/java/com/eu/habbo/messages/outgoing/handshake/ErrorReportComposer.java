package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorReportComposer extends MessageComposer {
    private final int messageId;
    private final int errorCode;
    private final String timestamp;

    public ErrorReportComposer(int errorCode) {
        this.messageId = 0;
        this.errorCode = errorCode;
        this.timestamp = "";
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.errorReportComposer);
        this.response.appendInt(this.messageId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.timestamp);

        return this.response;
    }
}
