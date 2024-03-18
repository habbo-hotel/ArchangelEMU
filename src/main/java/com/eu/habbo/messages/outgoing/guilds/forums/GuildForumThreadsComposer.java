package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.handshake.ErrorReportComposer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

@Getter
@AllArgsConstructor
public class GuildForumThreadsComposer extends MessageComposer {
    private final Guild guild;
    private final int index;


    @Override
    protected ServerMessage composeInternal() {
        ArrayList<ForumThread> threads;

        try {
            threads = new ArrayList<>(ForumThread.getByGuildId(guild.getId()));
        } catch (Exception e) {
            return new ErrorReportComposer(500).compose();
        }

        threads.sort(Comparator.comparingInt(o -> o.isPinned() ? Integer.MAX_VALUE : o.getUpdatedAt()));
        Collections.reverse(threads);

        Iterator<ForumThread> it = threads.iterator();
        int count = Math.min(threads.size(), 20);

        this.response.init(Outgoing.guildForumThreadsComposer);
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.index);
        this.response.appendInt(count);

        for (int i = 0; i < index; i++) {
            if (!it.hasNext())
                break;

            it.next();
        }

        for (int i = 0; i < count; i++) {
            if (!it.hasNext())
                break;

            it.next().serialize(this.response);
        }

        return this.response;
    }
}