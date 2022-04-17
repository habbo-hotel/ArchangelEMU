package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PostThreadMessageComposer extends MessageComposer {
    public final ForumThread thread;

    public PostThreadMessageComposer(ForumThread thread) {
        this.thread = thread;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.PostThreadMessageComposer);
        this.response.appendInt(this.thread.getGuildId());
        this.thread.serialize(this.response);
        return this.response;
    }
}