package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.ForumThreadComment;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class ThreadMessagesMessageComposer extends MessageComposer {
    private final int guildId;
    private final int threadId;
    private final int index;
    private final Collection<ForumThreadComment> guildForumCommentList;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.threadMessagesMessageComposer);

        this.response.appendInt(this.guildId); //guild_id
        this.response.appendInt(this.threadId); //thread_id
        this.response.appendInt(this.index); //start_index
        this.response.appendInt(this.guildForumCommentList.size());

        for (ForumThreadComment comment : this.guildForumCommentList) {
            comment.serialize(this.response);
        }
        return this.response;
    }
}