package com.eu.habbo.roleplay.messages.outgoing.guild.forums;

import com.eu.habbo.roleplay.guilds.Guild;
import com.eu.habbo.roleplay.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateThreadMessageComposer extends MessageComposer {

    private final Guild guild;

    private final ForumThread thread;

    private final Habbo habbo;

    private final boolean isPinned;

    private final boolean isLocked;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.updateThreadMessageComposer);
        this.response.appendInt(this.thread.getGuildId());
        this.thread.serialize(this.response);

        return this.response;
    }
}