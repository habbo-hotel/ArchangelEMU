package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CfhSanctionMessageComposer extends MessageComposer {
    private final int unknownInt1;
    private final int accountId;
    private final CFHSanction sanction;



    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.cfhSanctionMessageComposer);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.accountId);
        this.sanction.serialize(this.response);
        return this.response;
    }

    @AllArgsConstructor
    public static class CFHSanction implements ISerialize {
        private final String name;
        private final int length;
        private final int unknownInt1;
        private final boolean avatarOnly;
        private final String tradelockInfo;
        private final String machineBanInfo;

        @Override
        public void serialize(ServerMessage message) {
            message.appendString(this.name);
            message.appendInt(this.length);
            message.appendInt(this.unknownInt1);
            message.appendBoolean(this.avatarOnly);
            message.appendString(this.tradelockInfo);
            message.appendString(this.machineBanInfo);
        }
    }
}