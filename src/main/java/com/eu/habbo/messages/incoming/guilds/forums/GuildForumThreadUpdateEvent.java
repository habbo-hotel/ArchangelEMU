package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.guilds.SettingsState;
import com.eu.habbo.habbohotel.guilds.forums.ForumThread;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumThreadsComposer;
import com.eu.habbo.messages.outgoing.guilds.forums.ThreadUpdatedMessageComposer;
import com.eu.habbo.messages.outgoing.handshake.ConnectionErrorComposer;

public class GuildForumThreadUpdateEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int guildId = this.packet.readInt();
        int threadId = this.packet.readInt();
        boolean isPinned = this.packet.readBoolean();
        boolean isLocked = this.packet.readBoolean();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);
        ForumThread thread = ForumThread.getById(threadId);

        if (guild == null || thread == null) {
            this.client.sendResponse(new ConnectionErrorComposer(404));
            return;
        }

        boolean isStaff = this.client.getHabbo().hasPermission("acc_modtool_ticket_q");

        GuildMember member = Emulator.getGameEnvironment().getGuildManager().getGuildMember(guildId, this.client.getHabbo().getHabboInfo().getId());
        if (member == null) {
            this.client.sendResponse(new ConnectionErrorComposer(401));
            return;
        }

        boolean isAdmin = (guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || member.getRank().type < GuildRank.MEMBER.type);

        if ((guild.canModForum() == SettingsState.OWNER && guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() && !isStaff) || (guild.canModForum() == SettingsState.ADMINS && !isAdmin && !isStaff)) {
            this.client.sendResponse(new ConnectionErrorComposer(403));
            return;
        }

        this.client.sendResponse(new ThreadUpdatedMessageComposer(guild, thread, this.client.getHabbo(), isPinned, isLocked));

        if (isPinned) {
            this.client.sendResponse(new GuildForumThreadsComposer(guild, 0));
        }
    }
}