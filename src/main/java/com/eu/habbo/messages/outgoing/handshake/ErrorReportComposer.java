package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ErrorReportComposer extends MessageComposer {
    private final int messageId;
    private final int errorCode;
    private final String timestamp;

    public ErrorReportComposer(int errorCode) {
        this.messageId = 0;
        this.errorCode = errorCode;
        this.timestamp = "";
    }

    public ErrorReportComposer(int messageId, int errorCode, String timestamp) {
        this.messageId = messageId;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.ErrorReportComposer);
        this.response.appendInt(this.messageId);
        this.response.appendInt(this.errorCode);
        this.response.appendString(this.timestamp);

        return this.response;
    }
}
