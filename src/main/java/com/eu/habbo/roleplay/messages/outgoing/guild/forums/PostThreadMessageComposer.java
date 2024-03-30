package com.eu.habbo.roleplay.messages.outgoing.guild.forums;

import com.eu.habbo.roleplay.guilds.forums.ForumThread;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostThreadMessageComposer extends MessageComposer {
    public final ForumThread thread;


    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.postThreadMessageComposer);
        this.response.appendInt(this.thread.getGuildId());
        this.thread.serialize(this.response);
        return this.response;
    }
}