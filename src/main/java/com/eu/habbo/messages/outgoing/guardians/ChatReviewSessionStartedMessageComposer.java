package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.habbohotel.guides.GuardianTicket;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.TIntIntHashMap;
import lombok.AllArgsConstructor;

import java.util.Calendar;

@AllArgsConstructor
public class ChatReviewSessionStartedMessageComposer extends MessageComposer {
    private final GuardianTicket ticket;

    @Override
    protected ServerMessage composeInternal() {
        TIntIntHashMap mappedUsers = new TIntIntHashMap();
        mappedUsers.put(this.ticket.getReported().getHabboInfo().getId(), 0);

        Calendar c = Calendar.getInstance();
        c.setTime(this.ticket.getDate());

        StringBuilder fullMessage = new StringBuilder(c.get(Calendar.YEAR) + " ");
        fullMessage.append(c.get(Calendar.MONTH)).append(" ");
        fullMessage.append(c.get(Calendar.DAY_OF_MONTH)).append(" ");
        fullMessage.append(c.get(Calendar.MINUTE)).append(" ");
        fullMessage.append(c.get(Calendar.SECOND)).append(";");

        fullMessage.append("\r");

        for (ModToolChatLog chatLog : this.ticket.getChatLogs()) {
            if (!mappedUsers.containsKey(chatLog.getHabboId())) {
                mappedUsers.put(chatLog.getHabboId(), mappedUsers.size());
            }

            fullMessage.append("unused;").append(mappedUsers.get(chatLog.getHabboId())).append(";").append(chatLog.getMessage()).append("\r");
        }

        this.response.init(Outgoing.chatReviewSessionStartedMessageComposer);
        this.response.appendInt(this.ticket.getTimeLeft());
        this.response.appendString(fullMessage.toString());

        //2015 10 17 14 24 30
        return this.response;
    }
}
