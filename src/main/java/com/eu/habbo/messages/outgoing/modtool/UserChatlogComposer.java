package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

@AllArgsConstructor
public class UserChatlogComposer extends MessageComposer {
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private final ArrayList<ModToolRoomVisit> set;
    private final int userId;
    private final String username;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.userChatlogComposer);
        this.response.appendInt(this.userId);
        this.response.appendString(this.username);
        this.response.appendInt(this.set.size());

        for (ModToolRoomVisit visit : this.set) {
            this.response.appendByte(1);
            this.response.appendShort(2);
            this.response.appendString("roomName");
            this.response.appendByte(2);
            this.response.appendString(visit.getRoomName());
            this.response.appendString("roomId");
            this.response.appendByte(1);
            this.response.appendInt(visit.getRoomId());

            this.response.appendShort(visit.getChat().size());
            for (ModToolChatLog chatLog : visit.getChat()) {
                this.response.appendString(format.format(chatLog.getTimestamp() * 1000L));
                this.response.appendInt(chatLog.getHabboId());
                this.response.appendString(chatLog.getUsername());
                this.response.appendString(chatLog.getMessage());
                this.response.appendBoolean(false);
            }
        }
        return this.response;
    }
}
