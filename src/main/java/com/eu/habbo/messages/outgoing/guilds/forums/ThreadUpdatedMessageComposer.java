package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;

public class ThreadUpdatedMessageComposer extends MessageComposer {

    public final Guild guild;

    public final ForumThread thread;

    private final Habbo habbo;

    private final boolean isPinned;

    private final boolean isLocked;

    public ThreadUpdatedMessageComposer(Guild guild, ForumThread thread, Habbo habbo, boolean isPinned, boolean isLocked) {
        this.guild = guild;
        this.habbo = habbo;
        this.thread = thread;
        this.isPinned = isPinned;
        this.isLocked = isLocked;
    }

    @Override
    public ServerMessage compose() {
        if (isPinned != thread.isPinned()) {
            this.habbo.getClient().sendResponse(new BubbleAlertComposer(isPinned ? BubbleAlertKeys.FORUMS_THREAD_PINNED.key : BubbleAlertKeys.FORUMS_THREAD_UNPINNED.key).compose());
        }

        if (isLocked != thread.isLocked()) {
            this.habbo.getClient().sendResponse(new BubbleAlertComposer(isLocked ? BubbleAlertKeys.FORUMS_THREAD_LOCKED.key : BubbleAlertKeys.FORUMS_THREAD_UNLOCKED.key).compose());
        }

        if (this.habbo.getHabboInfo().getId() != guild.getOwnerId() ||
                guild.canModForum().state == 2 && (Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, habbo).getRank() == GuildRank.ADMIN
                        || Emulator.getGameEnvironment().getGuildManager().getGuildMember(guild, habbo).getRank() == GuildRank.MOD)
                || this.habbo.hasPermission("acc_modtool_ticket_q")) {
            this.thread.setPinned(isPinned);
            this.thread.setLocked(isLocked);

            Emulator.getThreading().run(this.thread);

            this.response.init(Outgoing.ThreadUpdateMessageComposer);
            this.response.appendInt(this.thread.getGuildId());
            this.thread.serialize(this.response);

            return this.response;
        }

        return null;
    }
}